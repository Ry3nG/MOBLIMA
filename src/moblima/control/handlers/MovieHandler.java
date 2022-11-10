package moblima.control.handlers;

import moblima.entities.Movie;
import moblima.entities.Movie.ContentRating;
import moblima.entities.Movie.ShowStatus;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;
import moblima.utils.datasource.Datasource;
import moblima.utils.datasource.MovieDatasource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static moblima.utils.Helper.colorizer;

/**
 * The type Movie handler.
 */
public class MovieHandler {
  /**
   * The Movies.
   */
  protected List<Movie> movies;
  /**
   * The Selected movie idx.
   */
  protected int selectedMovieIdx = -1;

  /**
   * Instantiates a new Movie handler.
   */
  public MovieHandler() {
    MovieDatasource dsMovie = new MovieDatasource();
    this.movies = dsMovie.getMovies();
  }

  /**
   * Gets selected movie.
   *
   * @return the selected movie
   */
//+ getSelectedMovie() : Movie
  public Movie getSelectedMovie() {
    return this.getMovie(this.selectedMovieIdx);
  }

  /**
   * Sets selected movie idx.
   *
   * @param movieIdx the movie idx
   */
// + setSelectedMovieIdx(movieIdx:int) :void
  public void setSelectedMovieIdx(int movieIdx) {
    this.selectedMovieIdx = movieIdx;
  }

  /**
   * Gets movie.
   *
   * @param movieIdx the movie idx
   * @return the movie
   */
//+ getMovie(movieldx : int) : Movie
  public Movie getMovie(int movieIdx) {
    return (this.movies.size() < 1 || movieIdx < 0) ? null : new Movie(this.movies.get(movieIdx));
  }

  /**
   * Gets movie idx.
   *
   * @param movieId the movie id
   * @return the movie idx
   */
// + getMovieIdx(movieId:int):int
  public int getMovieIdx(int movieId) {
    if (this.movies.size() < 1 || movieId < 0) return -1;
    Helper.logger("MovieHandler.getMovieIdx", "MovieId: " + movieId);

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      if (movie.getId() == movieId) {
        Helper.logger("MovieHandler.getMovieIdx", "Movie: " + movie);
        return i;
      }
    }

    return -1;
  }

  /**
   * Update movie rating boolean.
   *
   * @param movieId       the movie id
   * @param overallRating the overall rating
   * @return the boolean
   */
  public boolean updateMovieRating(int movieId, double overallRating) {
    Movie movie = this.getMovie(this.getMovieIdx(movieId));
    movie.setOverallRating(overallRating);

    return this.updateMovie(movie.getTitle(), movie.getSynopsis(), movie.getDirector(), movie.getCastList(), movie.getRuntime(), movie.getReleaseDate(), movie.isBlockbuster(), movie.getShowStatus(), movie.getContentRating(), movie.getOverallRating());
  }

  /**
   * Gets movies.
   *
   * @return the movies
   */
//+ getMovies() : List <Movie>
  public List<Movie> getMovies() {
    return this.movies;
  }

  /**
   * Gets movies.
   *
   * @param showStatus the show status
   * @return the movies
   */
//+ getMovies(showStatus : ShowStatus): List<Movie>
  public List<Movie> getMovies(ShowStatus showStatus) {
    List<Movie> movies = new ArrayList<Movie>();
    if (this.movies.size() < 1) return movies;

    for (Movie movie : this.movies) {
      if (movie.getShowStatus() == showStatus) movies.add(movie);
    }

    Helper.logger("MovieHandler.getMovies", "MOVIES: " + movies);
    return movies;
  }

  /**
   * Add movie int.
   *
   * @param id            the id
   * @param title         the title
   * @param synopsis      the synopsis
   * @param director      the director
   * @param castList      the cast list
   * @param runtime       the runtime
   * @param releaseDate   the release date
   * @param isBlockbuster the is blockbuster
   * @param showStatus    the show status
   * @param contentRating the content rating
   * @param overallRating the overall rating
   * @return the int
   */
