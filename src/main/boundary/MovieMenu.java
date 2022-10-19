/*
THROW AWAY, don't sad crys :(
*/
package main.boundary;

import tmdb.datasource.MovieDatasource;
import tmdb.entity.Movie;
import tmdb.entity.Review;
import tmdb.utils.Helper;

import java.util.List;

public class MovieMenu extends Menu {
  private static final MovieDatasource dsMovie = new MovieDatasource();
  private final List<Movie> movies;
  private final List<Review> reviews;
  private int selectedMovieIdx;

  public MovieMenu() {
    super();
    this.movies = dsMovie.getMovies();
    this.reviews = dsMovie.getReviews();
    this.selectedMovieIdx = 0;

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      int movieId = i;
      this.menuMap.put((i + 1) + ". " + movie.getTitle(), () -> {
        this.selectedMovieIdx = movieId;
        this.printMovie(this.getSelectedMovie());
      });
    }
    this.menuMap.put((this.menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
  }

  public int getSelectedMovieIdx() {
    return this.selectedMovieIdx;
  }

  /**
   * Purely for listing movies, no scanner functionality
   */
  public void listMovies() {
    if (this.movies.isEmpty()) {
      System.out.println("No movies available");
      return;
    }

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      System.out.println("> " + i + " " + movie.getTitle());
    }
  }

  public Movie getMovie(
      int movieIdx
  ) {
    return this.movies.get(movieIdx);
  }

  public void printMovie(
      Movie movie
  ) {
    Helper.logger("MovieMenu.printMovie", "Selected Idx: " + this.selectedMovieIdx);
    System.out.println(movie.toString());
  }

  public Movie getSelectedMovie() {
    return getMovie(this.selectedMovieIdx);
  }

  public void selectMovie() {
    this.displayMenu();
  }

  /**
   * Displays menu with scanner functionalities
   */
  @Override
  public void showMenu() {
    this.displayMenu();
  }
}
