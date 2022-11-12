package moblima.control.handlers;

import moblima.entities.Movie;
import moblima.entities.Review;
import moblima.utils.Helper;
import moblima.utils.datasource.Datasource;
import moblima.utils.datasource.MovieDatasource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static moblima.utils.Helper.colorPrint;
import static moblima.utils.Helper.formatAsTable;

/**
 * The type Review handler.
 */
public class ReviewHandler extends MovieHandler {
  /**
   * The Reviews.
   */
  protected List<Review> reviews;
  /**
   * The Selected review idx.
   */
  protected int selectedReviewIdx = -1;

  /**
   * Instantiates a new Review handler.
   */
  public ReviewHandler() {
    super();
    MovieDatasource dsMovie = new MovieDatasource();
    this.reviews = dsMovie.getReviews();
  }

  /**
   * Gets selected review.
   *
   * @return the selected review
   */
//+ getSelectedReview() : Review
  public Review getSelectedReview() {
    return this.getReview(this.selectedReviewIdx);
  }

  /**
   * Sets selected review idx.
   *
   * @param reviewIdx the review idx
   */
// + setSelectedReviewIdx(reviewIdx:int) :void
  public void setSelectedReviewIdx(int reviewIdx) {
    this.selectedReviewIdx = reviewIdx;
  }

  /**
   * Gets review.
   *
   * @param reviewIdx the review idx
   * @return the review
   */
//+ getReview(reviewIdx : int) : Review
  public Review getReview(int reviewIdx) {
    return (this.reviews.size() < 1 || reviewIdx < 0) ? null : new Review(this.reviews.get(reviewIdx));
  }

  /**
   * Gets review.
   *
   * @param reviewId the review id
   * @return the review
   */
//+ getReview(reviewId:String): Review
  public Review getReview(String reviewId) {
    int idx = this.getReviewIdx(reviewId);
    if (idx < 0) return null;

    return this.getReview(idx);
  }

  /**
   * Gets review idx.
   *
   * @param reviewId the review id
   * @return the review idx
   */
// + getReviewIdx(reviewId:String):int
  public int getReviewIdx(String reviewId) {
    if (this.reviews.size() < 1 || reviewId.isBlank()) return -1;
    Helper.logger("ReviewHandler.getReviewIdx", "ReviewId: " + reviewId);

    for (int i = 0; i < this.reviews.size(); i++) {
      Review review = this.reviews.get(i);
      if (review.getId().equals(reviewId)) {
        Helper.logger("ReviewHandler.getReviewIdx", "Review: " + review);
        return i;
      }
    }

    return -1;
  }

  /**
   * Gets reviews.
   *
   * @return the reviews
   */
//+ getReviews() : List <Review>
  public List<Review> getReviews() {
    List<Review> reviews = new ArrayList<Review>();
    if (this.reviews.size() > 0) reviews = this.reviews;
    return reviews;
  }

  /**
   * Update review boolean.
   *
   * @param rating        the rating
   * @param reviewContent the review content
   * @return the boolean
   */
//+ updateReview(rating:int, reviewContent:String):boolean
  public boolean updateReview(int rating, String reviewContent) {
    boolean status = false;
    if (this.reviews.size() < 1 || this.selectedReviewIdx < 0) return status;

    Review review = this.getReview(this.selectedReviewIdx);
    // Early return if review does not exist
    if (review == null) return status;

    this.reviews.set(this.selectedReviewIdx, new Review(review.getId(), review.getMovieId(), reviewContent, rating, review.getAuthorName(), review.getAuthorId()));

    // Compute movie's overall rating
    this.updateMovieRating(review.getMovieId(), this.computeMovieRatings(review.getMovieId()));

    //Serialize data
    this.saveReviews();
    status = true;

    return status;
  }

  /**
   * Remove review boolean.
   *
   * @param reviewId the review id
   * @return the boolean
   */
//+ removeReview (reviewId : int) : boolean
  public boolean removeReview(String reviewId) {
    boolean status = false;
    if (this.reviews.size() < 1 || reviewId.isEmpty()) return status;

    // Early return if review does not exist
    int reviewIdx = this.getReviewIdx(reviewId);
    if (reviewIdx < 0) return status;

    Review review = this.getReview(reviewIdx);
    this.reviews.remove(reviewIdx);

    // Compute movie's overall rating
    this.updateMovieRating(review.getMovieId(), this.computeMovieRatings(review.getMovieId()));

    //Serialize data
    this.saveReviews();

    status = true;
    return status;
  }

