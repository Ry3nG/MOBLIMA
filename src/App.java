import boundary.CustomerMenu;
import boundary.StaffMenu;
import entity.Menu;
import utils.Constants;

import java.util.Arrays;

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

    // Debug
    if (Arrays.stream(args).anyMatch(("--debug")::contains)) Constants.setDebugMode(true);

    // Default - Customer
    instance.currentMenu = CustomerMenu.getInstance();

    // Staff [--staff]
    if (args[0].equals("--staff")) instance.currentMenu = StaffMenu.getInstance();

    // Show menu
    instance.currentMenu.showMenu();
  }
}
