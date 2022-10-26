package control.handlers;

import boundary.MovieMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entity.Booking;
import entity.Booking.TicketType;
import entity.Showtime;
import moblima.control.Datasource;
import org.apache.commons.lang3.EnumUtils;
import utils.Helper;
import utils.Helper.Preset;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;

public class BookingHandler extends CinemaHandler {
  private static final MovieHandler movieHandler = MovieMenu.getHandler();
  protected List<Booking> bookings;
  protected int selectedBookingIdx = -1;

  public BookingHandler() {
    super();
    bookings = this.getBookings();
  }

  /**
   * Set selected booking idx
   *
   * @param selectedBookingIdx:int
   */
  //+
  public void setSelectedBookingIdx(int selectedBookingIdx) {
    this.selectedBookingIdx = selectedBookingIdx;
  }

  /**
   * Get booking of specified booking idx
   *
   * @param bookingIdx:int
   * @return booking:Booking
   */
  //+ getBooking(bookingldx : int): Booking
  public Booking getBooking(int bookingIdx) {
    this.selectedBookingIdx = bookingIdx;
    return (bookingIdx < 0 || this.bookings.size() < 1) ? null : new Booking(this.bookings.get(bookingIdx));
  }

  /**
   * Get booking of specified transaction id
   *
   * @param transactionId:String
   * @return booking:Booking
   */
  //+getBooking(transactionld : String) : Booking
  public Booking getBooking(String transactionId) {
    Booking booking = null;
    if (this.bookings.size() < 1) return booking;

    for (Booking b : this.bookings) {
      if (b.getTransactionId().equals(transactionId)) {
        booking = new Booking(b);
        break;
      }
    }
    return booking;
  }

  /**
   * Deserializes and returns booking list
   *
   * @return bookings:List<Booking>
   */
  //+getBookings() : List<Booking>
  public List<Booking> getBookings() {
    List<Booking> bookings = new ArrayList<Booking>();

    if (this.showtimes.size() < 1 || this.cinemas.size() < 1) {
      System.out.println(colorize("No showtimes available to fulfil bookings", Preset.ERROR.color));
      return bookings;
    }

    //Source from serialized datasource
    String fileName = "bookings.csv";
    if (fileName == null || fileName.isEmpty()) {
      this.bookings = bookings;
      return bookings;
    }
    JsonArray bookingList = Datasource.readArrayFromCsv(fileName);

    if (bookingList == null) {
      Helper.logger("BookingHandler.getBookings", "No serialized data available");
      return bookings;
    }

    for (JsonElement booking : bookingList) {
      JsonObject b = booking.getAsJsonObject();

      String transactionId = b.get("transactionId").getAsString();
      String customerId = b.get("customerId").getAsString();
      int cinemaId = b.get("cinemaId").getAsInt();
      int movieId = b.get("movieId").getAsInt();
      String showtimeId = b.get("showtimeId").getAsString();
      double totalPrice = b.get("totalPrice").getAsDouble();

      /// Seats
      String seatsArr = b.get("seats").getAsString();
      Type seatsType = new TypeToken<List<int[]>>() {
      }.getType();
      List<int[]> seats = Datasource.getGson().fromJson(seatsArr, seatsType);

      String type = b.get("type").getAsString();
      boolean isValidType = EnumUtils.isValidEnum(TicketType.class, type);
      if (!isValidType) continue;
      TicketType ticketType = TicketType.valueOf(type);

      /// Initialize and append Account object
      bookings.add(new Booking(
          transactionId,
          customerId,
          cinemaId,
          movieId,
          showtimeId,
          seats,
          totalPrice,
          ticketType
      ));

      // Update showtimes
      int showtimeIdx = this.getShowtimeIdx(showtimeId);
      this.bulkAssignSeat(showtimeIdx, seats, true);
    }

    this.bookings = bookings;

    return bookings;
  }

  /**
   * Get booking list of specified customer id
   *
   * @param customerId:String
   * @return bookings:List<Booking>
   */
  //+ getBookings (customerld : String) : List<Booking>
  public List<Booking> getBookings(String customerId) {
    List<Booking> bookings = new ArrayList<Booking>();
    if (this.bookings.size() < 1) return bookings;

    for (Booking b : this.bookings) {
      if (b.getCustomerId().equals(customerId)) bookings.add(b);
    }
    return bookings;
  }

