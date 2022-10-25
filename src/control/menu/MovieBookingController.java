package control.menu;

import boundary.BookingMenu;
import boundary.MovieMenu;
import control.handlers.BookingHandler;
import control.handlers.MovieHandler;
import entity.Showtime;
import tmdb.entities.Movie;
import utils.Helper;

import java.util.List;

public abstract class MovieBookingController {
  protected static MovieMenu movieMenu = MovieMenu.getInstance(true);
  protected static BookingMenu bookingMenu = BookingMenu.getInstance();

  protected void showMovieMenu(){
    this.movieMenu.showMenu();
  }

  protected void showBookingMenu(){
    this.bookingMenu.showMenu();
  }

  public MovieHandler movieHandler(){
    return this.movieMenu.getHandler();
  }

  public BookingHandler bookingHandler(){
    return this.bookingMenu.getHandler();
  }

  public int viewShowtimeAvailability(){
    int showtimeIdx = -1;

    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = this.movieMenu.selectMovieIdx();
    if (movieIdx < 0) return showtimeIdx;
    Movie selectedMovie = this.movieHandler().getSelectedMovie();
    Helper.logger("MovieBookingController.viewShowtimeAvailability", "Movie: " + selectedMovie);

    // Select showtimes for selected movie
    System.out.println("Select showtime slot: ");
    List<Showtime> movieShowtimes = this.bookingMenu.getHandler().getShowtimes(selectedMovie.getId());
    showtimeIdx = this.bookingMenu.selectShowtimeIdx(movieShowtimes);
    Helper.logger("MovieBookingController.viewShowtimeAvailability", "showtimeIdx: " + showtimeIdx);
    if (showtimeIdx < 0) return showtimeIdx;
    Showtime showtime = this.bookingMenu.getHandler().getShowtime(showtimeIdx);

    // Print showtime details
    this.bookingMenu.getHandler().printShowtimeDetails(showtimeIdx);
    this.bookingMenu.getHandler().printSeats(showtimeIdx);

    return showtimeIdx;
  }

  public void updateShowtimes(){
    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return;
    Movie selectedMovie = this.movieHandler().getSelectedMovie();

    // Select showtimes
    System.out.println("Select showtime slot: ");
    List<Showtime> movieShowtimes = bookingMenu.getHandler().getShowtimes(selectedMovie.getId());
    int showtimeIdx = bookingMenu.selectShowtimeIdx(movieShowtimes);
    if (showtimeIdx < 0) return;

    Showtime showtime = bookingMenu.getHandler().getShowtime(showtimeIdx);
    bookingMenu.editShowtime(showtime.getId());
  }

  public void updateMovies(){
    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = movieMenu.selectMovieIdx();
    if (movieIdx < 0) return;

    movieMenu.selectEditableAction(movieIdx);
  }

  public void updateCinemas(){
    // Cinema ID / IDX is the same
    int cinemaIdx = bookingMenu.selectCinemaIdx();
    if (cinemaIdx < 0) return;
    bookingMenu.editCinema(cinemaIdx);
  }
}
