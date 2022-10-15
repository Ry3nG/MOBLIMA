package main.boundary;

import main.data.MovieDatasource;
import main.entity.Movie;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Movie Goer's Menu
 *
 * @author SS11 Group 1
 * @version 1.0
 * @since 2022/10/11
 */
public class CustomerMenu extends Menu {
  private static final MovieDatasource dsMovie = new MovieDatasource();
  private final List<Movie> movies;

  public CustomerMenu() {
    super();
    this.movies = dsMovie.fetchMovies();
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("1. Search/List Movies", () -> listMovies());
      put("2. View movie details – including reviews and ratings", () -> {
      });
      put("3. Check seat availability and selection of seat/s.", () -> {
      });
      put("4. Book and purchase ticket", () -> {
      });
      put("5. View booking history", () -> {
      });
      put("6. List the Top 5 ranking by ticket sales OR by overall reviewers’ ratings", () -> {
      });
      put("7. Return to main menu", () -> System.out.println("\t>>> " + "Returning to main menu..."));
    }};
  }

  public void listMovies() {
    if (this.movies.isEmpty()) {
      System.out.println("No movies available");
      return;
    }

    for (Movie m : this.movies) {
      System.out.println(m.getTitle());
    }
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }
}
