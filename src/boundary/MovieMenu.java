package boundary;

import control.handlers.MovieHandler;
import moblima.entities.Movie;
import moblima.entities.Movie.ContentRating;
import moblima.entities.Movie.ShowStatus;
import moblima.entities.Review;
import utils.Helper;
import utils.Helper.Preset;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diogonunes.jcolor.Ansi.colorize;

public class MovieMenu extends Menu {
  private static boolean showLimitedMovies = true;
  private static boolean showReviews = true;
  private static MovieHandler handler;
  private static MovieMenu instance;

  private MovieMenu() {
    super();
    handler = new MovieHandler();
    this.refreshMenu(this.getMovieMenu());
  }

  public static MovieMenu getInstance() {
    return getInstance(showLimitedMovies, showReviews);
  }

  public static MovieMenu getInstance(boolean showLimited, boolean showReviews) {
    showLimitedMovies = showLimited;
    showReviews = showReviews;
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
    return this.getMovieMenu(null);
  }

  public void updateReviewMenu(Runnable addMovieRunnable) {
    this.refreshMenu(this.getMovieMenu(addMovieRunnable));
  }

  /**
   * Get the updated movie list to be displayed with runnable injection
   *
   * @param addMovieRunnable:Runnable
   * @return menuMap:LinkedHashMap<String, Runnable>
   */
  //+ getMovieMenu(addMovieRunnable : Runnable):LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getMovieMenu(Runnable addMovieRunnable) {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    List<Movie> movies = this.getViewableMovies();
    Helper.logger("MovieMenu.getMovieMenu", "SHOW LIMITED: " + showLimitedMovies);
    Helper.logger("MovieMenu.getMovieMenu", "SHOW REVIEWS: " + showReviews);
    Helper.logger("MovieMenu.getMovieMenu", "MOVIES: " + movies);
    Helper.logger("MovieMenu.getMovieMenu", "RUNNABLE: " + (addMovieRunnable != null));

    for (int i = 0; i < movies.size(); i++) {
      Movie movie = movies.get(i);
      int movieIdx = handler.getMovieIdx(movie.getId());
      menuMap.put((i + 1) + ". " + movie.getTitle(), () -> {
        handler.setSelectedMovieIdx(movieIdx);
        handler.printMovieDetails(movieIdx);

        if (showReviews) {
          if (addMovieRunnable != null) addMovieRunnable.run();
          else this.selectReviewOptions(handler.getSelectedMovie().getId(), "", "");
        }
      });
    }
    menuMap.put((menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
    return menuMap;
  }

  /**
   * Enable user to view movie reviews
   *
   * @return status:boolean
   */
  //+ selectReviewOptions():boolean
  public boolean selectReviewOptions(int movieId, String reviewerName, String reviewerId) {
    boolean status = false;
    boolean canAddReview = !reviewerName.isEmpty() && !reviewerId.isEmpty();
    String reviewId = handler.checkIfAlreadyReviewed(reviewerId, movieId);
    Helper.logger("MovieMenu.selectReviewOptions", "canAddReview: " + canAddReview);

    List<String> proceedOptions = new ArrayList<String>() {
      {
        add("View all reviews");
        add("Add review");
        add("Return to previous menu");
      }
    };

    if (!canAddReview) proceedOptions.remove(1);
    else if (reviewId != null) proceedOptions.set(1, "Update review");


    while (!status) {
      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);


      // Return to previous menu
      if (proceedSelection >= proceedOptions.size() - 1) {
        System.out.println("\t>>> " + "Returning to previous menu...");
        return status;
      }

      // View all reviews
      else if (proceedSelection == 0) {
        List<Review> movieReviews = handler.getMovieReviews(movieId);
        if (movieReviews.size() < 1) {
          System.out.println("No reviews available");
          continue;
        }

        for (Review review : movieReviews) System.out.println(review);

        // Automatically revert to previous menu
        if (!canAddReview) status = true;
      }

      // Add Review
      else if (canAddReview && proceedSelection == 1) {
        int reviewIdx = -1;

        // Update Review
        if (reviewId != null) {
          reviewIdx = handler.getReviewIdx(reviewId);
          if (reviewIdx < 0) {
            reviewId = null;
            continue;
          }

          handler.setSelectedReviewIdx(reviewIdx);
          System.out.println(handler.getSelectedReview());
          status = this.selectUpdatableAction(reviewId);
          break;
        }

        int rating = -1;
        String review = null;
        System.out.println("Add Movie Review");

        while (reviewIdx < 0) {
          scanner = new Scanner(System.in).useDelimiter("\n");

          try {
            // Prompt for rating [1-5]
            while (rating < 0) {
              System.out.print("Rating: ");

              if (scanner.hasNext()) {
                String input = scanner.nextLine();
                rating = Helper.parseStrToInt(input);

                // VALIDATION: Verify valid rating
                if (rating < 1 || rating > 5) {
                  System.out.println(colorize("Invalid rating value, rate from 1 – 5 [best].", Preset.ERROR.color));
                  rating = -1;
                  continue;
                }
              }
            }

            // Prompt for review
            System.out.print("Review: ");
            if (review == null) {
              review = scanner.next().trim();
            }

            // Add to reviews
            reviewIdx = handler.addReview(movieId, review, rating, reviewerName, reviewerId);
            if (reviewIdx < 0) throw new Exception("Unable to add review");

            System.out.println(colorize("Successfully added review", Preset.SUCCESS.color));

            // Flush excess scanner buffer
            scanner = new Scanner(System.in);

            status = true;
          } catch (Exception e) {
            System.out.println(colorize(e.getMessage(), Preset.ERROR.color));
            review = null;
            rating = -1;
          }
        }
      }
    }

    return status;
  }

