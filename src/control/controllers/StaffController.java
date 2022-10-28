package control.controllers;

import boundary.MovieMenu;

import java.util.LinkedHashMap;

public class StaffController extends MovieBookingController {
  private static StaffController instance;

  private StaffController() {
    super();
    movieMenu = MovieMenu.getInstance(false, true);
  }

  public static StaffController getInstance() {
    if (instance == null)
      instance = new StaffController();
    return instance;
  }

  public LinkedHashMap<String, Runnable> getStaffMenu() {
    return new LinkedHashMap<String, Runnable>() {{
      put("View and update movie details", () -> updateMovies());
      put("View and update showtimes", () -> updateShowtimes());
      put("View and update cinemas", () -> updateCinemas());
      put("View and update system settings", () -> updateSettings());
    }};
  }
}
