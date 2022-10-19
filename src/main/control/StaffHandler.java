/*
RENAME TO LIKE STAFFLOGIN
*/
package main.control;

import main.entity.SystemSettings;

import java.util.HashMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.entity.Account;
import main.entity.Staff;
import org.apache.commons.lang3.EnumUtils;
import tmdb.datasource.Datasource;
import tmdb.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class StaffHandler extends AccountHandler {
  private static StaffHandler instance = null;
  private List<Staff> staffList;

  private StaffHandler() {
    super();
    this.dataFileName = "staff.csv";
    this.getAccounts();
  }

  public static StaffHandler getInstance() {
    if (instance == null) {
      instance = new StaffHandler();
    }
    return instance;
  }

  public boolean saveAccounts() {
    return Datasource.serializeData(this.staffList, this.dataFileName);
  }

  public HashMap<String,Object> retrieveSettings() {
    SystemSettings settings = SystemSettings.getInstance();
    HashMap<String,Object> mapping = new HashMap<String,Object>();
    mapping.put("standardTix", settings.getStandardTix());
    mapping.put("studentTix", settings.getStudentTix());
    mapping.put("seniorTix", settings.getSeniorTix());
    mapping.put("holidays", settings.getHolidays().toArray());

    return mapping;
  }
  public List<Staff> getStaffList() {
    return this.staffList;
  }

  public List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<Account>();
    List<Staff> staffs = new ArrayList<Staff>();

    String fileName = this.dataFileName;
    if (fileName == null || fileName.isEmpty()) {
      this.accounts = accounts;
      this.staffList = staffs;
      return accounts;
    }
    JsonArray accountList = Datasource.readArrayFromCsv(fileName);

    if (accountList == null) {
      Helper.logger("StaffHandler.getAccounts", "No serialized data available");
      return accounts;
    }

    for (JsonElement account : accountList) {
      JsonObject a = account.getAsJsonObject();

      String id = a.get("id").getAsString();
      String username = a.get("username").getAsString();
      String password = a.get("password").getAsString();

      String accountType = a.get("type").getAsString();
      boolean isValidType = EnumUtils.isValidEnum(Account.Type.class, accountType);
      if (!isValidType) continue;
      Account.Type type = Account.Type.valueOf(accountType);

      String name = a.get("name").getAsString();
      String cinemaCode = a.get("cinemaCode").getAsString();

      /// Initialize and append Account object
      Staff staff = new Staff(
          id,
          username,
          password,
          type,
          name,
          cinemaCode
      );
      staffs.add(staff);
      accounts.add(staff);
    }

    this.accounts = accounts;
    this.staffList = staffs;

    return accounts;
  }

  public String createStaffAccount(
      String username,
      String password,
      String name,
      String cinemaCode
  ) {

    // Initialize new Account
    String accountId = register(username, password, Account.Type.Staff);
    if (accountId == null) return accountId;
    int accountIdx = this.findAccountIdx(accountId);
    Account account = !isAdmin() ? this.currentAccount : this.accounts.get(accountIdx);

    // Initialize new Staff
    Staff staff = new Staff(
        account.getId(),
        account.getUsername(),
        account.getPassword(),
        account.getType(),
        name,
        cinemaCode
    );

    this.staffList.add(staff);
    this.saveAccounts();

    return accountId;
  }

  public boolean updateAccount(String username, String password) {
    boolean status = false;
    if (this.currentAccount == null) return status;

    status = update(new Account(this.currentAccount.getId(), username, password, this.currentAccount.getType()));

    return status;
  }

}
