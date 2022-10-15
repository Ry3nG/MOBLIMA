package main.entity;

import main.types.MovieConstants.ShowStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * The movie class
 *
 * @author SS11 Group 1
 * @version 1.0
 * @see Cinema
 * @see Cineplex
 * @see Showtime
 * @see Seat
 * @since 2022/10/11
 */
public class Movie {
  private final String id;
  private String title;
  private String synopsis;
  private final String releaseDate;
  private String director;
  private int runtime;
  private boolean isBlockbuster;
  private ShowStatus showStatus;
  private List<String> cast;
  private List<Review> reviews;
  private List<Showtime> showtimes;


  public Movie(
      String id,
      String title,
      String synopsis,
      String releaseDate,
      int runtime,
      ShowStatus showStatus,
      String director,
      List<String> cast,
      boolean isBlockbuster,
      List<Review> reviews,
      List<Showtime> showtimes
  ) {
    this.id = id;
    this.title = title;
    this.synopsis = synopsis;
    this.releaseDate = releaseDate;
    this.runtime = runtime;
    this.showStatus = showStatus;
    this.director = director;
    this.cast = cast;
    this.isBlockbuster = isBlockbuster;
    this.reviews = reviews;
    this.showtimes = showtimes;
  }

  public Movie(
      String id,
      String title,
      String synopsis,
      String releaseDate,
      int runtime,
      ShowStatus showStatus,
      String director,
      List<String> cast,
      boolean isBlockbuster
  ) {
    this(
        id,
        title,
        synopsis,
        releaseDate,
        runtime,
        showStatus,
        director,
        cast,
        isBlockbuster,
        new ArrayList<Review>(),
        new ArrayList<Showtime>()
    );
  }

  /*
   * GETTERS
   */
  public String getTitle() {
    return title;
  }

  /*
   * SETTERS
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public String getDirector() {
    return this.director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public List<String> getCast() {
    return this.cast;
  }

  public void setCast(List<String> cast) {
    this.cast = cast;
  }

  public ShowStatus getShowStatus() {
    return this.showStatus;
  }

  public void setShowStatus(ShowStatus showStatus) {
    this.showStatus = showStatus;
  }

  public boolean getIsBlockbuster() {
    return this.isBlockbuster;
  }

  public void setIsBlockbuster(boolean isBlockbuster) {
    this.isBlockbuster = isBlockbuster;
  }

  public int getRuntime() {
    return this.runtime;
  }

  public void setRuntime(int runtime) {
    this.runtime = runtime;
  }

  public List<Review> getReviews() {
    return this.reviews;
  }

  public void setReviews(List<Review> reviews) {
    this.reviews = reviews;
  }

  public List<Showtime> getShowtimes() {
    return this.showtimes;
  }

  public void setShowtimes(List<Showtime> showtimes) {
    this.showtimes = showtimes;
  }

}
