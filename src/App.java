import boundary.CustomerMenu;
import boundary.Menu;
import utils.Constants;

public class App {
  private static App instance = null;
  private Menu currentMenu;

  private App() {

  }

  public static App getInstance() {
    if (instance == null) instance = new App();
    return instance;
  }

  public static void main(String[] args) {
    instance = App.getInstance();

    // Default - Customer
    instance.currentMenu = CustomerMenu.getInstance();

//    // Debug
//    if (Arrays.stream(args).anyMatch(("--debug")::contains)) {
      Constants.setDebugMode(true);
//      Helper.logger("App.main", "ARGS: " + Arrays.deepToString(args));
//      Helper.logger("App.main", "DEBUG MODE: " + Constants.DEBUG_MODE);
//    }
//
//    if (args.length > 0) {
//      // Staff [--staff]
//      if (args[0].equals("--staff")) instance.currentMenu = StaffMenu.getInstance();
//    }
//
//    // Show menu
//    Helper.figPrint("MOBLIMA");
    instance.currentMenu.showMenu();
  }
}