  /**
   * Gets movie reviews.
   *
   * @param movieId the movie id
   * @return the movie reviews
   */
//+ getMovieReviews(movieId:int) : List<Review>
  public List<Review> getMovieReviews(int movieId) {
    List<Review> reviews = new ArrayList<Review>();
    if (this.reviews.size() < 1) return reviews;

    for (Review review : this.reviews) {
      if (review.getMovieId() == movieId) reviews.add(review);
    }
    Helper.logger("ReviewHandler.getMovieReviews", "Reviews: " + reviews);
    return reviews;
  }

  /**
   * Gets user reviews.
   *
   * @param authorId the author id
   * @return the user reviews
   */
//+ getUserReviews(authorId:String) : List<Review>
  public List<Review> getUserReviews(String authorId) {
    List<Review> reviews = new ArrayList<Review>();
    if (this.reviews.size() < 1 || authorId.isBlank()) return reviews;

    for (Review review : this.reviews) {
      if (review.getAuthorId().equals(authorId)) reviews.add(review);
    }
    Helper.logger("ReviewHandler.getUserReviews", "Reviews: " + reviews);
    return reviews;
  }

  /**
   * Add review int.
   *
   * @param movieId    the movie id
   * @param review     the review
   * @param rating     the rating
   * @param authorName the author name
   * @param authorId   the author id
   * @return the int
   */
//+ addReview(movieId: int, review:String, rating:int, authorName:String, authorId:String): int
  public int addReview(int movieId, String review, int rating, String authorName, String authorId) {
    this.reviews.add(new Review(UUID.randomUUID().toString(), movieId, review, rating, authorName, authorId));

    // Compute movie's overall rating
    this.updateMovieRating(movieId, this.computeMovieRatings(movieId));

    //Serialize data
    this.saveReviews();

    return this.reviews.size() - 1;
  }

  /**
   * Compute movie ratings double.
   *
   * @param movieId the movie id
   * @return the double
   */
  public double computeMovieRatings(int movieId) {
    double overallRatings = 0;
    List<Review> reviews = this.getMovieReviews(movieId);
    if (reviews.size() < 1) return overallRatings;

    int totalRatings = 0;
    for (Review review : reviews) totalRatings += review.getRating();
    overallRatings = (double) totalRatings / reviews.size();

    return overallRatings;
  }

  /**
   * Check if already reviewed string.
   *
   * @param authorId the author id
   * @param movieId  the movie id
   * @return the string
   */
//+ checkIfAlreadyReviewed(authorId:String, movieId:int):String
  public String checkIfAlreadyReviewed(String authorId, int movieId) {
    String reviewId = null;

    List<Review> reviews = this.getUserReviews(authorId);
    if (reviews.size() < 1 || movieId < 0) return reviewId;

    for (Review review : reviews) {
      if (review.getMovieId() == movieId) {
        reviewId = review.getId();
        break;
      }
    }
    return reviewId;
  }

  /**
   * Print movie details.
   *
   * @param movieIdx      the movie idx
   * @param showTruncated the show truncated
   * @return the string
   */
//+ printMovieDetails(movieldx : int) : void
  public String printMovieDetails(int movieIdx, boolean showTruncated) {
    Movie movie = getMovie(movieIdx);
    if (movie == null) return "";

    this.selectedMovieIdx = movieIdx;
    String header = "\n/// MOVIE DETAILS ///";
    colorPrint(header, Helper.Preset.HIGHLIGHT);

    boolean displayOverallRatings = this.getMovieReviews(movie.getId()).size() > 1;
    colorPrint(formatAsTable(movie.toStringRows(showTruncated, displayOverallRatings)), Helper.Preset.HIGHLIGHT);

    return header + "\n" + movie;
  }

  /**
   * Save reviews boolean.
   *
   * @return the boolean
   */
//# saveCustomers():boolean
  protected boolean saveReviews() {
    return Datasource.serializeData(this.reviews, "reviews.csv");
  }
}
