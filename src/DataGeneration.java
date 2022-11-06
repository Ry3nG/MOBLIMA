import utils.Constants;
import utils.datasource.HolidayDatasource;
import utils.datasource.MovieDatasource;

public class DataGeneration {
  public static void main(String[] args) {
    Constants.setDebugMode(true);
    MovieDatasource dsMovie = new MovieDatasource();
    dsMovie.getMovies();
    dsMovie.getReviews();

    HolidayDatasource dsHoliday = new HolidayDatasource();
    dsHoliday.getHolidays();
  }
}