  //+ selectReviewIdx():int
  public int selectReviewIdx(List<Review> reviews) {
    int reviewIdx = -1;
    if (reviews.size() < 1) {
      System.out.println("No reviews available . . .");
      return reviewIdx;
    }

    List<String> reviewList = reviews.stream().map(r -> {
      int mIdx = handler.getMovieIdx(r.getMovieId());
      Movie m = handler.getMovie(mIdx);
      return m.getTitle();
    }).collect(Collectors.toList());
    reviewList.add((reviewList.size()), "Return to previous menu");

    System.out.println("Next steps:");
    this.displayMenuList(reviewList);
    int listSelectionIdx = getListSelectionIdx(reviewList, false);

    // Return to previous menu
    if (listSelectionIdx == (reviewList.size() - 1)) {
      System.out.println("\t>>> " + "Returning to previous menu...");
      return reviewIdx;
    }

    Review review = reviews.get(listSelectionIdx);
    reviewIdx = handler.getReviewIdx(review.getId());
    if (reviewIdx < 0) return reviewIdx;

    handler.setSelectedReviewIdx(reviewIdx);
    Helper.logger("MovieMenu.selectReviewIdx", "Selected Review Idx: " + reviewIdx);

    return reviewIdx;
  }

  /**
   * Retrieves the user movie selection idx
   *
   * @return selectedMovieIdx:int
   */
  //+ selectMovieIdx():int
  public int selectMovieIdx() {
    showReviews = false;
    this.refreshMenu(this.getMovieMenu());

    // Retrieve user selection idx for movies
    List<Movie> movies = this.getViewableMovies();
    int selectedIdx = this.getListSelectionIdx(movies);

    Helper.logger("MovieMenu.selectMovieIdx", "Max: " + (this.menuMap.size()));
    Helper.logger("MovieMenu.selectMovieIdx", "Selected: " + selectedIdx);

    // Display movie details upon selection
    this.menuMap.get(this.menuMap.keySet().toArray()[selectedIdx]).run();
    this.awaitContinue();

    // Return to previous menu
    if (selectedIdx == (this.menuMap.size() - 1)) return -1;

    Movie movie = movies.get(selectedIdx);
    int movieIdx = handler.getMovieIdx(movie.getId());
    handler.setSelectedMovieIdx(movieIdx);
    Helper.logger("MovieMenu.selectMovieIdx", "Selected Movie Idx: " + handler.getMovie(movieIdx).getId());

    // Revert to menu with review
    showReviews = true;
    this.refreshMenu(this.getMovieMenu());

    return movieIdx;
  }

