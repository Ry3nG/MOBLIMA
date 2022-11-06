package moblima.entities;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static moblima.utils.Helper.formatAsTable;

/**
 * The type Showtime.
 */
public class Showtime {
  private String id;
  private int cinemaId;
  private int movieId;
  private LocalDateTime datetime;
  private ShowType type;
  private boolean[][] seats;

  /**
   * Instantiates a new Showtime.
   *
   * @param id       the id
   * @param cinemaId the cinema id
   * @param movieId  the movie id
   * @param datetime the datetime
   * @param type     the type
   * @param seats    the seats
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
   * Instantiates a new Showtime.
   *
   * @param id       the id
   * @param cinemaId the cinema id
   * @param movieId  the movie id
   * @param datetime the datetime
   * @param type     the type
   */
  public Showtime(String id, int cinemaId, int movieId, LocalDateTime datetime, ShowType type) {
    this(id, cinemaId, movieId, datetime, type, new boolean[][]{{true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true}, {true, true, true, true, true, true, true, true},});
  }

  /**
   * Instantiates a new Showtime.
   *
   * @param cloneShowtime the clone showtime
   */
  public Showtime(Showtime cloneShowtime) {
    this(cloneShowtime.id, cloneShowtime.cinemaId, cloneShowtime.movieId, cloneShowtime.datetime, cloneShowtime.type, cloneShowtime.seats);
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
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
   * Gets datetime.
   *
   * @return the datetime
   */
  public LocalDateTime getDatetime() {
    return datetime;
  }

  /**
   * Sets datetime.
   *
   * @param datetime the datetime
   */
  public void setDatetime(LocalDateTime datetime) {
    this.datetime = datetime;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public ShowType getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(ShowType type) {
    this.type = type;
  }

  /**
   * Get seats boolean [ ] [ ].
   *
   * @return the boolean [ ] [ ]
   */
  public boolean[][] getSeats() {
    return seats;
  }

  /**
   * Sets seats.
   *
   * @param seats the seats
   */
  public void setSeats(boolean[][] seats) {
    this.seats = seats;
  }

  /**
   * Gets day.
   *
   * @return the day
   */
  public DayOfWeek getDay() {
    return datetime.getDayOfWeek();
  }

  /**
   * Gets formatted datetime.
   *
   * @return the formatted datetime
   */
  public String getFormattedDatetime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mma");
    return this.datetime.format(formatter);
  }

  /**
   * Gets seat count.
   *
   * @param isAvailable the is available
   * @return the seat count
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
   * Gets seat count.
   *
   * @return the seat count
   */
  public int getSeatCount() {
    int seatCount = 0;
    if (this.seats.length < 1) return seatCount;
    for (boolean[] seat : this.seats) for (int col = 0; col < seat.length; col++) seatCount++;
    return seatCount;
  }

  @Override
  public String toString() {

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Datetime:", this.getDay() + ", " + this.getFormattedDatetime()));
    rows.add(Arrays.asList("Cinema ID:", Integer.toString(this.cinemaId)));
    rows.add(Arrays.asList("Show Type:", this.type.toString()));
    rows.add(Arrays.asList("Booked Seats:", this.getSeatCount(false) + "/" + this.getSeatCount()));

    return formatAsTable(rows);
  }

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
   * The enum Show type.
   */
  public enum ShowType {

    /**
     * Digital show type.
     */
    Digital("Digital"),
    /**
     * Three dimensional show type.
     */
    ThreeDimensional("3D");

    private final String displayName;

    ShowType(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
