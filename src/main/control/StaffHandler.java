package main.control;

import main.entity.Account;

public class StaffHandler extends AccountHandler {
  private static StaffHandler instance = null;

  private StaffHandler() {
    super();
    this.dataFileName = "staff.csv";
    this.accounts = this.getAccounts(Account.Type.Staff);
  }

  public static StaffHandler getInstance() {
    if (instance == null) {
      instance = new StaffHandler();
    }
    return instance;
  }

  public boolean createAccount(String username, String password) {
    return register(username, password, Account.Type.Staff);
  }

  public boolean updateAccount(String username, String password) {
    boolean status = false;
    if (this.currentAccount == null) return status;

    status = update(new Account(this.currentAccount.getId(), username, password, this.currentAccount.getType()));

    return status;
  }

}
