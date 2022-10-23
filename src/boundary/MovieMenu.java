package boundary;

import control.MovieHandler;
import entity.Menu;
import tmdb.entities.Movie;
import utils.Helper;

import java.util.LinkedHashMap;
import java.util.List;

public class MovieMenu extends Menu {
  private static MovieHandler handler;
  private static MovieMenu instance;

  private MovieMenu() {
    super();
    this.handler = new MovieHandler();
    this.refreshMenu(this.getMovieMenu());
  }

  public static MovieMenu getInstance() {
    if (instance == null) instance = new MovieMenu();
    return instance;
  }

  public static MovieHandler getHandler() {
    return handler;
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

  public LinkedHashMap<String, Runnable> getMovieMenu() {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    List<Movie> movies = this.handler.getMovies();
    Helper.logger("MovieMenu.getMovieMenu", "Movies: " + movies);
    for (int i = 0; i < movies.size(); i++) {
      Movie movie = movies.get(i);
      int movieIdx = i;
      menuMap.put((i + 1) + ". " + movie.getTitle(), () -> {
        this.handler.setSelectedMovieIdx(movieIdx);
        this.handler.printMovieDetails(movieIdx);
      });
    }
    menuMap.put((menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
    return menuMap;
  }

  public int selectMovieIdx() {
//    this.refreshMenu(this.getMovieMenu());

    // Retrieve user selection idx for movies
    List<Movie> movies = this.handler.getMovies();
    int selectedIdx = this.getListSelectionIdx(movies);

    Helper.logger("MovieMenu.selectMovieIdx", "Max: " + (this.menuMap.size()));
    Helper.logger("MovieMenu.selectMovieIdx", "Selected: " + selectedIdx);

    // Display movie details upon selection
    this.menuMap.get(this.menuMap.keySet().toArray()[selectedIdx]).run();

    // Return to previous menu
    if (selectedIdx == (this.menuMap.size() - 1)) return -1;

    this.handler.setSelectedMovieIdx(selectedIdx);

    System.out.println(this.handler.getMovie(selectedIdx).getId());

    return selectedIdx;
  }
}
