package main.control;

public class StaffHandler extends AccountHandler {

  private static StaffHandler instance = null;

  private StaffHandler() {
    super();
  }

  // singleton pattern design
  public static StaffHandler getInstance() {
    if (instance == null) {
      instance = new StaffHandler();
    }
    return instance;
  }

//  public boolean login(String username, String password) {
//    // return false;
//    return true;
//  }
}
