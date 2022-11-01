package entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static utils.Helper.formatAsTable;


public class Showtime {
  private String id;
  private String cineplexId;
  private int cinemaId;
  private int movieId;
  private LocalDateTime datetime;
  private boolean[][] seats;

  public Showtime(String id, String cineplexId, int cinemaId, int movieId, LocalDateTime datetime, boolean[][] seats) {
    this.id = id;
    this.cineplexId = cineplexId;
    this.cinemaId = cinemaId;
    this.movieId = movieId;
    this.datetime = datetime;
    this.seats = seats;
  }

  public Showtime(String id, String cineplexId, int cinemaId, int movieId, LocalDateTime datetime) {
    this(id, cineplexId, cinemaId, movieId, datetime, new boolean[][]{
        {true, true, true, true, true, true, true, true},
        {true, true, true, true, true, true, true, true},
        {true, true, true, true, true, true, true, true},
        {true, true, true, true, true, true, true, true},
        {true, true, true, true, true, true, true, true},
    });
  }

  /**
   * Clone constructor
   *
   * @param cloneShowtime:Showtime
   */
  public Showtime(Showtime cloneShowtime) {
    this(
        cloneShowtime.id,
        cloneShowtime.cineplexId,
        cloneShowtime.cinemaId,
        cloneShowtime.movieId,
        cloneShowtime.datetime,
        cloneShowtime.seats
    );
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCineplexId() {
    return cineplexId;
  }

  public void setCineplexId(String cineplexId) {
    this.cineplexId = cineplexId;
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

  public boolean[][] getSeats() {
    return seats;
  }

  public void setSeats(boolean[][] seats) {
    this.seats = seats;
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

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' hh:mma");
    DayOfWeek day = datetime.getDayOfWeek();

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Datetime:", day + ", " + this.datetime.format(formatter)));
    rows.add(Arrays.asList("Movie ID:", Integer.toString(this.movieId)));
    rows.add(Arrays.asList("Cinema ID:", Integer.toString(this.cinemaId)));
    rows.add(Arrays.asList("Cineplex ID:", this.cineplexId));
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
}
