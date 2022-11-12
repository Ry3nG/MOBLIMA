package moblima.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static moblima.utils.Helper.formatAsTable;

/**
 * The type Review.
 */
public class Review {
  /**
   * The constant MIN_RATING.
   */
  public static final int MIN_RATING = 1;
  /**
   * The constant MAX_RATING.
   */
  public static final int MAX_RATING = 5;

  private String id;
  private int movieId;
  private String review;
  private int rating;
  private String authorName;
  private String authorId;

  /**
   * Instantiates a new Review.
   *
   * @param id         the id
   * @param movieId    the movie id
   * @param review     the review
   * @param rating     the rating
   * @param authorName the author name
   * @param authorId   the author id
   */
  public Review(String id, int movieId, String review, int rating, String authorName, String authorId) {
    this.id = id;
    this.movieId = movieId;
    this.review = review;
    this.rating = rating;
    this.authorName = authorName;
    this.authorId = authorId;
  }

  /**
   * Instantiates a new Review.
   *
   * @param cloneReview the clone review
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
   * Gets review.
   *
   * @return the review
   */
  public String getReview() {
    return review;
  }

  /**
   * Sets review.
   *
   * @param review the review
   */
  public void setReview(String review) {
    this.review = review;
  }

  /**
   * Gets rating.
   *
   * @return the rating
   */
  public int getRating() {
    return rating;
  }

  /**
   * Sets rating.
   *
   * @param rating the rating
   */
  public void setRating(int rating) {
    this.rating = rating;
  }

  /**
   * Gets author name.
   *
   * @return the author name
   */
  public String getAuthorName() {
    return authorName;
  }

  /**
   * Sets author name.
   *
   * @param authorName the author name
   */
  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  /**
   * Gets author id.
   *
   * @return the author id
   */
  public String getAuthorId() {
    return authorId;
  }

  /**
   * Sets author id.
   *
   * @param authorId the author id
   */
  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  @Override
  public String toString() {

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Author:", this.authorName));
    rows.add(Arrays.asList("Movie ID:", Integer.toString(this.movieId)));
    rows.add(Arrays.asList("Rating:", this.rating + " / " + MAX_RATING));
    rows.add(Arrays.asList("Review:", this.review));

    return formatAsTable(rows);
  }
}
