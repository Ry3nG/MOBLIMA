package entities;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.Helper.formatAsTable;

/**
 * Encapsulates showtimes details
 *
 * @author Crystal Cheong
 * @version 1.0
 */
public class Showtime {
  private String id;
  private int cinemaId;
  private int movieId;
  private LocalDateTime datetime;
  private ShowType type;
  private boolean[][] seats;

  /**
   * Default constructor
   *
   * @param id:String
   * @param cinemaId:int
   * @param movieId:int
   * @param datetime:LocalDateTime
   * @param type:ShowType
   * @param seats:boolean[][]
   */
  public Showtime(String id, int cinemaId, int movieId, LocalDateTime datetime, ShowType type, boolean[][] seats) {
    this.id = id;
    this.cinemaId = cinemaId;
    this.movieId = movieId;
    this.datetime = datetime;
    this.type = type;
    this.seats = seats;
  }

  /**
   * Subset constructor with default seat arrangements
   *
   * @param id:String
   * @param cinemaId:int
   * @param movieId:int
   * @param datetime:LocalDateTime
   * @param type:ShowType
   */
  public Showtime(String id, int cinemaId, int movieId, LocalDateTime datetime, ShowType type) {
    this(id, cinemaId, movieId, datetime, type, new boolean[][]{{true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true},});
  }

  /**
   * Clone constructor
   *
   * @param cloneShowtime:Showtime
   */
  public Showtime(Showtime cloneShowtime) {
    this(cloneShowtime.id, cloneShowtime.cinemaId, cloneShowtime.movieId, cloneShowtime.datetime, cloneShowtime.type, cloneShowtime.seats);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public LocalDateTime getDatetime() {
    return datetime;
  }

  public void setDatetime(LocalDateTime datetime) {
    this.datetime = datetime;
  }

  public ShowType getType() {
    return type;
  }

  public void setType(ShowType type) {
    this.type = type;
  }

  public boolean[][] getSeats() {
    return seats;
  }

  public void setSeats(boolean[][] seats) {
    this.seats = seats;
  }

  /**
   * Gets the day of showtime
   *
   * @return showtimeDay:DayOfWeek
   */
  public DayOfWeek getDay() {
    return datetime.getDayOfWeek();
  }

  /**
   * Pretty print show datetime
   *
   * @return strShowDateTime:String
   */
  public String getFormattedDatetime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mma");
    return this.datetime.format(formatter);
  }

  /**
   * Get available seat count of showtime
   *
   * @param isAvailable:boolean
   * @return availableSeatCount:int
   */
  public int getSeatCount(boolean isAvailable) {
    int availableSeatCount = 0;
    if (this.seats.length < 1) return availableSeatCount;

    for (boolean[] seat : this.seats) {
      for (boolean b : seat) {
        if (b == isAvailable) availableSeatCount++;
      }
    }

    return availableSeatCount;
  }

  /**
   * Get total seat count of showtime
   *
   * @return seatCount:int
   */
  public int getSeatCount() {
    int seatCount = 0;
    if (this.seats.length < 1) return seatCount;
    for (boolean[] seat : this.seats) for (int col = 0; col < seat.length; col++) seatCount++;
    return seatCount;
  }

  /**
   * Pretty print Showtime object
   *
   * @return strShowtime:String
   */
  @Override
  public String toString() {

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Datetime:", this.getDay() + ", " + this.getFormattedDatetime()));
    rows.add(Arrays.asList("Cinema ID:", Integer.toString(this.cinemaId)));
    rows.add(Arrays.asList("Show Type:", this.type.toString()));
    rows.add(Arrays.asList("Booked Seats:", this.getSeatCount(false) + "/" + this.getSeatCount()));

    return formatAsTable(rows);
  }

  /**
   * Compares Showtime ID to determine object equality
   *
   * @param obj:Object
   * @return isEqual:boolean
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Showtime && ((Showtime) obj).id.equals(this.id);
  }

  @Override
  public int hashCode() {
    int prime = 31;
    return prime + Objects.hashCode(this.id);
  }

  /**
   * Constant values of all possible Showtime show types
   *
   * @author Crystal Cheong
   * @version 1.0
   */
  public enum ShowType {

    Digital("Digital"),
    ThreeDimensional("3D");

    private final String displayName;

    /**
     * Default constructor to initialize Enum value with display name
     *
     * @param displayName:String
     */
    ShowType(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
