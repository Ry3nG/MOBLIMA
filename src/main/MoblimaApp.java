package main;

import main.boundary.CustomerMenu;
import main.boundary.Menu;
import main.boundary.StaffMenu;

/**
 * Console-based Movie Booking and Listing Management Application (MOBLIMA).
 * <p>
 * MOBLIMA can run in two modes - Customer mode or Staff mode.
 * <p>
 * Staff mode requires "--staff" as a command-line argument.
 *
 * @author SS11 Group 1
 * @version 1.0
 * @since 18 October 2022
 */
public class MoblimaApp {

  /**
   * For the singleton pattern implementation.
   */
  private static MoblimaApp _app = null;
  /**
   * May be CustomerMenu or StaffMenu, depending on the mode.
   */
  private Menu userMenu;

  /**
   * Private constructor, so instantiation outside of class only through getInstance()
   */
  private MoblimaApp() {
  }


  /**
   * Singleton pattern implementation for MoblimaApp
   *
   * @return instance of the MoblimaApp
   * @since 1.0
   */
  public static MoblimaApp getInstance() {
    if (_app == null) _app = new MoblimaApp();
    return _app;
  }

  /**
   * Main method, entry point of the application
   *
   * @param mode of run
   * @since 1.0
   */
  public static void main(String[] args) {
    MoblimaApp app = MoblimaApp.getInstance();

    // test for run-mode
    if (args.length == 0) app.userMenu = new CustomerMenu();
    else if (args[0].equals("--staff")) app.userMenu = new StaffMenu();

    app.userMenu.showMenu();
  }
}
