package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.Helper.formatAsTable;

public class Review {
  private String id;
  private int movieId;
  private String review;
  private int rating;
  private String authorName;
  private String authorId;

  public Review(String id, int movieId, String review, int rating, String authorName, String authorId) {
    this.id = id;
    this.movieId = movieId;
    this.review = review;
    this.rating = rating;
    this.authorName = authorName;
    this.authorId = authorId;
  }

  /**
   * Clone constructor
   *
   * @param cloneReview:Review
   */
  public Review(Review cloneReview) {
    this(
        cloneReview.id,
        cloneReview.movieId,
        cloneReview.review,
        cloneReview.rating,
        cloneReview.authorName,
        cloneReview.authorId
    );
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getMovieId() {
    return movieId;
  }

  public void setMovieId(int movieId) {
    this.movieId = movieId;
  }

  public String getReview() {
    return review;
  }

  public void setReview(String review) {
    this.review = review;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  @Override
  public String toString() {

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Author:", this.authorName));
    rows.add(Arrays.asList("Movie ID:", Integer.toString(this.movieId)));
    rows.add(Arrays.asList("Rating:", Integer.toString(this.rating)));
    rows.add(Arrays.asList("Review:", this.review));

    return formatAsTable(rows);
  }
}
