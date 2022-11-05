package control.controllers;

import boundaries.BookingMenu;
import boundaries.MovieMenu;
import boundaries.SettingsMenu;
import control.handlers.BookingHandler;
import control.handlers.ReviewHandler;
import control.handlers.SettingsHandler;
import entities.Booking;
import entities.Movie;
import entities.Showtime;
import utils.Helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.Helper.colorizer;

public abstract class MovieBookingController {
  protected static MovieMenu movieMenu;
  protected static BookingMenu bookingMenu;
  protected static SettingsMenu settingsMenu;

  public MovieBookingController() {
    Helper.logger("MovieBookingController", "Initialization");
    movieMenu = MovieMenu.getInstance();
    bookingMenu = BookingMenu.getInstance();
    settingsMenu = SettingsMenu.getInstance();
  }

  public ReviewHandler reviewHandler() {
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
   * Interactive showtime update
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

  public List<Movie> rankMoviesByBooking() {
    List<Movie> movies = reviewHandler().getMovies();
    List<Booking> bookings = bookingHandler().getBookings();
    if (bookings.isEmpty()) {
      System.out.println(colorizer("No bookings made yet", Helper.Preset.ERROR));
      return new ArrayList<Movie>();
    }

    Map<Integer, Long> bookedMovieIds = bookings.stream()
        .collect(Collectors.groupingBy(b -> b.getMovieId(), Collectors.counting()));
    Helper.logger("BookingHandler.sortBookingMovies", "bookedMovieIds: " + bookedMovieIds);

    List<Movie> rankedMovies = bookedMovieIds.entrySet().stream()
        .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
        .map(e -> reviewHandler().getMovie(reviewHandler().getMovieIdx(e.getKey())))
        .toList();
    Helper.logger("BookingHandler.sortBookingMovies", "rankedMovies: \n" + rankedMovies);


    return rankedMovies;
  }

  public List<Movie> rankMoviesByRatings() {
    List<Movie> movies = reviewHandler().getMovies();
    if (movies.isEmpty()) {
      System.out.println(colorizer("No movies available", Helper.Preset.ERROR));
      return movies;
    }

    List<Movie> rankedMovies = movies.stream()
        .sorted(Comparator.comparingDouble(Movie::getOverallRating).reversed())
        .limit(5)
        .collect(Collectors.toList());

    Helper.logger("BookingHandler.rankMoviesByRatings", "rankedMovies: \n" + rankedMovies);

    return rankedMovies;
  }
}
