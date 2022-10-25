package control.menu;

import boundary.MovieMenu;

import java.util.LinkedHashMap;

public class StaffController extends MovieBookingController {
  private static StaffController instance;

  private StaffController() {
    super();
    movieMenu = MovieMenu.getInstance(false);
  }

  public static StaffController getInstance() {
    if (instance == null)
      instance = new StaffController();
    return instance;
  }

  public LinkedHashMap<String, Runnable> getStaffMenu() {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>() {{
      put("View and update movie details", () -> {
        updateMovies();
      });
      put("View and update showtimes", () -> {
        updateShowtimes();
      });
      put("View and update cinemas", () -> {
        updateCinemas();
      });
    }};

    return menuMap;
  }
}
