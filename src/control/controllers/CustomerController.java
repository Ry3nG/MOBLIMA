package control.controllers;

import entities.*;
import utils.Helper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class CustomerController extends MovieBookingController {
  private static CustomerController instance;

  private CustomerController() {
    super();
    Helper.logger("CustomerController", "Initialization");
  }

  public static CustomerController getInstance() {
    Helper.logger("CustomerController.getInstance", "Instance: " + instance);
    if (instance == null)
      instance = new CustomerController();
    return instance;
  }

  //+ getCustomerMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getCustomerMenu() {
    Helper.logger("CustomerContoller.getCustomerMenu", "Retrieving customer menu . . .");
    boolean authStatus = settingsHandler().checkIfIsAuthenticated();
    Helper.logger("CustomerContoller.getCustomerMenu", "authStatus: " + authStatus);

    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>() {{
//      put("Search/List Movies", () -> {
//        List<Movie> movies = movieMenu.getViewableMovies();
//        reviewHandler().printMovies(movies);
//      });
      put("Top 5 movies by ticket sales", () -> {
        List<Movie> rankedMovies = rankMoviesByBooking();
        reviewHandler().printMovies(rankedMovies);
      });
      put("Top 5 movies by overall rating", () -> {
        List<Movie> rankedMovies = rankMoviesByRatings();
        reviewHandler().printMovies(rankedMovies);
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


//        int showtimeIdx = -1;
//
//        // Select movie
//        System.out.println("Select movie: ");
//        int movieIdx = movieMenu.selectMovieIdx();
//        if (movieIdx < 0) return showtimeIdx;
//        Movie selectedMovie = this.reviewHandler().getSelectedMovie();
//        Helper.logger("MovieBookingController.viewShowtimeAvailability", "Movie: " + selectedMovie);
//
//

    });


    return menuMap;
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
    int movieIdx = this.reviewHandler().getMovieIdx(showtime.getMovieId());
    Movie movie = this.reviewHandler().getMovie(movieIdx);
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
    Booking.TicketType ticketType = settingsHandler().verifyTicketType(showtime.getDatetime(), Booking.TicketType.NON_PEAK);
    double totalCost = this.settingsHandler().computeTotalCost(
        movie.isBlockbuster(),
        cinema.getClassType(),
        ticketType,
        showtime.getDatetime(),
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
