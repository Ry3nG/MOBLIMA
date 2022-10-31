package control.controllers;

import boundary.BookingMenu;
import boundary.MovieMenu;
import boundary.SettingsMenu;
import control.handlers.BookingHandler;
import control.handlers.MovieHandler;
import control.handlers.SettingsHandler;
import entity.Showtime;
import moblima.entities.Movie;
import utils.Helper;

import java.util.List;

public abstract class MovieBookingController {
  protected static MovieMenu movieMenu = MovieMenu.getInstance();
  protected static BookingMenu bookingMenu = BookingMenu.getInstance();
  protected static SettingsMenu settingsMenu = SettingsMenu.getInstance();

  public MovieHandler movieHandler() {
    return MovieMenu.getHandler();
  }

  public BookingHandler bookingHandler() {
    return BookingMenu.getHandler();
  }

  public SettingsHandler settingsHandler() {
    return settingsMenu.getHandler();
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
    List<Showtime> movieShowtimes = bookingHandler().getShowtimes(selectedMovie.getId());
    showtimeIdx = bookingMenu.selectShowtimeIdx(movieShowtimes);
    Helper.logger("MovieBookingController.viewShowtimeAvailability", "showtimeIdx: " + showtimeIdx);
    if (showtimeIdx < 0) return showtimeIdx;

    // Print showtime details
    bookingHandler().printShowtimeDetails(showtimeIdx);
    bookingHandler().printSeats(showtimeIdx);

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
    List<Showtime> movieShowtimes = bookingHandler().getShowtimes(selectedMovie.getId());
    int showtimeIdx = bookingMenu.selectShowtimeIdx(movieShowtimes);
    if (showtimeIdx < 0) return;

    Showtime showtime = bookingHandler().getShowtime(showtimeIdx);
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

  /**
   * System Settings update
   */
  //+ updateSettings():void
  public void updateSettings() {
    settingsMenu.showMenu();
  }
}
