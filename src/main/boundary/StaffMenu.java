package main.boundary;

import main.control.StaffHandler;

import java.util.LinkedHashMap;
import java.io.Console;

/**
 * Staff's Menu
 *
 * @author SS11 Group 1
 * @version 1.0
 * @since 11 October 2022
 */
public class StaffMenu extends Menu {

  /**
   * State of staff - whether he/she is logged in or not
   */
  private static boolean loggedIn = false;

  /**
   * Constructor for StaffMenu
   * 
   * The actual menu is created within this constructor.
   * @since 1.0
   */
  public StaffMenu() {
    super();
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("Create / Update / Remove movie listing", () -> {
      });
      put("Create / Update / Remove cinema showtimes and the movies to be shown", () -> {
      });
      put("Configure system settings", () -> {
        // SettingsMenu settingsMenu = new SettingsMenu();
        // settingsMenu.showMenu();
      });
      put("Log out / Exit", () -> {
        System.out.println("\t>>> Logging out...");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Goodbye!");}
      );
    }};
  }

  /**
   * Require the Staff to login before displaying the menu
   * 
   * @see main.boundary.Menu#showMenu()
   * @since 1.0
   */
  @Override
  public void showMenu() {
    
    while (!loggedIn) {
      loggedIn = staffLogin();
    }

    this.displayMenu();
  }

  /**
   * Requests for the username and password, and attempts the login.
   * 
   * @return whether the login is successful or not
   * @since 1.0
   */
  private boolean staffLogin() {
    
    Console console = System.console();
    String username = "";
    String password = "";
    
    System.out.println("---------------------------------------------------------------------------");
    if (console == null) { // running in IDE
      System.out.println("Username: ");
      username = scanner.nextLine();
      System.out.println("Password: ");
      password = scanner.nextLine();
    } else { // running via console - password input hidden
      username = console.readLine("Username: ");
      password = new String(console.readPassword("Password: "));
    }
    
    System.out.println("\t>>> Logging in...");

    boolean success = StaffHandler.getInstance().login(username,password);

    System.out.println("---------------------------------------------------------------------------");
    if (success) System.out.println("SUCCESSFUL. Welcome back!");
    else System.out.println("FAILED. Please try again!");

    return success;
  }

}