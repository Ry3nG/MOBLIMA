package moblima.control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import moblima.boundaries.MovieMenu;
import moblima.entities.Booking;
import moblima.entities.Booking.TicketType;
import moblima.entities.Showtime;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;
import moblima.utils.datasource.Datasource;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static moblima.utils.Helper.colorPrint;

/**
 * The type Booking handler.
 */
public class BookingHandler extends CinemaHandler {
  /**
   * The constant movieHandler.
   */
  protected static final ReviewHandler reviewHandler = MovieMenu.getHandler();
  /**
   * The Bookings.
   */
  protected List<Booking> bookings;
  /**
   * The Selected booking idx.
   */
  protected int selectedBookingIdx = -1;

  /**
   * Instantiates a new Booking handler.
   */
  public BookingHandler() {
    super();

    bookings = this.getBookings();

    Helper.logger("BookingHandler", "Cinema:\n" + this.cinemas);
    Helper.logger("BookingHandler", "Showtimes:\n" + this.showtimes);
    Helper.logger("BookingHandler", "Bookings:\n" + this.bookings);
  }

  /**
   * Sets selected booking idx.
   *
   * @param selectedBookingIdx the selected booking idx
   */
//+
  public void setSelectedBookingIdx(int selectedBookingIdx) {
    this.selectedBookingIdx = selectedBookingIdx;
  }

  /**
   * Gets booking.
   *
   * @param bookingIdx the booking idx
   * @return the booking
   */
//+ getBooking(bookingldx : int): Booking
  public Booking getBooking(int bookingIdx) {
    this.selectedBookingIdx = bookingIdx;
    return (bookingIdx < 0 || this.bookings.size() < 1) ? null : new Booking(this.bookings.get(bookingIdx));
  }

  /**
   * Gets booking.
   *
   * @param transactionId the transaction id
   * @return the booking
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
   * Gets bookings.
   *
   * @return the bookings
   */
//+getBookings() : List<Booking>
  public List<Booking> getBookings() {
    cinemas = this.getCinemas();
    showtimes = this.getShowtimes();
    List<Booking> bookings = new ArrayList<Booking>();

    if (this.showtimes.size() < 1 || this.cinemas.size() < 1) {
      colorPrint("No showtimes available to fulfil bookings", Preset.WARNING);
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
      bookings.add(new Booking(transactionId, customerId, cinemaId, movieId, showtimeId, seats, totalPrice, ticketType));

      // Update showtimes
      int showtimeIdx = this.getShowtimeIdx(showtimeId);
      this.bulkAssignSeat(showtimeIdx, seats, true);
    }

    this.bookings = bookings;

    return bookings;
  }

  /**
   * Gets bookings.
   *
   * @param customerId the customer id
   * @return the bookings
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
   * Add booking int.
   *
   * @param customerId the customer id
   * @param cinemaId   the cinema id
   * @param movieId    the movie id
   * @param showtimeId the showtime id
   * @param seats      the seats
   * @param totalPrice the total price
   * @param type       the type
   * @return the int
   */
//+addBooking(customerId:String, cinemaId:int, movieId:int, showtimeId:String, seats:List<int[]>, totalPrice:double, type:TicketType) : int
  public int addBooking(String customerId, int cinemaId, int movieId, String showtimeId, List<int[]> seats, double totalPrice, TicketType type) {
    List<Booking> bookings = new ArrayList<Booking>();
    if (this.bookings != null) bookings = this.bookings;

    Showtime showtime = this.getShowtime(showtimeId);

    // The TID is of the format XXXYYYYMMDDhhmm (Y : year, M : month, D : day, h : hour, m : minutes, XXX : cinema code in letters)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmm");
    String timestamp = (LocalDateTime.now()).format(formatter);
    String cineplexCode = this.getShowtimeCinema(showtimeId).getCineplexCode().toUpperCase();
    String transactionId = cineplexCode + timestamp;

    bookings.add(new Booking(transactionId, customerId, cinemaId, movieId, showtimeId, seats, totalPrice, type));
    this.bookings = bookings;

    // Finalize the seat selection
    this.bulkAssignSeat(selectedShowtimeIdx, seats, true);

    // Serialize bookings
    this.saveBookings();

    return this.bookings.size() - 1;
  }

  /**
   * Check if showtime has booking boolean.
   *
   * @param showtimeId the showtime id
   * @return the boolean
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
   * Check if cinema has booking boolean.
   *
   * @param cinemaId the cinema id
   * @return the boolean
   */
//+ checkIfCinemaHasBooking(cinemaId:int):boolean
  public boolean checkIfCinemaHasBooking(int cinemaId) {
    boolean hasBooking = false;
    if (cinemaId < 0 || this.bookings.isEmpty()) return false;

    for (Booking booking : this.bookings) {
      Showtime bookingShowtime = this.getShowtime(booking.getShowtimeId());
      boolean isActiveBooking = !bookingShowtime.getDatetime().isBefore(LocalDateTime.now());
      if (booking.getCinemaId() == cinemaId && isActiveBooking) {
        hasBooking = true;
        break;
      }
    }

    return hasBooking;
  }

  /**
   * Print booking.
   *
   * @param transactionId the transaction id
   * @return the string
   */
//+ printBooking(transactionld : String) : void
  public String printBooking(String transactionId) {
    Booking booking = this.getBooking(transactionId);
    if (booking == null) return "";

    String header = "\n/// BOOKING DETAILS ///";
    System.out.println("---------------------------------------------------------------------------");
    colorPrint(header, Preset.HIGHLIGHT);
    colorPrint(booking.toString(), Preset.HIGHLIGHT);

    // Showtime
    int showtimeIdx = this.getShowtimeIdx(booking.getShowtimeId());
    String showtimeDetails = this.printShowtimeDetails(showtimeIdx);

    // Movie
    int movieIdx = reviewHandler.getMovieIdx(booking.getMovieId());
    String movieDetails = reviewHandler.printMovieDetails(movieIdx, true);
    System.out.println("---------------------------------------------------------------------------");

    return header + "\n" + booking + "\n" + showtimeDetails + "\n" + movieDetails;
  }

  /**
   * Save bookings boolean.
   *
   * @return the boolean
   */
//#
  protected boolean saveBookings() {
    return Datasource.serializeData(this.bookings, "bookings.csv");
  }

}
