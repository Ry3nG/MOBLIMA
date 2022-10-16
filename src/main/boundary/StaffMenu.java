package main.boundary;

import java.util.LinkedHashMap;

/**
 * Staff's Menu
 *
 * @author SS11 Group 1
 * @version 1.0
 * @since 2022/10/11
 */
public class StaffMenu extends Menu {

  public StaffMenu() {
    super();
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("1. Login", () -> {
      });
      put("2. Create/Update/Remove movie listing", () -> {
      });
      put("3. Create/Update/Remove cinema showtimes and the movies to be shown", () -> {
      });
      put("4. Configure system settings", () -> {
      });
      put("5. Return to main menu", () -> System.out.println("\t>>> " + "Returning to main menu..."));
    }};
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

}
