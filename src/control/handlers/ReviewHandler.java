package control.handlers;

import entity.Review;
import utils.Helper;
import utils.datasource.Datasource;
import utils.datasource.MovieDatasource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewHandler extends MovieHandler{
  protected List<Review> reviews;
  protected int selectedReviewIdx = -1;

  public ReviewHandler(){
    super();
    MovieDatasource dsMovie = new MovieDatasource();
    this.movies = dsMovie.getMovies();
    this.reviews = dsMovie.getReviews();
  }

  /**
   * Get selected review
   *
   * @return review:Review
   */
  //+ getSelectedReview() : Review
  public Review getSelectedReview() {
    return this.getReview(this.selectedReviewIdx);
  }

  /**
   * Set selected review idx
   *
   * @param reviewIdx:int
   */
  // + setSelectedReviewIdx(reviewIdx:int) :void
  public void setSelectedReviewIdx(int reviewIdx) {
    this.selectedReviewIdx = reviewIdx;
  }

  /**
   * Get review of specified idx
   *
   * @param reviewIdx:int
   * @return review:Review | null
   */
  //+ getReview(reviewIdx : int) : Review
  public Review getReview(int reviewIdx) {
    return (this.reviews.size() < 1 || reviewIdx < 0) ? null : new Review(this.reviews.get(reviewIdx));
  }

  /**
   * Retrieves review by the specified id
   *
   * @param reviewId:String
   * @return review:Review
   */
  //+ getReview(reviewId:String): Review
  public Review getReview(String reviewId) {
    int idx = this.getReviewIdx(reviewId);
    if (idx < 0) return null;

    return this.getReview(idx);
  }

  /**
   * Get idx of specified review id
   *
   * @param reviewId:int
   * @return reviewIdx:int
   */
  // + getReviewIdx(reviewId:String):int
  public int getReviewIdx(String reviewId) {
    if (this.reviews.size() < 1 || reviewId.isBlank()) return -1;
    Helper.logger("ReviewHandler.getReviewIdx", "ReviewId: " + reviewId);

    for (int i = 0; i < this.reviews.size(); i++) {
      Review review = this.reviews.get(i);
      if (review.getId() == reviewId) {
        Helper.logger("ReviewHandler.getReviewIdx", "Review: " + review);
        return i;
      }
    }

    return -1;
  }

  /**
   * Get review list
   *
   * @return reviews:List<Review>
   */
  //+ getReviews() : List <Review>
  public List<Review> getReviews() {
    List<Review> reviews = new ArrayList<Review>();
    if (this.reviews.size() > 0) reviews = this.reviews;
    return reviews;
  }

  /**
   * Update an existing review
   *
   * @param rating:int
   * @param reviewContent:String
   * @return status:boolean
   */
  //+ updateReview(rating:int, reviewContent:String):boolean
  public boolean updateReview(int rating, String reviewContent) {
    boolean status = false;
    if (this.reviews.size() < 1 || this.selectedReviewIdx < 0) return status;

    Review review = this.getReview(this.selectedReviewIdx);
    // Early return if review does not exist
    if (review == null) return status;

    this.reviews.set(this.selectedReviewIdx, new Review(
        review.getId(),
        review.getMovieId(),
        reviewContent,
        rating,
        review.getAuthorName(),
        review.getAuthorId()
    ));

    // Compute movie's overall rating
    this.updateMovieRating(review.getMovieId(), this.computeMovieRatings(review.getMovieId()));

    //Serialize data
    this.saveReviews();
    status = true;

    return status;
  }

  /**
   * Remove review of specified id
   *
   * @param reviewId:String
   * @return status:boolean
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
   * Gets list of movie reviews
   *
   * @param movieId:int
   * @return reviews:List<Review>
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
   * Gets list of user reviews
   *
   * @param authorId:String
   * @return reviews:List<Review>
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
   * Append new review to existing review list
   *
   * @param movieId:int
   * @param review:String
   * @param rating:int
   * @param authorName:String
   * @param authorId:String
   * @return reviewIdx:int
   */
  //+ addReview(movieId: int, review:String, rating:int, authorName:String, authorId:String): int
  public int addReview(int movieId, String review, int rating, String authorName, String authorId) {
    this.reviews.add(new Review(
        UUID.randomUUID().toString(),
        movieId,
        review,
        rating,
        authorName,
        authorId
    ));

    // Compute movie's overall rating
    this.updateMovieRating(movieId, this.computeMovieRatings(movieId));

    //Serialize data
    this.saveReviews();

    return this.reviews.size() - 1;
  }

  public double computeMovieRatings(int movieId) {
    double overallRatings = 0;
    List<Review> reviews = this.getMovieReviews(movieId);
    if (reviews.size() < 1) return overallRatings;

    int totalRatings = 0;
    for (Review review : reviews) totalRatings += review.getRating();
    overallRatings = Double.valueOf(totalRatings) / reviews.size();

    return overallRatings;
  }

  /**
   * Checks if author has already reviewed specified movie id
   *
   * @param authorId:String
   * @param movieId:int
   * @return reviewId:String | null
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
   * Serialize review data to CSV
   */
  //# saveCustomers():boolean
  public boolean saveReviews() {
    return Datasource.serializeData(this.reviews, "reviews.csv");
  }
}
