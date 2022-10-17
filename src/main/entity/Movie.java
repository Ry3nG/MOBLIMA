package main.entity;

import tmdb.types.MovieConstants.ShowStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
  private final int id;
  private final String releaseDate;
  private String title;
  private String synopsis;
  private String director;
  private String contentRating;
  private int runtime;
  private boolean isBlockbuster;
  private ShowStatus showStatus;
  private List<String> cast;
  private List<String> reviewIds;
  private List<Showtime> showtimes;


  public Movie(
      int id,
      String title,
      String synopsis,
      String releaseDate,
      String contentRating,

      String director,
      List<String> cast,

      int runtime,
      boolean isBlockbuster,
      ShowStatus showStatus,
      List<String> reviewIds,
      List<Showtime> showtimes
  ) {
    this.id = id;
    this.title = title;
    this.synopsis = synopsis;
    this.releaseDate = releaseDate;
    this.contentRating = contentRating;

    this.director = director;
    this.cast = cast;

    this.runtime = runtime;
    this.isBlockbuster = isBlockbuster;
    this.showStatus = showStatus;

    this.reviewIds = reviewIds;
    this.showtimes = showtimes;
  }

  public Movie(
      int id,
      String title,
      String synopsis,
      String releaseDate,
      String contentRating,

      String director,
      List<String> cast,

      int runtime,
      boolean isBlockbuster,
      ShowStatus showStatus,
      List<String> reviewIds
  ) {
    this(
        id,
        title,
        synopsis,
        releaseDate,
        contentRating,

        director,
        cast,

        runtime,
        isBlockbuster,
        showStatus,

        reviewIds,
        new ArrayList<Showtime>()
    );
  }

  public int getId() {
    return this.id;
  }

  public String getTitle() {
    return title;
  }

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
  public String getContentRating() {
    return this.contentRating;
  }

  public void setContentRating(String contentRating) {
    this.contentRating = contentRating;
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

  public List<String> getReviews() {
    return this.reviewIds;
  }

  public void setReviews(List<String> reviews) {
    this.reviewIds = reviews;
  }

  public List<Showtime> getShowtimes() {
    return this.showtimes;
  }

  public void setShowtimes(List<Showtime> showtimes) {
    this.showtimes = showtimes;
  }

  public String getUrl() {
    return "https://www.themoviedb.org/movie/" + this.id;
  }

  @Override
  public String toString() {
    String printed = "Title: " + this.title + "\n";
    printed += "Runtime: " + this.runtime + " minutes\n";
    printed += "Synopsis: " + this.synopsis + "\n";
    printed += "Content Rating: " + this.contentRating + "\n";
    printed += "Directed By: " + this.director + "\n";
    printed += "Cast: " + this.cast.toString() + "\n";
    printed += "LINK: " + this.getUrl() + "\n";

    return printed;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Movie && ((Movie) obj).id == (this.id);
  }

  @Override
  public int hashCode() {
    int prime = 31;
    return prime + Objects.hashCode(this.id);
  }
}
