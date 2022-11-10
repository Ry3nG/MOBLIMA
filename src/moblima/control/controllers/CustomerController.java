package moblima.control.controllers;

import moblima.entities.*;
import moblima.utils.Helper;
import moblima.utils.services.email.EmailService;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Customer controller.
 */
public class CustomerController extends MovieBookingController {
  private static CustomerController instance;

  private CustomerController() {
    super();
    Helper.logger("CustomerController", "Initialization");
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static CustomerController getInstance() {
    Helper.logger("CustomerController.getInstance", "Instance: " + instance);
    if (instance == null) instance = new CustomerController();
    return instance;
  }

  /**
   * Gets customer menu.
   *
   * @return the customer menu
   */
//+ getCustomerMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getCustomerMenu() {
    Helper.logger("CustomerContoller.getCustomerMenu", "Retrieving customer menu . . .");
    boolean authStatus = settingsHandler().checkIfIsAuthenticated();
    Helper.logger("CustomerContoller.getCustomerMenu", "authStatus: " + authStatus);

    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>() {{
      put(Settings.RankedType.MOVIES_BY_TICKETS.toString(), () -> {
        printRankedMoviesByBooking(false);
      });
      put(Settings.RankedType.MOVIES_BY_RATINGS.toString(), () -> {
        printRankedMoviesByRatings(false);
      });
      put("Search / View all movies", () -> {
        // Runnable injection if currently authenticated
        if (authStatus) {
          movieMenu.updateReviewMenu(() -> {
            Account currentAccount = settingsHandler().getCurrentAccount();
            String reviewerId = currentAccount.getId();
            String reviewerName = currentAccount.getName();
            movieMenu.selectReviewOptions(reviewHandler().getSelectedMovie().getId(), reviewerName, reviewerId);
          });
        }

        movieMenu.showMenu();
      });
    }};

    // Fetch ranked types
    Settings settings = settingsHandler().getCurrentSystemSettings();
    settings.getRankedTypes().entrySet().stream()
        .filter(t -> !t.getValue())
        .forEach(t -> menuMap.remove(t.getKey().toString()));

    // Auth-enable menu options
    if (!authStatus) return menuMap;
    menuMap.put("View and update reviews", () -> {
      String customerId = authStatus ? settingsHandler().getCurrentAccount().getId() : "";
      Helper.logger("CustomerContoller.getCustomerMenu", "customerId: " + customerId);

      int reviewIdx = -1;

      // Select reviews for authenticated account
      List<Review> customerReviews = reviewHandler().getUserReviews(customerId);
      reviewIdx = movieMenu.selectReviewIdx(customerReviews);
      Helper.logger("CustomerContoller.getCustomerMenu", "reviewIdx: " + reviewIdx);
      if (reviewIdx < 0) return;

      Review review = reviewHandler().getReview(reviewIdx);
      System.out.println(review.toString());

      movieMenu.selectUpdatableAction(review.getId());
    });


    return menuMap;
  }

  /**
   * View bookings.
   *
   * @param customerId the customer id
   */
//+ viewBookings(customerId:String): void
  public void viewBookings(String customerId) {
    bookingMenu.selectBookingIdx(customerId);
  }


  /**
   * Make booking int.
   *
   * @param customer the customer
   * @param showtime the showtime
   * @return the int
   */
//+ makeBooking(customerId:String, showtime:Showtime):int
  public int makeBooking(Customer customer, Showtime showtime) {
    int bookingIdx = -1;
    int showtimeIdx = this.bookingHandler().getShowtimeIdx(showtime.getId());
    if (showtimeIdx < 0) return bookingIdx;

    // Get movie details
    int movieIdx = this.reviewHandler().getMovieIdx(showtime.getMovieId());
    Movie movie = this.reviewHandler().getMovie(movieIdx);
    if (movie == null) return bookingIdx;

    Helper.logger("CustomerMenu.makeBooking", "Movie ShowStatus: " + movie.getShowStatus());
    /// Check if movie's show status is COMING_SOON
    if (movie.getShowStatus().equals(Movie.ShowStatus.COMING_SOON)) {
      System.out.println("Movie is not available for booking at present time.");
      return bookingIdx;
    }

    // Get cinema details
    Cinema cinema = this.bookingHandler().getCinema(showtime.getCinemaId());
    if (cinema == null) return bookingIdx;

    // Select seats
    List<int[]> seats = bookingMenu.selectSeat(showtimeIdx);
    Helper.logger("CustomerMenu.makeBooking", "No. of seats: " + seats.size());
    Helper.logger("CustomerMenu.makeBooking", "Selected seats: " + Arrays.deepToString(seats.toArray()));
    if (seats.size() < 1) return bookingIdx;

    // Select TicketType (only if not PEAK)
    EnumMap<Booking.TicketType, Double> ticketSurcharges = this.settingsHandler().getCurrentSystemSettings().getTicketSurcharges();
    Booking.TicketType ticketType = settingsHandler().verifyTicketType(showtime.getDatetime(), Booking.TicketType.NON_PEAK);

    List<Booking.TicketType> peakTickets = Arrays.asList(Booking.TicketType.PEAK, Booking.TicketType.SUPER_PEAK);
    if (!peakTickets.contains(ticketType)) {
      List<String> ticketOptions = Stream.of(Booking.TicketType.values()).filter(t -> !peakTickets.contains(t)).map(t -> {
        double estimatedCost = this.settingsHandler().computeTotalCost(movie.isBlockbuster(), showtime.getType(), cinema.getClassType(), t, showtime.getDatetime(), seats.size());

        return t + " - " + estimatedCost;
      }).collect(Collectors.toList());

      ticketType = bookingMenu.selectTicket(ticketOptions);
    }
    System.out.println("Ticket type: " + ticketType.toString());

    // Compute total cost by multiplying num. of seats selected
    double totalCost = this.settingsHandler().computeTotalCost(movie.isBlockbuster(), showtime.getType(), cinema.getClassType(), ticketType, showtime.getDatetime(), seats.size());
    Helper.logger("CustomerMenu.makeBooking", "Ticket type: " + ticketType + " - " + totalCost);


    // Make booking
    bookingIdx = bookingHandler().addBooking(customer.getId(), showtime.getCinemaId(), showtime.getMovieId(), showtime.getId(), seats, totalCost, ticketType);
    Booking booking = bookingHandler().getBooking(bookingIdx);
    String bookingDetails = bookingHandler().printBooking(booking.getTransactionId());

    new EmailService().sentBookingEmail(customer.getName(), customer.getEmailAddress(), bookingDetails);

    return bookingIdx;
  }

}
