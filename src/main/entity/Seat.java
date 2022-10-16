package main.entity;

/**
 * Represents the seating chart in a cinema.
 *
 * @author SS11 Group 1
 * @version 1.0
 * @see Cinema
 * @see Movie
 * @see Showtime
 * @see Seat
 * @since 2022/10/11
 */
public class Seat {
  private String seatCode;
  private boolean isBooked;

  public Seat(String seatCode, boolean isBooked) {
    this.seatCode = seatCode;
    this.isBooked = isBooked;
  }

  public String getSeatCode() {
    return seatCode;
  }

  public void setSeatCode(String seatCode) {
    this.seatCode = seatCode;
  }

  public boolean isIsBooked() {
    return isBooked;
  }

  public void setIsBooked(boolean isBooked) {
    this.isBooked = isBooked;
  }

}
