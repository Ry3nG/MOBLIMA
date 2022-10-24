package control;

import tmdb.control.MovieDatasource;
import tmdb.entities.Movie;
import tmdb.entities.Movie.ContentRating;
import tmdb.entities.Movie.ShowStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieHandler {
  private final MovieDatasource dsMovie = new MovieDatasource();
  private List<Movie> movies = new ArrayList<Movie>();
  private int selectedMovieIdx = -1;

  public MovieHandler() {
    this.movies = dsMovie.getMovies();
  }

  /**
   * Set selected movie idx
   *
   * @param movieIdx:int
   */
  // + setSelectedMovieIdx(movieIdx:int) :void
  public void setSelectedMovieIdx(int movieIdx) {
    this.selectedMovieIdx = movieIdx;
  }

  /**
   * Get movie of specified idx
   *
   * @param movieIdx:int
   * @return movie:Movie | null
   */
  //+ getMovie(movieldx : int) : Movie
  public Movie getMovie(int movieIdx) {
    return (this.movies.size() < 1 || movieIdx < 0) ? null : this.movies.get(movieIdx);
  }

  /**
   * Get idx of specified movie id
   *
   * @param movieId:int
   * @return movieIdx:int
   */
  // + getMovieIdx(movieId:int):int
  public int getMovieIdx(int movieId) {
    if (this.movies.size() < 1 || movieId < 0) return -1;
    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      if (movie.getId() == movieId) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Get movie list
   *
   * @return movies:List<Movie>
   */
  //+getMovies() : List <Movie>
  public List<Movie> getMovies() {
    return this.movies;
  }

  /**
   * Get movie list filtered by show staus
   *
   * @param showStatus:ShowStatus
   * @return movies:List<Movie>
   */
  //+getMovies(showStatus : ShowStatus): List<Movie>
  public List<Movie> getMovies(
      ShowStatus showStatus
  ) {
    List<Movie> movies = new ArrayList<Movie>();
    if (this.movies.size() < 1) return movies;

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      if (movie.getShowStatus().equals(showStatus)) movies.add(movie);
    }
    return movies;
  }

  /**
   * Append new movie to movie list
   *
   * @param id:int
   * @param title:String
   * @param synopsis:String
   * @param director:String
   * @param castList:List<String>
   * @param runtime:int
   * @param releaseDate:LocalDate
   * @param isBlockbuster:boolean
   * @param showStatus:ShowStatus
   * @param contentRating:ContentRating
   * @return movieIdx:int
   */
  //+ addMovie(id:int, title:String, synopsis:String, director:String, castList:List<String>, runtime:int, releaseDate:LocalDate, isBlockbuster:boolean, showStatus:ShowStatus, contentRating:ContentRating): int
  public int addMovie(int id, String title, String synopsis, String director, List<String> castList, int runtime, LocalDate releaseDate, boolean isBlockbuster, ShowStatus showStatus, Movie.ContentRating contentRating) {
    this.movies.add(new Movie(
        id,
        title,
        synopsis,
        director,
        castList,
        runtime,
        releaseDate,
        isBlockbuster,
        showStatus,
        contentRating
    ));
    return this.movies.size() - 1;
  }

  /**
   * Update movie of currently selected movie idx
   *
   * @param title:String
   * @param synopsis:String
   * @param director:String
   * @param castList:List<String>
   * @param runtime:int
   * @param releaseDate:LocalDate
   * @param isBlockbuster:boolean
   * @param showStatus:ShowStatus
   * @param contentRating:ContentRating
   * @return status:boolean
   */
  //+ updateMovie(title:String, synopsis:String, director:String, castList:List<String>, runtime:int, releaseDate:LocalDate, isBlockbuster:boolean, showStatus:ShowStatus, contentRating:ContentRating):boolean
  public boolean updateMovie(String title, String synopsis, String director, List<String> castList, int runtime, LocalDate releaseDate, boolean isBlockbuster, ShowStatus showStatus, ContentRating contentRating) {
    boolean status = false;
    if (this.movies.size() < 1 || this.selectedMovieIdx < 0) return status;

    Movie movie = this.getMovie(this.selectedMovieIdx);
    // Early return if movie does not exist
    if (movie == null) return status;

    this.movies.set(this.selectedMovieIdx, new Movie(
        movie.getId(),
        title,
        synopsis,
        director,
        castList,
        runtime,
        releaseDate,
        isBlockbuster,
        showStatus,
        contentRating
    ));
    status = true;

    return status;
  }

  /**
   * Remove movie of specified idx
   *
   * @param movieIdx:int
   * @return status:boolean
   */
  //+ removeMovie (movieldx : int) : boolean
  public boolean removeMovie(int movieIdx) {
    boolean status = false;
    if (this.movies.size() < 1 || movieIdx < 0) return status;

    // Early return if movie does not exist
    if (this.getMovie(movieIdx) == null) return status;

    this.movies.remove(movieIdx);
    status = true;
    return status;
  }

  /**
   * Print movie details of specified idx
   *
   * @param movieIdx:int
   */
  //+ printMovieDetails(movieldx : int) : void
  public void printMovieDetails(int movieIdx) {
    Movie movie = getMovie(movieIdx);
    if (movie == null) return;

    this.selectedMovieIdx = movieIdx;
    System.out.println(movie);
  }

  /**
   * Print list of movie titles
   */
  //+ printMovies() : void
  public void printMovies() {
    if (this.movies.isEmpty()) {
      System.out.println("No movies available");
      return;
    }

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      System.out.println("> " + i + " " + movie.getTitle());
    }
  }

////+ printMovies(showStatus ShowStatus) : void

//  // +getSelectedMovieIdx():int
//  public int getSelectedMovieIdx() {
//    return selectedMovieIdx;
//  }

//  //+ getSelectedMovie() : Movie
//  public Movie getSelectedMovie() {
//    return (this.movies.size() < 1 || this.selectedMovieIdx < 0) ? null : this.movies.get(this.selectedMovieIdx);
//  }
}