  public boolean selectUpdatableAction(String reviewId) {
    boolean status = false;

    Review review = handler.getReview(reviewId);
    if (review == null) return status;

    List<String> proceedOptions = new ArrayList<String>() {
      {
        add("Set rating");
        add("Set review");
        add("Discard changes");
        add("Remove review");
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
          Helper.logger("MovieMenu.selectUpdatableAction", "Saved Review: \n" + review);
          status = handler.updateReview(review.getRating(), review.getReview());
        }
        // Remove movie
        else if (proceedSelection == proceedOptions.size() - 3) {
          System.out.println(colorize("[UPDATED] Review removed", Preset.SUCCESS.color));
          status = handler.removeReview(reviewId);
        }

        System.out.println("\t>>> " + "Returning to previous menu...");
        return status;
      }

      // Discard changes
      else if (proceedSelection == proceedOptions.size() - 4) {
        System.out.println(colorize("[REVERTED] Changes discarded", Preset.SUCCESS.color));
        review = handler.getReview(reviewId);
        System.out.println(review);
      }

      // Set rating
      else if (proceedSelection == 0) {
        int prevStatus = review.getRating();
        System.out.println("[CURRENT] Rating: " + prevStatus + " / 5");

        //TODO: Extract as separate function
        scanner = new Scanner(System.in).useDelimiter("\n");

        // Prompt for rating [1-5]
        int curStatus = -1;
        while (curStatus < 0) {
          System.out.print("Set to:");
          if (scanner.hasNext()) {
            String input = scanner.nextLine();
            curStatus = Helper.parseStrToInt(input);

            // VALIDATION: Verify valid rating
            if (curStatus < 1 || curStatus > 5) {
              System.out.println(colorize("Invalid rating value, rate from 1 – 5 [best].", Preset.ERROR.color));
              curStatus = -1;
              continue;
            }

            review.setRating(curStatus);
          }
        }


        if (prevStatus == curStatus) {
          System.out.println(colorize("[NO CHANGE] Rating: " + prevStatus, Preset.SUCCESS.color));
        } else {
          System.out.println(colorize("[UPDATED] Rating: " + prevStatus + " -> " + curStatus, Preset.SUCCESS.color));
        }
      }

      // Set review
      else if (proceedSelection == 1) {
        String prevStatus = review.getReview();
        System.out.println("[CURRENT] Review: " + prevStatus);

        //TODO: Extract as separate function
        scanner = new Scanner(System.in).useDelimiter("\n");

        System.out.print("Set to:");
        String curStatus = scanner.next().trim();

        review.setReview(curStatus);

        if (prevStatus.equals(curStatus)) {
          System.out.println(colorize("[NO CHANGE] Review: " + prevStatus, Preset.SUCCESS.color));
        } else {
          System.out.println(colorize("[UPDATED] Review: " + prevStatus + " -> " + curStatus, Preset.SUCCESS.color));
        }
      }

    }

    return status;
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
          handler.updateMovie(movie.getTitle(), movie.getSynopsis(), movie.getDirector(), movie.getCastList(), movie.getRuntime(), movie.getReleaseDate(), movie.isBlockbuster(), movie.getShowStatus(), movie.getContentRating(), movie.getOverallRating());
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
        List<String> updateOptions = Stream.of(ShowStatus.values()).map(Enum::toString).collect(Collectors.toList());

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
        List<String> updateOptions = Stream.of(ContentRating.values()).map(Enum::name).collect(Collectors.toList());
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
