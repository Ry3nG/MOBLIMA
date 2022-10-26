package control.menu;

import boundary.MovieMenu;
import entity.Booking;
import entity.Cinema;
import entity.Showtime;
import moblima.entities.Movie;
import utils.Helper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class CustomerController extends MovieBookingController {
  private static CustomerController instance;

  private CustomerController() {
    super();
  }

  public static CustomerController getInstance() {
    if (instance == null)
      instance = new CustomerController();
    return instance;
  }

  //+ getCustomerMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getCustomerMenu() {
    return new LinkedHashMap<String, Runnable>() {{
      put("Search/List Movies", () -> {
        List<Movie> movies = movieMenu.getViewableMovies();
        MovieMenu.getHandler().printMovies(movies);
      });
      put("View movie details – including reviews and ratings", movieMenu::showMenu);
    }};
  }

  //+ viewBookings(customerId:String): void
  public void viewBookings(String customerId) {
    bookingMenu.selectBookingIdx(customerId);
  }

  //+ makeBooking(customerId:String, showtime:Showtime):int
  public int makeBooking(String customerId, Showtime showtime) {
    int bookingIdx = -1;
    int showtimeIdx = this.bookingHandler().getShowtimeIdx(showtime.getId());
    if (showtimeIdx < 0) return bookingIdx;

    // Get movie details
    int movieIdx = this.movieHandler().getMovieIdx(showtime.getMovieId());
    Movie movie = this.movieHandler().getMovie(movieIdx);
    if (movie == null) return bookingIdx;

    // Get cinema details
    Cinema cinema = this.bookingHandler().getCinema(showtime.getCinemaId());
    if (cinema == null) return bookingIdx;

    // Select seats
    List<int[]> seats = bookingMenu.selectSeat(showtimeIdx);
    Helper.logger("CustomerMenu.makeBooking", "No. of seats: " + seats.size());
    Helper.logger("CustomerMenu.makeBooking", "Selected seats: " + Arrays.deepToString(seats.toArray()));
    if (seats.size() < 1) return bookingIdx;

    // Compute total cost by multiplying num. of seats selected
    Booking.TicketType ticketType = Booking.TicketType.PEAK;
    double totalCost = this.priceHandler().computeTotalCost(
        movie.isBlockbuster(),
        cinema.getClassType(),
        ticketType,
        seats.size()
    );

    // Make booking
    bookingIdx = bookingHandler().addBooking(customerId, showtime.getCinemaId(), showtime.getMovieId(),
        showtime.getId(), seats, totalCost, ticketType);
    Booking booking = bookingHandler().getBooking(bookingIdx);
    bookingHandler().printBooking(booking.getTransactionId());

    return bookingIdx;
  }

}
