package moblima;

import moblima.boundaries.CustomerMenu;
import moblima.boundaries.Menu;
import moblima.boundaries.StaffMenu;
import moblima.utils.Constants;
import moblima.utils.Helper;
import moblima.utils.datasource.HolidayDatasource;
import moblima.utils.datasource.MovieDatasource;

import java.util.Arrays;

import static java.lang.System.exit;

/**
 * Instantiation point of Movie Booking and Listing Management Application (MOBLIMA)
 *
 * @author SC2002 /SS11 Group 1
 * @version 1.0
 */
public class App {
  /**
   * Singleton instance of App
   */
  private static App instance = null;

  /**
   * Cache current Menu instance
   */
  private Menu currentMenu;

  /**
   * Retrieves instance of App
   *
   * @return instance :current App instance
   */
  public static App getInstance() {
    if (instance == null) instance = new App();
    return instance;
  }

  /**
   * MOBLIMA's point-of-entry
   *
   * @param args :mode execution flags<br/> Modes:<br/> > [DEFAULT] Customer<br/> > Staff (--staff)<br/> > [DEBUG] Customer (--debug)<br/> > [DEBUG] Staff (--staff --debug)<br/> > [DEBUG] Generate (--generate --debug)<br/>
   */
  public static void main(String[] args) {
    instance = App.getInstance();

    // Default - Customer
    instance.currentMenu = CustomerMenu.getInstance();

    // Debug
    if (Arrays.stream(args).anyMatch(("--debug")::contains)) {
      Constants.setDebugMode(true);
      Helper.logger("App.main", "ARGS: " + Arrays.deepToString(args));
      Helper.logger("App.main", "DEBUG MODE: " + Constants.DEBUG_MODE);
    }

    if (args.length > 0) {
      // Staff [--staff]
      if (args[0].equals("--staff")) instance.currentMenu = StaffMenu.getInstance();

      // Generate [--generate]
      if (args[0].equals("--generate")) generate();
    }

    // Show menu
    Helper.figPrint("MOBLIMA");
    instance.currentMenu.showMenu();

  }

  /**
   * Generate.
   */
  protected static void generate() {
    // Movies, Reviews
    MovieDatasource dsMovie = new MovieDatasource();
    dsMovie.getMovies();
    dsMovie.getReviews();

    // Holidays
    HolidayDatasource dsHoliday = new HolidayDatasource();
    dsHoliday.getHolidays();

    exit(0);
  }
}
