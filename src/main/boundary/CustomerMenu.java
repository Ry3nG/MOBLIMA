package main.boundary;

import java.util.LinkedHashMap;

/**
 * Movie Goer's Menu
 *
 * @author SS11 Group 1
 * @version 1.0
 * @since 11 October 2022
 */
public class CustomerMenu extends Menu {

  /**
   * Obtain an instance of MovieMenu to use methods in menu related to Movies
   */
  private static final MovieMenu movieMenu = new MovieMenu();

  /**
   * Constructor for CustomerMenu
   * <p>
   * The actual menu is created within this constructor.
   *
   * @since 1.0
   */
  public CustomerMenu() {
    super();
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("Search/List Movies", movieMenu::listMovies);
      put("View movie details – including reviews and ratings", movieMenu::showMenu);
      put("Check seat availability and selection of seat/s.", () -> {
      });
      put("Book and purchase ticket", () -> {
      });
      put("View booking history", () -> {
      });
      put("List the Top 5 ranking by ticket sales OR by overall reviewers’ ratings", () -> {
      });
      put("Exit", () -> {
        System.out.println("\t>>> Quitting application...");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Thank you for using MOBLIMA. We hope to see you again soon!");
        System.exit(0);
      });
    }};
  }

  /**
   * Display the menu
   *
   * @see main.boundary.Menu#showMenu()
   * @since 11 October 2022
   */
  @Override
  public void showMenu() {
    this.displayMenu();
  }
}
