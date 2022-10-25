package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.Helper.formatAsTable;

public class Booking {
  private String transactionId;
  private String customerId;
  private int cinemaId;
  private int movieId;
  private String showtimeId;
  private List<int[]> seats;
  private double totalPrice;
  private TicketType type;

  public Booking(String transactionId, String customerId, int cinemaId, int movieId, String showtimeId, List<int[]> seats, double totalPrice, TicketType type) {
    this.transactionId = transactionId;
    this.customerId = customerId;
    this.cinemaId = cinemaId;
    this.movieId = movieId;
    this.showtimeId = showtimeId;
    this.seats = seats;
    this.totalPrice = totalPrice;
    this.type = type;
  }

  /**
   * Clone constructor
   *
   * @param cloneBooking:Booking
   */
  public Booking(Booking cloneBooking) {
    this(
        cloneBooking.transactionId,
        cloneBooking.customerId,
        cloneBooking.cinemaId,
        cloneBooking.movieId,
        cloneBooking.showtimeId,
        cloneBooking.seats,
        cloneBooking.totalPrice,
        cloneBooking.type
    );
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public int getCinemaId() {
    return cinemaId;
  }

  public void setCinemaId(int cinemaId) {
    this.cinemaId = cinemaId;
  }

  public int getMovieId() {
    return movieId;
  }

  public void setMovieId(int movieId) {
    this.movieId = movieId;
  }

  public String getShowtimeId() {
    return showtimeId;
  }

  public void setShowtimeId(String showtimeId) {
    this.showtimeId = showtimeId;
  }

  public List<int[]> getSeats() {
    return seats;
  }

  public void setSeats(List<int[]> seats) {
    this.seats = seats;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  public TicketType getType() {
    return type;
  }

  public void setType(TicketType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    // Parse seatCodes
    List<String> seatCodes = this.seats.stream()
        .map(s -> ("R" + (s[0] + 1) + "C" + (s[1] + 1))).toList();

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Transaction ID:", this.transactionId));
    rows.add(Arrays.asList("Seats:", Arrays.deepToString(seatCodes.toArray())));
    rows.add(Arrays.asList("Type:", this.type.displayName));
    rows.add(Arrays.asList("Total Price:", String.format("%2f",this.totalPrice)));

    return formatAsTable(rows);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Booking && ((Booking) obj).transactionId.equals(this.transactionId);
  }

  @Override
  public int hashCode() {
    int prime = 31;
    return prime + Objects.hashCode(this.transactionId);
  }

  public enum TicketType {

    SENIOR_CITIZEN("Senior Citizen"),
    STUDENT("Student"),
    NON_PEAK("Non-Peak"),
    PEAK("PEAK");

    private final String displayName;

    TicketType(String displayName) {
      this.displayName = displayName;
    }
  }
}
