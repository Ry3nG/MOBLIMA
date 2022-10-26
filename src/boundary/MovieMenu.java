package boundary;

import control.handlers.MovieHandler;
import moblima.entities.Movie;
import moblima.entities.Movie.ContentRating;
import moblima.entities.Movie.ShowStatus;
import utils.Helper;
import utils.Helper.Preset;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diogonunes.jcolor.Ansi.colorize;

public class MovieMenu extends Menu {
  private static boolean showLimitedMovies = true;
  private static MovieHandler handler;
  private static MovieMenu instance;

  private MovieMenu() {
    super();
    handler = new MovieHandler();
    this.refreshMenu(this.getMovieMenu());
  }

  public static MovieMenu getInstance(boolean showLimited) {
    showLimitedMovies = showLimited;
    if (instance == null) instance = new MovieMenu();
    return instance;
  }

  /**
   * Get movie handler
   *
   * @return movieHandler:MovieHandler
   */
  //+ getHandler():MovieHandler
  public static MovieHandler getHandler() {
    return handler;
  }

  @Override
  public void showMenu() {
    Helper.logger("MovieMenu.showMenu", "Displaying menu . . .");
    this.displayMenu();
  }

  /**
   * Filter movies by show status: NOW_SHOWING
   *
   * @return movies:List<Movie>
   */
  //+ getViewableMovie(): List<Movie>
  public List<Movie> getViewableMovies() {
    return showLimitedMovies ? handler.getMovies(ShowStatus.NOW_SHOWING) : handler.getMovies();
  }

  /**
   * Get the updated movie list to be displayed
   *
   * @return menuMap:LinkedHashMap<String, Runnable>
   */
  //+ getMovieMenu(showLimited : boolean):LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getMovieMenu() {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    List<Movie> movies = this.getViewableMovies();
    Helper.logger("MovieMenu.getMovieMenu", "SHOW LIMITED: " + showLimitedMovies);
    Helper.logger("MovieMenu.getMovieMenu", "MOVIES: " + movies);

