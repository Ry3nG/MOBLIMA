package main.entity;

public class Review {
  private int movieId;
  private String id, content, author;
  private int rating;

  public Review(
      String id,
      int movieId,
      String content,
      String author,
      int rating
  ) {
    this.id = id;
    this.movieId = movieId;
    this.content = content;
    this.author = author;
    this.rating = rating;
  }

  public String getId() {
    return this.id;
  }

  public void setId(
      String id
  ) {
    this.id = id;
  }

  public int getMovieId() {
    return this.movieId;
  }

  public void setMovieId(
      int movieId
  ) {
    this.movieId = movieId;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(
      String content
  ) {
    this.content = content;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(
      String author
  ) {
    this.author = author;
  }

  public int getRating() {
    return this.rating;
  }

  public void setRating(
      int rating
  ) {
    this.rating = rating;
  }

}
