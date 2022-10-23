package control;

import boundary.MovieMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entity.Booking;
import entity.Booking.TicketType;
import org.apache.commons.lang3.EnumUtils;
import tmdb.control.Datasource;
import utils.Helper;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookingHandler extends CinemaHandler {
  protected List<Booking> bookings;
  protected int selectedBookingIdx = -1;

  private static final MovieHandler movieHandler = MovieMenu.getHandler();

  // private PriceHandler priceHandler

  public BookingHandler() {
    super();
    bookings = this.getBookings();
  }

  //+ setSelectedBookingIdx(selectedBookingIdx:int) : void
  public void setSelectedBookingIdx(int selectedBookingIdx){
    this.selectedBookingIdx = selectedBookingIdx;
  }

  //+getSelectedBooking() : Booking
  public Booking getSelectedBooking() {
    return (this.selectedBookingIdx < 0 || this.bookings.size() < 1) ? null : this.bookings.get(this.selectedBookingIdx);
  }

  //+ getBooking(bookingldx : int) Booking
  public Booking getBooking(int bookingIdx) {
    this.selectedBookingIdx = bookingIdx;
    return (bookingIdx < 0 || this.bookings.size() < 1) ? null : this.bookings.get(bookingIdx);
  }

  //+getBooking(transactionld : String) : Booking
  public Booking getBooking(String transactionId) {
    Booking booking = null;
    if (this.bookings.size() < 1) return booking;

    for (Booking b : this.bookings) {
      if (b.getTransactionId().equals(transactionId)) {
        booking = b;
        break;
      }
    }
    return booking;
  }

  //+getBookings() : List<Booking>
  public List<Booking> getBookings() {
    List<Booking> bookings = new ArrayList<Booking>();

    if(this.showtimes.size() < 1 || this.cinemas.size() < 1){
      System.out.println("No showtimes available to fulfil bookings");
      return bookings;
    }

    //TODO: Source from serialized datasource
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

  //+ getBookings (customerld : String) : List<Booking >
  public List<Booking> getBookings(String customerId) {
    List<Booking> bookings = new ArrayList<Booking>();
    if (this.bookings.size() < 1) return bookings;

    for (Booking b : this.bookings) {
      if (b.getCustomerId().equals(customerId)) bookings.add(b);
    }
    return bookings;
  }


  //+addBooking(booking : Booking) : int
  public int addBooking(String customerId, int cinemaId, int movieId, String showtimeId, List<int[]> seats, double totalPrice, TicketType type) {
    List<Booking> bookings = new ArrayList<Booking>();
    if(this.bookings != null) bookings = this.bookings;

    // The TID is of the format XXXYYYYMMDDhhmm (Y : year, M : month, D : day, h : hour, m : minutes, XXX : cinema code in letters)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMDDhhmm");
    String timestamp = (LocalDateTime.now()).format(formatter);
    String transactionId = timestamp;

    bookings.add(new Booking(transactionId, customerId, cinemaId, movieId, showtimeId, seats, totalPrice, type));
    this.bookings = bookings;

    // Finalize the seat selection
    this.bulkAssignSeat(selectedShowtimeIdx, seats, true);

    // Serialize bookings
    this.saveBookings();

    return this.bookings.size() - 1;
  }

  public boolean saveBookings() {
    return Datasource.serializeData(this.bookings, "bookings.csv");
  }

//+ updateBooking (booking : Booking) : boolean
//+ removeBooking (transactionld : String)
//boolean
//+ printBookings() : void
//+ printBookings(customerld : String):void
  public void printBookings(String customerId){
    List<Booking> bookings = this.getBookings(customerId);
    Helper.logger("BookingHandler.printBookings", "Bookings: " + bookings);
    if(bookings.size() < 1) return;

    for(Booking booking : bookings) printBooking(booking.getTransactionId());
  }
//+ printBooking(transactionld : String) : void
  public void printBooking(String transactionId){
    Booking booking = this.getBooking(transactionId);
    if (booking == null) return;

    System.out.println("---------------------------------------------------------------------------");
    System.out.println(booking);

    // Showtime
    System.out.println("/// SHOWTIME DETAILS ///");
    int showtimeIdx = this.getShowtimeIdx(booking.getShowtimeId());
    this.printShowtimeDetails(showtimeIdx);

    // Movie
    System.out.println("/// MOVIE DETAILS ///");
    int movieIdx = movieHandler.getMovieIdx(booking.getMovieId());
    movieHandler.printMovieDetails(movieIdx);
    System.out.println("---------------------------------------------------------------------------");
  }
//+getBookingPrice(booking : Booking) : double
}