    for (int i = 0; i < movies.size(); i++) {
      Movie movie = movies.get(i);
      int movieIdx = handler.getMovieIdx(movie.getId());
      menuMap.put((i + 1) + ". " + movie.getTitle(), () -> {
        handler.setSelectedMovieIdx(movieIdx);
        handler.printMovieDetails(movieIdx);
      });
    }
    menuMap.put((menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
    return menuMap;
  }

  /**
   * Retrieves the user movie selection idx
   *
   * @return selectedMovieIdx:int
   */
  //+ selectMovieIdx():int
  public int selectMovieIdx() {
    this.refreshMenu(this.getMovieMenu());

    // Retrieve user selection idx for movies
    List<Movie> movies = this.getViewableMovies();
    int selectedIdx = this.getListSelectionIdx(movies);

    Helper.logger("MovieMenu.selectMovieIdx", "Max: " + (this.menuMap.size()));
    Helper.logger("MovieMenu.selectMovieIdx", "Selected: " + selectedIdx);

    // Display movie details upon selection
    this.menuMap.get(this.menuMap.keySet().toArray()[selectedIdx]).run();

    // Return to previous menu
    if (selectedIdx == (this.menuMap.size() - 1)) return -1;

    Movie movie = movies.get(selectedIdx);
    int movieIdx = handler.getMovieIdx(movie.getId());
    handler.setSelectedMovieIdx(movieIdx);
    Helper.logger("MovieMenu.selectMovieIdx", "Selected Movie Idx: " + handler.getMovie(movieIdx).getId());

    return selectedIdx;
  }

  /**
   * Retrieves the user movie selection idx
   *
   * @return selectedMovieIdx:int
   */
  //+ selectEditableAction():int
  public boolean selectEditableAction(int movieIdx) {
    boolean status = false;

    Movie movie = handler.getMovie(movieIdx);
    if (movie == null) return status;

    List<String> proceedOptions = new ArrayList<String>() {
      {
        add("Set blockbuster status");
        add("Set showing status");
        add("Set content rating");
        add("Discard changes");
        add("Remove movie");
        add("Save changes & return");
        add("Return to previous menu");
      }
    };

    while (!status) {
      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);

      // Save changes & return OR Return to previous menu
      if (proceedSelection >= proceedOptions.size() - 3) {
        // Save changes
        if (proceedSelection == proceedOptions.size() - 2) {
          handler.updateMovie(
              movie.getTitle(),
              movie.getSynopsis(),
              movie.getDirector(),
              movie.getCastList(),
              movie.getRuntime(),
              movie.getReleaseDate(),
              movie.isBlockbuster(),
              movie.getShowStatus(),
              movie.getContentRating()
          );
          status = true;
        }
        // Remove movie
        else if (proceedSelection == proceedOptions.size() - 3) {
          System.out.println(colorize("[UPDATED] Movie removed", Preset.SUCCESS.color));
          status = handler.removeMovie(movieIdx);
        }

        System.out.println("\t>>> " + "Returning to previous menu...");
        return status;
      }

      // Discard changes
      else if (proceedSelection == proceedOptions.size() - 4) {
        System.out.println(colorize("[REVERTED] Changes discarded", Preset.SUCCESS.color));
        movie = handler.getMovie(movieIdx);
        System.out.println(movie);
      }

      // Set blockbuster status
      else if (proceedSelection == 0) {
        String prevStatus = (movie.isBlockbuster() ? "Blockbuster" : "Non-Blockbuster");
        System.out.println("[CURRENT] Blockbuster Status: " + prevStatus);

        //TODO: Extract as separate function
        List<String> updateOptions = new ArrayList<String>() {
          {
            add("Blockbuster");
            add("Non-Blockbuster");
          }
        };

        System.out.println("Set to:");
        this.displayMenuList(updateOptions);
        int selectionIdx = getListSelectionIdx(updateOptions, false);

        movie.setBlockbuster((selectionIdx == 0));
        String curStatus = (movie.isBlockbuster() ? "Blockbuster" : "Non-Blockbuster");

        if (prevStatus.equals(curStatus)) {
          System.out.println(colorize("[NO CHANGE] Blockbuster Status: " + prevStatus, Preset.SUCCESS.color));
        } else {
          System.out.println(colorize("[UPDATED] Blockbuster Status: " + prevStatus + " -> " + curStatus, Preset.SUCCESS.color));
        }
      }

      // Set showing status
      else if (proceedSelection == 1) {
        ShowStatus prevStatus = movie.getShowStatus();
        System.out.println("[CURRENT] Showing Status: " + prevStatus);

        //TODO: Extract as separate function
        List<ShowStatus> showStatuses = new ArrayList<ShowStatus>(EnumSet.allOf(ShowStatus.class));
        List<String> updateOptions = Stream.of(ShowStatus.values())
            .map(Enum::toString)
            .collect(Collectors.toList());

        System.out.println("Set to:");
        this.displayMenuList(updateOptions);
        int selectionIdx = getListSelectionIdx(updateOptions, false);

        movie.setShowStatus(showStatuses.get(selectionIdx));
        ShowStatus curStatus = movie.getShowStatus();

        if (prevStatus.equals(curStatus)) {
          System.out.println(colorize("[NO CHANGE] Showing Status: " + prevStatus, Preset.SUCCESS.color));
        } else {
          System.out.println(colorize("[UPDATED] Showing Status: " + prevStatus + " -> " + curStatus, Preset.SUCCESS.color));
        }
      }

      // Set content rating
      else if (proceedSelection == 2) {
        ContentRating prevStatus = movie.getContentRating();
        System.out.println("[CURRENT] Current Rating: " + prevStatus);

        //TODO: Extract as separate function
        List<String> updateOptions = Stream.of(ContentRating.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        System.out.println("Set to:");
        this.displayMenuList(updateOptions);
        int selectionIdx = getListSelectionIdx(updateOptions, false);

        List<ContentRating> contentRatings = new ArrayList<ContentRating>(EnumSet.allOf(ContentRating.class));
        movie.setContentRating(contentRatings.get(selectionIdx));
        ContentRating curStatus = movie.getContentRating();

        if (prevStatus.equals(curStatus)) {
          System.out.println(colorize("[NO CHANGE] Current Rating: " + prevStatus, Preset.SUCCESS.color));
        } else {
          System.out.println(colorize("[UPDATED] Current Rating: " + prevStatus + " -> " + curStatus, Preset.SUCCESS.color));
        }
      }
    }

    return status;
  }
}
