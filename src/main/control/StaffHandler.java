package main.control;

public class StaffHandler {

  private static StaffHandler instance = null;

  // private constructor
  private StaffHandler() {
  }

  // singleton pattern design
  public static StaffHandler getInstance() {
      if (instance == null) {
          instance = new StaffHandler();
      }
      return instance;
  }

  public boolean login(String username, String password) {
    // return false;
    return true;
  }
}
