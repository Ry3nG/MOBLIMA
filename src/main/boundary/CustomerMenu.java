package main.boundary;

import java.util.LinkedHashMap;

/**
 * Movie Goer's Menu
 *
 * @author SS11 Group 1
 * @version 1.0
 * @since 2022/10/11
 */
public class CustomerMenu extends Menu {
  private static final MovieMenu movieMenu = new MovieMenu();

  public CustomerMenu() {
    super();
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("1. Search/List Movies", movieMenu::listMovies);
      put("2. View movie details – including reviews and ratings", movieMenu::showMenu);
      put("3. Check seat availability and selection of seat/s.", () -> {
      });
      put("4. Book and purchase ticket", () -> {
      });
      put("5. View booking history", () -> {
      });
      put("6. List the Top 5 ranking by ticket sales OR by overall reviewers’ ratings", () -> {
      });
      put("7. Return to main menu", () -> System.out.println("\t>>> " + "Returning to main menu..."));
    }};
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }
}