  /**
   * Append new booking to booking list
   *
   * @param customerId:String
   * @param cinemaId:int
   * @param movieId:int
   * @param showtimeId:String
   * @param seats:List<int[]>
   * @param totalPrice:double
   * @param type:TicketType
   * @return bookingIdx:int
   */
  //+addBooking(customerId:String, cinemaId:int, movieId:int, showtimeId:String, seats:List<int[]>, totalPrice:double, type:TicketType) : int
  public int addBooking(String customerId, int cinemaId, int movieId, String showtimeId, List<int[]> seats, double totalPrice, TicketType type) {
    List<Booking> bookings = new ArrayList<Booking>();
    if (this.bookings != null) bookings = this.bookings;

    Showtime showtime = this.getShowtime(showtimeId);

    // The TID is of the format XXXYYYYMMDDhhmm (Y : year, M : month, D : day, h : hour, m : minutes, XXX : cinema code in letters)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmm");
    String timestamp = (LocalDateTime.now()).format(formatter);
    String cineplexId = showtime.getCineplexId();
    String transactionId = cineplexId + timestamp;

    bookings.add(new Booking(transactionId, customerId, cinemaId, movieId, showtimeId, seats, totalPrice, type));
    this.bookings = bookings;

    // Finalize the seat selection
    this.bulkAssignSeat(selectedShowtimeIdx, seats, true);

    // Serialize bookings
    this.saveBookings();

    return this.bookings.size() - 1;
  }

  /**
   * Checks if specified showtime id has any associated bookings
   *
   * @param showtimeId:String
   * @return hasBooking:boolean
   */
  //+ checkIfShowtimeHasBooking(showtimeId:String):boolean
  public boolean checkIfShowtimeHasBooking(String showtimeId) {
    boolean hasBooking = false;
    if (showtimeId == null || this.bookings.isEmpty()) return false;

    for (Booking booking : this.bookings) {
      if (booking.getShowtimeId().equals(showtimeId)) {
        hasBooking = true;
        break;
      }
    }
    return hasBooking;
  }

  /**
   * Checks if specified cinema id has any associated bookings
   *
   * @param cinemaId:int
   * @return hasBooking:boolean
   */
  //+ checkIfCinemaHasBooking(cinemaId:int):boolean
  public boolean checkIfCinemaHasBooking(int cinemaId) {
    boolean hasBooking = false;
    if (cinemaId < 0 || this.bookings.isEmpty()) return false;

    for (Booking booking : this.bookings) {
      if (booking.getCinemaId() == cinemaId) {
        hasBooking = true;
        break;
      }
    }

    return hasBooking;
  }

  /**
   * Prints booking details along with its associated showtime and movie details
   *
   * @param transactionId:String
   */
  //+ printBooking(transactionld : String) : void
  public void printBooking(String transactionId) {
    Booking booking = this.getBooking(transactionId);
    if (booking == null) return;

    System.out.println("---------------------------------------------------------------------------");
    System.out.println(colorize("/// BOOKING DETAILS ///", Preset.HIGHLIGHT.color));
    System.out.println(colorize(booking.toString(), Preset.HIGHLIGHT.color));

    // Showtime
    int showtimeIdx = this.getShowtimeIdx(booking.getShowtimeId());
    this.printShowtimeDetails(showtimeIdx);

    // Movie
//    System.out.println(colorize("/// MOVIE DETAILS ///", Preset.HIGHLIGHT.color));
    int movieIdx = movieHandler.getMovieIdx(booking.getMovieId());
    movieHandler.printMovieDetails(movieIdx);
    System.out.println("---------------------------------------------------------------------------");
  }

  /**
   * Serializes booking data to CSV
   */
  //#
  protected boolean saveBookings() {
    return Datasource.serializeData(this.bookings, "bookings.csv");
  }

//  //+getSelectedBooking() : Booking
//  public Booking getSelectedBooking() {
//    return (this.selectedBookingIdx < 0 || this.bookings.size() < 1) ? null : this.bookings.get(this.selectedBookingIdx);
//  }

////+getBookingPrice(booking : Booking) : double
////+ updateBooking (booking : Booking) : boolean
////+ removeBooking (transactionld : String)
////boolean
////+ printBookings() : void
////+ printBookings(customerld : String):void
//  public void printBookings(String customerId){
//    List<Booking> bookings = this.getBookings(customerId);
//    Helper.logger("BookingHandler.printBookings", "Bookings: " + bookings);
//    if(bookings.size() < 1) return;
//
//    for(Booking booking : bookings) printBooking(booking.getTransactionId());
//  }
}
