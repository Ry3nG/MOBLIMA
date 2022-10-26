package control.menu;

import boundary.BookingMenu;
import boundary.MovieMenu;
import control.handlers.BookingHandler;
import control.handlers.MovieHandler;
import control.handlers.PriceHandler;
import entity.Showtime;
import moblima.entities.Movie;
import utils.Helper;

import java.util.List;

public abstract class MovieBookingController {
  protected static MovieMenu movieMenu = MovieMenu.getInstance(true);
  protected static BookingMenu bookingMenu = BookingMenu.getInstance();
  protected static PriceHandler priceHandler = PriceHandler.getInstance();

  public MovieHandler movieHandler() {
    return MovieMenu.getHandler();
  }

  public BookingHandler bookingHandler() {
    return BookingMenu.getHandler();
  }

  public PriceHandler priceHandler() {
    return priceHandler;
  }

  /**
   * Interactive showtime display
   *
   * @return showtimeIdx:int
   */
  //+ viewShowtimeAvailability(): int
  public int viewShowtimeAvailability() {
    int showtimeIdx = -1;

    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return showtimeIdx;
    Movie selectedMovie = this.movieHandler().getSelectedMovie();
    Helper.logger("MovieBookingController.viewShowtimeAvailability", "Movie: " + selectedMovie);

    // Select showtimes for selected movie
    System.out.println("Select showtime slot: ");
    List<Showtime> movieShowtimes = BookingMenu.getHandler().getShowtimes(selectedMovie.getId());
    showtimeIdx = bookingMenu.selectShowtimeIdx(movieShowtimes);
    Helper.logger("MovieBookingController.viewShowtimeAvailability", "showtimeIdx: " + showtimeIdx);
    if (showtimeIdx < 0) return showtimeIdx;

    // Print showtime details
    BookingMenu.getHandler().printShowtimeDetails(showtimeIdx);
    BookingMenu.getHandler().printSeats(showtimeIdx);

    return showtimeIdx;
  }

  /**
   * Interactive showtime update
   */
  //+ updateShowtimes():void
  public void updateShowtimes() {
    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return;
    Movie selectedMovie = this.movieHandler().getSelectedMovie();

    // Select showtimes
    System.out.println("Select showtime slot: ");
    List<Showtime> movieShowtimes = BookingMenu.getHandler().getShowtimes(selectedMovie.getId());
    int showtimeIdx = bookingMenu.selectShowtimeIdx(movieShowtimes);
    if (showtimeIdx < 0) return;

    Showtime showtime = BookingMenu.getHandler().getShowtime(showtimeIdx);
    bookingMenu.editShowtime(showtime.getId());
  }

  /**
   * Interactive movie update
   */
  //+ updateMovies():void
  public void updateMovies() {
    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return;

    movieMenu.selectEditableAction(movieIdx);
  }

  /**
   * Interactive cinema update
   */
  //+ updateCinemas():void
  public void updateCinemas() {
    // Cinema ID / IDX is the same
    int cinemaIdx = bookingMenu.selectCinemaIdx();
    if (cinemaIdx < 0) return;
    bookingMenu.editCinema(cinemaIdx);
  }

}
