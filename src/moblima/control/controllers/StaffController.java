package moblima.control.controllers;

import moblima.boundaries.MovieMenu;
import moblima.utils.Helper;

import java.util.LinkedHashMap;

/**
 * The type Staff controller.
 */
public class StaffController extends MovieBookingController {
  private static StaffController instance;

  private StaffController() {
    super();
    Helper.logger("MovieBookingController.StaffController", "Initialization");
    movieMenu = MovieMenu.getInstance(false, true);
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static StaffController getInstance() {
    if (instance == null)
      instance = new StaffController();
    return instance;
  }

  /**
   * Gets staff menu.
   *
   * @return the staff menu
   */
  public LinkedHashMap<String, Runnable> getStaffMenu() {
    return new LinkedHashMap<String, Runnable>() {{
      put("Top 5 movies by ticket sales", () -> printRankedMoviesByBooking(true));
      put("Top 5 movies by overall rating", () -> printRankedMoviesByRatings(true));
      put("Add movie", () -> movieMenu.createMovie());
      put("View and update movie details", () -> updateMovies());
      put("View and update showtimes", () -> updateShowtimes());
      put("View and update cinemas", () -> updateCinemas());
      put("View and update system settings", () -> updateSettings());
    }};
  }
}
