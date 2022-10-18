package main.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.entity.Account;
import org.apache.commons.codec.digest.DigestUtils;
import tmdb.datasource.Datasource;
import tmdb.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AccountHandler {
  protected List<Account> accounts;
  private final String dataFileName;

  public AccountHandler() {
    dataFileName = "accounts.csv";
    accounts = this.getAccounts();
  }

  public List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<Account>();

    String fileName = this.dataFileName;
    JsonArray accountList = Datasource.readArrayFromCsv(fileName);

    if (accountList == null) {
      Helper.logger("AccountHandler.getAccounts", "No serialized data available");
      return accounts;
    }

    for (JsonElement account : accountList) {
      JsonObject a = account.getAsJsonObject();

      String id = a.get("id").getAsString();
      String username = a.get("username").getAsString();
      String password = a.get("password").getAsString();

      /// Initialize and append Review object
      accounts.add(new Account(
          id,
          username,
          password
      ));
    }

    return accounts;
  }

  public boolean saveAccounts() {
    return Datasource.serializeData(this.accounts, this.dataFileName);
  }

  public boolean register(
      String username,
      String password
  ) {
    boolean isSuccess = false;

    // Create new Account
    Account account = new Account(
        UUID.randomUUID().toString(),
        username,
        DigestUtils.sha256Hex(password)
    );
    this.accounts.add(account);

    // Save account to text
    isSuccess = saveAccounts();

    return isSuccess;
  }

  public Account findAccount(
      String username
  ) {
    Account account = null;

    if (this.accounts.size() < 1) return account;

    for (Account a : accounts) {
      if (a.getUsername().equals(username)) {
        account = a;
        break;
      }
    }

    return account;
  }

  public boolean login(
      String username,
      String password
  ) {
    boolean status = false;

    // Find account by username
    Account account = this.findAccount(username);
    if (account == null) return status;

    // Match password
    if (DigestUtils.sha256Hex(password).equals(account.getPassword())) status = true;

    return status;
  }
}
