package entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Booking {
  public enum TicketType {

    SENIOR_CITIZEN("Senior Citizen"),
    STUDENT("Student"),
    NON_PEAK("Non-Peak"),
    PEAK("PEAK");

    private String displayName;

    TicketType(String displayName) {
      this.displayName = displayName;
    }
  }

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
        .map(s -> ("R" + (s[0] + 1) + "C" + (s[1] + 1)))
        .collect(Collectors.toList());

    String printed = "Transaction ID: " + this.transactionId + "\n";
    printed += "Seats: " + Arrays.deepToString(seatCodes.toArray()) + "\n";
    printed += "Type: " + this.type.displayName + "\n";
    printed += "Total Price: " + this.totalPrice + "\n";

    return printed;
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
}
