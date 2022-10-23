package control;

import tmdb.control.MovieDatasource;
import tmdb.entities.Movie;
import tmdb.entities.Movie.ShowStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieHandler {
  private MovieDatasource dsMovie = new MovieDatasource();
  private List<Movie> movies = new ArrayList<Movie>();
  private int selectedMovieIdx = -1;

  public MovieHandler(){
    this.dsMovie = new MovieDatasource();
    this.movies = dsMovie.getMovies();
  }

  // +getSelectedMovieIdx():int
  public int getSelectedMovieIdx() {
    return selectedMovieIdx;
  }

  // + setSelectedMovieIdx(movieIdx:int) :void
  public void setSelectedMovieIdx(int movieIdx){
    this.selectedMovieIdx = movieIdx;
  }

  //+ getSelectedMovie() : Movie
  public Movie getSelectedMovie() {
    return (this.movies.size() < 1 || this.selectedMovieIdx < 0) ? null : this.movies.get(this.selectedMovieIdx);
  }

  //+ getMovie(movieldx : int) : Movie
  public Movie getMovie(int movieIdx) {
    return (this.movies.size() < 1 || movieIdx < 0) ? null : this.movies.get(movieIdx);
  }

  // + getMovieIdx(movieId:int):int
  public int getMovieIdx(int movieId) {
    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      if (movie.getId() == movieId) {
        return i;
      }
    }
    return -1;
  }

  //+getMovies() : List <Movie>
  public List<Movie> getMovies() {
    return this.movies;
  }

  //+getMovies(showStatus : ShowStatus): List< Movie>
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

  //+addMovie(movie : Movie) :int
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

  //+ updateMovie(movie : Movie):boolean
  public boolean updateMovie(String title, String synopsis, String director, List<String> castList, int runtime, LocalDate releaseDate, boolean isBlockbuster, ShowStatus showStatus, Movie.ContentRating contentRating) {
    boolean status = false;
    if (this.movies.size() < 1 || this.selectedMovieIdx < 0) return status;

    Movie movie = this.movies.get(this.selectedMovieIdx);

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

  //+ removeMovie (movieldx : int) : boolean
  public boolean removeMovie(int movieIdx) {
    boolean status = false;
    if (this.movies.size() < 1 || movieIdx < 0) return status;

    this.movies.remove(movieIdx);
    status = true;
    return status;
  }

//-+ printMovieDetails(movieldx : int) : void
  public void printMovieDetails(int movieIdx){
    Movie movie = getMovie(movieIdx);
    if(movie == null) return;
    this.selectedMovieIdx = movieIdx;
    System.out.println(movie.toString());
  }

//+ printMovies() : void
  public void printMovies(){
    if(this.movies.isEmpty()){
      System.out.println("No movies available");
      return;
    }

    for (int i = 0; i < this.movies.size(); i++) {
      Movie movie = this.movies.get(i);
      System.out.println("> " + i + " " + movie.getTitle());
    }
  }

//+ printMovies(showStatus ShowStatus) : void
}
