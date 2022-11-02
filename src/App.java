import utils.Constants;
import utils.Helper;
import utils.datasource.HolidayDatasource;
import utils.datasource.MovieDatasource;

public class App {

  public static void main(String[] args) {

    Constants.setDebugMode(true);
    Helper.logger("App.main", "Initialization");

    MovieDatasource dsMovie = new MovieDatasource();
    // Movies
    Helper.logger("App.main", "Movies:" + dsMovie.getMovies().size());
    // Reviews
    Helper.logger("App.main", "Reviews: " + dsMovie.getReviews().size());

    HolidayDatasource dsHoliday = new HolidayDatasource();
    // Holidays
    Helper.logger("App.main", "Holidays: " + dsHoliday.getHolidays().size());
  }
}