//+ addMovie(id:int, title:String, synopsis:String, director:String, castList:List<String>, runtime:int, releaseDate:LocalDate, isBlockbuster:boolean, showStatus:ShowStatus, contentRating:ContentRating): int
  public int addMovie(int id, String title, String synopsis, String director, List<String> castList, int runtime, LocalDate releaseDate, boolean isBlockbuster, ShowStatus showStatus, Movie.ContentRating contentRating, int overallRating) {
    this.movies.add(new Movie(id, title, synopsis, director, castList, runtime, releaseDate, isBlockbuster, showStatus, contentRating, overallRating));

    //Serialize data
    this.saveMovies();

    return this.movies.size() - 1;
  }

  /**
   * Update movie boolean.
   *
   * @param title         the title
   * @param synopsis      the synopsis
   * @param director      the director
   * @param castList      the cast list
   * @param runtime       the runtime
   * @param releaseDate   the release date
   * @param isBlockbuster the is blockbuster
   * @param showStatus    the show status
   * @param contentRating the content rating
   * @param overallRating the overall rating
   * @return the boolean
   */
//+ updateMovie(title:String, synopsis:String, director:String, castList:List<String>, runtime:int, releaseDate:LocalDate, isBlockbuster:boolean, showStatus:ShowStatus, contentRating:ContentRating):boolean
  public boolean updateMovie(String title, String synopsis, String director, List<String> castList, int runtime, LocalDate releaseDate, boolean isBlockbuster, ShowStatus showStatus, ContentRating contentRating, double overallRating) {
    boolean status = false;
    if (this.movies.size() < 1 || this.selectedMovieIdx < 0) return status;

    Movie movie = this.getMovie(this.selectedMovieIdx);
    // Early return if movie does not exist
    if (movie == null) return status;

    this.movies.set(this.selectedMovieIdx, new Movie(movie.getId(), title, synopsis, director, castList, runtime, releaseDate, isBlockbuster, showStatus, contentRating, overallRating));

    //Serialize data
    this.saveMovies();
    status = true;

    return status;
  }

  /**
   * Remove movie boolean.
   *
   * @param movieIdx the movie idx
   * @return the boolean
   */
//+ removeMovie (movieldx : int) : boolean
  public boolean removeMovie(int movieIdx) {
    boolean status = false;
    if (this.movies.size() < 1 || movieIdx < 0) return status;

    // Early return if movie does not exist
    if (this.getMovie(movieIdx) == null) return status;

    this.movies.remove(movieIdx);

    //Serialize data
    this.saveMovies();

    status = true;
    return status;
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
    String header = "/// MOVIE DETAILS ///";
    System.out.println(colorizer(header, Preset.HIGHLIGHT));
    System.out.println(colorizer(movie.toString(showTruncated), Preset.HIGHLIGHT));

    return header + "\n" + movie;
  }

  /**
   * Print movies.
   */
//+ printMovies() : void
  public void printMovies() {
    if (this.movies.isEmpty()) {
      System.out.println("No movies available");
      return;
    }

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      System.out.println("> " + (i + 1) + " " + movie.getTitle());
    }
  }

  /**
   * Print movies.
   *
   * @param movies the movies
   */
//+ printMovies(movies:List<Movie>) : void
  public void printMovies(List<Movie> movies) {
    if (movies.isEmpty()) {
      System.out.println("No movies available");
      return;
    }

    for (int i = 0; i < movies.size(); i++) {
      Movie movie = movies.get(i);
      System.out.println("> " + (i + 1) + " " + movie.getTitle());
    }
  }

  /**
   * Save movies boolean.
   *
   * @return the boolean
   */
//# saveCustomers():boolean
  protected boolean saveMovies() {
    return Datasource.serializeData(this.movies, "movies.csv");
  }

}
