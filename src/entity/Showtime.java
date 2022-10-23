package entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
        {true, true, true, true, true, true, true, true}
    });
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

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.ofPattern("dd-MM-yyyy 'at' hh:mma");
    DayOfWeek day = datetime.getDayOfWeek();

    String printed = "Datetime: " + day + ", " + this.datetime.format(formatter) + "\n";
    printed += "Movie ID: " + this.movieId + "\n";
    printed += "Cinema ID: " + this.cinemaId + "\n";
    printed += "Cineplex ID: " + this.cineplexId + "\n";

    return printed;
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
