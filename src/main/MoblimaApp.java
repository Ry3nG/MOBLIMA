package main;

import main.boundary.CustomerMenu;
import main.boundary.Menu;
import main.boundary.StaffMenu;

import java.util.LinkedHashMap;

import static java.lang.System.exit;

public class MoblimaApp extends Menu {
  // Console-based Movie Booking and Listing Management Application (MOBLIMA)
  private static MoblimaApp _app = null;
  private static CustomerMenu customerMenu;
  private static StaffMenu staffMenu;

  private MoblimaApp() {
    super();
    customerMenu = new CustomerMenu();
    staffMenu = new StaffMenu();
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("1. I'm a Movie-goer", () -> customerMenu.showMenu());
      put("2. I'm a Staff", () -> staffMenu.showMenu());
      put("3. Exit", () -> {
        System.out.println("\t>>> " + "Exiting application...");
        scanner.close();
        exit(0);
      });
    }};
  }

  public static MoblimaApp getInstance() {
    if (_app == null) _app = new MoblimaApp();
    return _app;
  }

  public static void main(String[] args) {
    MoblimaApp app = MoblimaApp.getInstance();
    app.showMenu();
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }
}
