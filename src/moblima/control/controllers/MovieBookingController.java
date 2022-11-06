package moblima.control.controllers;

import moblima.boundaries.BookingMenu;
import moblima.boundaries.MovieMenu;
import moblima.boundaries.SettingsMenu;
import moblima.control.handlers.BookingHandler;
import moblima.control.handlers.ReviewHandler;
import moblima.control.handlers.SettingsHandler;
import moblima.entities.Booking;
import moblima.entities.Movie;
import moblima.entities.Showtime;
import moblima.utils.Helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static moblima.utils.Helper.colorizer;

/**
 * The type Movie booking controller.
 */
public abstract class MovieBookingController {
  /**
   * The constant movieMenu.
   */
  protected static MovieMenu movieMenu;
  /**
   * The constant bookingMenu.
   */
  protected static BookingMenu bookingMenu;
  /**
   * The constant settingsMenu.
   */
  protected static SettingsMenu settingsMenu;

  /**
   * Instantiates a new Movie booking controller.
   */
  public MovieBookingController() {
    Helper.logger("MovieBookingController", "Initialization");
    movieMenu = MovieMenu.getInstance();
    bookingMenu = BookingMenu.getInstance();
    settingsMenu = SettingsMenu.getInstance();
  }

  /**
   * Review handler review handler.
   *
   * @return the review handler
   */
  public ReviewHandler reviewHandler() {
    return MovieMenu.getHandler();
  }

  /**
   * Booking handler booking handler.
   *
   * @return the booking handler
   */
  public BookingHandler bookingHandler() {
    return BookingMenu.getHandler();
  }

  /**
   * Sets handler.
   *
   * @return the handler
   */
  public SettingsHandler settingsHandler() {
    return settingsMenu.getHandler();
  }

  /**
   * View showtime availability int.
   *
   * @return the int
   */
//+ viewShowtimeAvailability(): int
  public int viewShowtimeAvailability() {
    int showtimeIdx = -1;

    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return showtimeIdx;
    Movie selectedMovie = this.reviewHandler().getSelectedMovie();
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
   * Update showtimes.
   */
//+ updateShowtimes():void
  public void updateShowtimes() {
    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return;
    Movie selectedMovie = this.reviewHandler().getSelectedMovie();

    // Select showtimes
    System.out.println("Select showtime slot: ");
    List<Showtime> movieShowtimes = bookingHandler().getShowtimes(selectedMovie.getId());
    int showtimeIdx = bookingMenu.selectShowtimeIdx(movieShowtimes);
    if (showtimeIdx < 0) return;

    Showtime showtime = bookingHandler().getShowtime(showtimeIdx);
    bookingMenu.editShowtime(showtime.getId());
  }

  /**
   * Update movies.
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
   * Update cinemas.
   */
//+ updateCinemas():void
  public void updateCinemas() {
    // Cinema ID / IDX is the same
    int cinemaIdx = bookingMenu.selectCinemaIdx();
    if (cinemaIdx < 0) return;
    bookingMenu.editCinema(cinemaIdx);
  }

  /**
   * Update settings.
   */
//+ updateSettings():void
  public void updateSettings() {
    settingsMenu.showMenu();
  }

  /**
   * Rank movies by booking list.
   *
   * @param maxRanking the max ranking
   * @return the list
   */
  public List<Movie> rankMoviesByBooking(int maxRanking) {
    List<Movie> movies = reviewHandler().getMovies();
    List<Booking> bookings = bookingHandler().getBookings();
    if (bookings.isEmpty()) {
      System.out.println(colorizer("No bookings made yet", Helper.Preset.ERROR));
      return new ArrayList<Movie>();
    }

    Map<Integer, Long> bookedMovieIds = bookings.stream().collect(Collectors.groupingBy(b -> b.getMovieId(), Collectors.counting()));
    Helper.logger("BookingHandler.sortBookingMovies", "bookedMovieIds: " + bookedMovieIds);

    List<Movie> rankedMovies = bookedMovieIds.entrySet().stream().sorted(Map.Entry.<Integer, Long>comparingByValue().reversed()).map(e -> reviewHandler().getMovie(reviewHandler().getMovieIdx(e.getKey()))).limit(maxRanking).toList();
    Helper.logger("BookingHandler.sortBookingMovies", "rankedMovies: \n" + rankedMovies);


    return rankedMovies;
  }

  /**
   * Rank movies by ratings list.
   *
   * @param maxRanking the max ranking
   * @return the list
   */
  public List<Movie> rankMoviesByRatings(int maxRanking) {
    List<Movie> movies = reviewHandler().getMovies();
    if (movies.isEmpty()) {
      System.out.println(colorizer("No movies available", Helper.Preset.ERROR));
      return movies;
    }

    List<Movie> rankedMovies = movies.stream().sorted(Comparator.comparingDouble(Movie::getOverallRating).reversed()).limit(maxRanking).collect(Collectors.toList());

    Helper.logger("BookingHandler.rankMoviesByRatings", "rankedMovies: \n" + rankedMovies);

    return rankedMovies;
  }
}
