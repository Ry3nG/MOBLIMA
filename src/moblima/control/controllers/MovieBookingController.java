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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static moblima.utils.Helper.colorizer;
import static moblima.utils.Helper.formatAsTable;

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

    /// Check if movie's show status is COMING_SOON = no showtimes allowed
    if (selectedMovie.getShowStatus().equals(Movie.ShowStatus.COMING_SOON)) {
      System.out.println("Movie is not available for booking at present time.");
      return showtimeIdx;
    }

    // Select showtimes for selected movie
    System.out.println("Select showtime slot: ");
    int movieId = selectedMovie.getId();
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
    int movieId = selectedMovie.getId();
    List<Showtime> movieShowtimes = bookingHandler().getShowtimes(selectedMovie.getId());
    int showtimeIdx = bookingMenu.selectMovieShowtimeIdx(movieId, movieShowtimes, true);
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
  public LinkedHashMap<Movie, Integer> rankMoviesByBooking(int maxRanking) {
    List<Movie> movies = reviewHandler().getMovies();
    List<Booking> bookings = bookingHandler().getBookings();
    if (bookings.isEmpty()) return new LinkedHashMap<>();

    Map<Integer, Long> bookedMovieIds = bookings.stream().collect(Collectors.groupingBy(b -> b.getMovieId(), Collectors.counting()));
    Helper.logger("BookingHandler.sortBookingMovies", "bookedMovieIds: " + bookedMovieIds);

//    List<Movie> rankedMovies = bookedMovieIds.entrySet().stream().sorted(Map.Entry.<Integer, Long>comparingByValue().reversed()).map(e -> reviewHandler().getMovie(reviewHandler().getMovieIdx(e.getKey()))).limit(maxRanking).toList();
//    Helper.logger("BookingHandler.sortBookingMovies", "rankedMovies: \n" + rankedMovies);

    LinkedHashMap<Movie, Integer> rankedMovies = new LinkedHashMap<Movie, Integer>();
    bookedMovieIds.entrySet().stream()
        .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
        .limit(maxRanking)
        .forEach(e -> rankedMovies.put(reviewHandler().getMovie(reviewHandler().getMovieIdx(e.getKey())), Math.toIntExact(e.getValue())));

//    List<String> strRankedMovies = rankedMovies.entrySet().stream()
//        .map(e -> e.getKey().getTitle() + " - " + e.getValue())
//        .collect(Collectors.toList());
//    Helper.logger("BookingHandler.sortBookingMovies", "strRankedMovies: \n" + strRankedMovies);


    return rankedMovies;
  }

  /**
   * Print ranked movies by booking.
   *
   * @param showBookingCount the show booking count
   */
  public void printRankedMoviesByBooking(boolean showBookingCount) {
    LinkedHashMap<Movie, Integer> rankedMovies = this.rankMoviesByBooking(5);
    if (rankedMovies.size() < 1) {
      System.out.println(colorizer("No bookings made yet", Helper.Preset.ERROR));
      return;
    }

    List<List<String>> strRankedMovies = IntStream.range(0, rankedMovies.size())
        .mapToObj(idx -> {
          String labelStart = "> " + (idx + 1) + ". " + ((new ArrayList<Movie>(rankedMovies.keySet())).get(idx)).getTitle();
          String labelEnd = showBookingCount ? " | Sales: " + (new ArrayList<Integer>(rankedMovies.values()).get(idx)) : "";

          return Arrays.asList(labelStart, labelEnd);
        })
        .collect(Collectors.toList());

    String output = formatAsTable(strRankedMovies);
    Helper.logger("BookingHandler.printRankedMoviesByBooking", "strRankedMovies: \n" + output);
    System.out.println(output);
  }

  /**
   * Rank movies by ratings list.
   *
   * @param maxRanking the max ranking
   * @return the list
   */
  public List<Movie> rankMoviesByRatings(int maxRanking) {
    List<Movie> movies = reviewHandler().getMovies();
    if (movies.isEmpty()) return movies;

    List<Movie> rankedMovies = movies.stream()
        .sorted(Comparator.comparingDouble(Movie::getOverallRating).reversed())
        .limit(maxRanking)
        .collect(Collectors.toList());
    Helper.logger("BookingHandler.rankMoviesByRatings", "rankedMovies: \n" + rankedMovies);

    return rankedMovies;
  }

  /**
   * Print ranked movies by ratings.
   *
   * @param showRatingCount the show rating count
   */
  public void printRankedMoviesByRatings(boolean showRatingCount) {
    List<Movie> rankedMovies = this.rankMoviesByRatings(5);
    if (rankedMovies.size() < 1) {
      System.out.println(colorizer("No movies available", Helper.Preset.ERROR));
      return;
    }

    List<List<String>> strRankedMovies = IntStream.range(0, rankedMovies.size())
        .mapToObj(idx -> {
          Movie m = rankedMovies.get(idx);
          String labelStart = "> " + (idx + 1) + ". " + (m.getTitle());
          String labelEnd = showRatingCount ? " | Rating: " + m.getOverallRating() : "";

          return Arrays.asList(labelStart, labelEnd);
        })
        .collect(Collectors.toList());

    String output = formatAsTable(strRankedMovies);
    Helper.logger("BookingHandler.printRankedMoviesByRatings", "strRankedMovies: \n" + output);
    System.out.println(output);
  }
}
