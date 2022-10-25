package control.menu;

import boundary.BookingMenu;
import boundary.MovieMenu;
import entity.Booking;
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

  public LinkedHashMap<String, Runnable> getCustomerMenu() {
    return new LinkedHashMap<String, Runnable>() {{
      put("Search/List Movies", () -> {
        List<Movie> movies = movieMenu.getViewableMovies();
        MovieMenu.getHandler().printMovies(movies);
      });
      put("View movie details – including reviews and ratings", movieMenu::showMenu);
    }};
  }

  public void viewBookings(String customerId) {
    bookingMenu.selectBookingIdx(customerId);
  }

  public int makeBooking(String customerId, Showtime showtime) {
    int bookingIdx = -1;
    int showtimeIdx = this.bookingHandler().getShowtimeIdx(showtime.getId());
    if (showtimeIdx < 0) return bookingIdx;

    // Select seats
    List<int[]> seats = bookingMenu.selectSeat(showtimeIdx);
    Helper.logger("CustomerMenu.makeBooking", "No. of seats: " + seats.size());
    Helper.logger("CustomerMenu.makeBooking", "Selected seats: " + Arrays.deepToString(seats.toArray()));
    if (seats.size() < 1) return bookingIdx;

    bookingIdx = BookingMenu.getHandler().addBooking(customerId, showtime.getCinemaId(), showtime.getMovieId(),
        showtime.getId(), seats, 10.0, Booking.TicketType.PEAK);

    return bookingIdx;
  }

}
