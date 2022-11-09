package moblima.entities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static moblima.utils.Helper.formatAsTable;

/**
 * The type Booking.
 */
public class Booking {
  private String transactionId;
  private String customerId;
  private int cinemaId;
  private int movieId;
  private String showtimeId;
  private List<int[]> seats;
  private double totalPrice;
  private TicketType type;

  /**
   * Instantiates a new Booking.
   *
   * @param transactionId the transaction id
   * @param customerId    the customer id
   * @param cinemaId      the cinema id
   * @param movieId       the movie id
   * @param showtimeId    the showtime id
   * @param seats         the seats
   * @param totalPrice    the total price
   * @param type          the type
   */
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
   * Instantiates a new Booking.
   *
   * @param cloneBooking the clone booking
   */
  public Booking(Booking cloneBooking) {
    this(cloneBooking.transactionId, cloneBooking.customerId, cloneBooking.cinemaId, cloneBooking.movieId, cloneBooking.showtimeId, cloneBooking.seats, cloneBooking.totalPrice, cloneBooking.type);
  }

  /**
   * Gets transaction id.
   *
   * @return the transaction id
   */
  public String getTransactionId() {
    return transactionId;
  }

  /**
   * Sets transaction id.
   *
   * @param transactionId the transaction id
   */
  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  /**
   * Gets customer id.
   *
   * @return the customer id
   */
  public String getCustomerId() {
    return customerId;
  }

  /**
   * Sets customer id.
   *
   * @param customerId the customer id
   */
  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  /**
   * Gets cinema id.
   *
   * @return the cinema id
   */
  public int getCinemaId() {
    return cinemaId;
  }

  /**
   * Sets cinema id.
   *
   * @param cinemaId the cinema id
   */
  public void setCinemaId(int cinemaId) {
    this.cinemaId = cinemaId;
  }

  /**
   * Gets movie id.
   *
   * @return the movie id
   */
  public int getMovieId() {
    return movieId;
  }

  /**
   * Sets movie id.
   *
   * @param movieId the movie id
   */
  public void setMovieId(int movieId) {
    this.movieId = movieId;
  }

  /**
   * Gets showtime id.
   *
   * @return the showtime id
   */
  public String getShowtimeId() {
    return showtimeId;
  }

  /**
   * Sets showtime id.
   *
   * @param showtimeId the showtime id
   */
  public void setShowtimeId(String showtimeId) {
    this.showtimeId = showtimeId;
  }

  /**
   * Gets seats.
   *
   * @return the seats
   */
  public List<int[]> getSeats() {
    return seats;
  }

  /**
   * Sets seats.
   *
   * @param seats the seats
   */
  public void setSeats(List<int[]> seats) {
    this.seats = seats;
  }

  /**
   * Gets total price.
   *
   * @return the total price
   */
  public double getTotalPrice() {
    return totalPrice;
  }

  /**
   * Sets total price.
   *
   * @param totalPrice the total price
   */
  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public TicketType getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(TicketType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    DecimalFormat df = new DecimalFormat("0.00");

    // Parse seatCodes
    List<String> seatCodes = this.seats.stream().map(s -> ("R" + (s[0] + 1) + "C" + (s[1] + 1))).toList();

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Transaction ID:", this.transactionId));
    rows.add(Arrays.asList("Seats:", Arrays.deepToString(seatCodes.toArray())));
    rows.add(Arrays.asList("Type:", this.type.displayName));
    rows.add(Arrays.asList("Total Price:", "SGD $" + df.format(this.totalPrice)));

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

  /**
   * The enum Ticket type.
   */
  public enum TicketType {

    /**
     * The Senior.
     */
    SENIOR("Senior Citizen"),
    /**
     * Student ticket type.
     */
    STUDENT("Student"),
    /**
     * The Non peak.
     */
    NON_PEAK("Non Peak"),
    /**
     * Peak ticket type.
     */
    PEAK("Peak"),
    /**
     * The Super peak.
     */
    SUPER_PEAK("Super Peak");

    private final String displayName;

    TicketType(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
