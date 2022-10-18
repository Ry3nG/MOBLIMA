package main.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.entity.Account;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.EnumUtils;
import tmdb.datasource.Datasource;
import tmdb.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AccountHandler {
  protected String dataFileName;
  protected Account currentAccount;
  protected List<Account> accounts;

  public AccountHandler() {
    this.dataFileName = "accounts.csv";
    this.currentAccount = null;
    this.accounts = this.getAccounts();
  }

  public List<Account> getAccounts(
      Account.Type type
  ) {
    List<Account> accounts = this.getAccounts();

    for (int i = 0; i < accounts.size(); i++) {
      Account a = accounts.get(i);
      if (a.getType().equals(type)) continue;
      accounts.remove(i);
    }

    return accounts;
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

      String accountType = a.get("type").getAsString();
      boolean isValidType = EnumUtils.isValidEnum(Account.Type.class, accountType);
      if (!isValidType) continue;
      Account.Type type = Account.Type.valueOf(accountType);

      /// Initialize and append Account object
      accounts.add(new Account(
          id,
          username,
          password,
          type
      ));
    }

    return accounts;
  }

  public boolean saveAccounts() {
    return Datasource.serializeData(this.accounts, this.dataFileName);
  }

  public boolean register(
      String username,
      String password,
      Account.Type type
  ) {
    boolean isSuccess = false;

    // Create new Account
    Account account = new Account(
        UUID.randomUUID().toString(),
        username,
        DigestUtils.sha256Hex(password),
        type
    );
    this.accounts.add(account);

    // Save accounts
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

  public int findAccountIdx(
      String id
  ) {
    int idx = -1;

    if (this.accounts.size() < 1) return idx;

    for (int i = 0; i < this.accounts.size(); i++) {
      if (this.accounts.get(i).getId().equals(id)) {
        idx = i;
        break;
      }
    }

    return idx;
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

    this.currentAccount = account;
    return status;
  }

  public boolean logout() {
    this.currentAccount = null;
    return saveAccounts();
  }

  public boolean update(
      Account account
  ) {
    boolean status = false;

    // Find account idx in list of accounts
    int idx = findAccountIdx(account.getId());
    if (idx < 0) return status;

    // Save accounts
    this.accounts.set(idx, account);
    this.currentAccount = account;
    status = saveAccounts();

    return status;
  }

  public boolean delete(
      String id
  ) {
    boolean status = false;

    // Find account idx in list of accounts
    int idx = findAccountIdx(id);
    if (idx < 0) return status;

    // Save accounts
    this.accounts.remove(idx);
    status = saveAccounts();

    return status;
  }
}
