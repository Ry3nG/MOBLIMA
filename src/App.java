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


    // Default - Customer
    instance.currentMenu = CustomerMenu.getInstance();

    if(args.length > 0){
      // Debug
      if (Arrays.stream(args).anyMatch(("--debug")::contains)) Constants.setDebugMode(true);

        // Staff [--staff]
      else if (args[0].equals("--staff")) instance.currentMenu = StaffMenu.getInstance();
    }

    // Show menu
    instance.currentMenu.showMenu();
  }
}
