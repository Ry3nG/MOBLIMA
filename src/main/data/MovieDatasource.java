package main.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.entity.Movie;
import main.entity.Review;
import main.entity.Showtime;
import main.types.MovieConstants;
import main.utils.Constants;
import main.utils.Datasource;
import main.utils.Helper;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieDatasource extends Datasource {

  /**
   * Parse and retrieve list of review from serialized data
   * Will not request from API if no serialized data available
   *
   * @return review:List<Review>
   */
  public List<Review> getReviews() {
    List<Review> reviews = new ArrayList<Review>();

    String fileName = "reviews.csv";
    JsonArray reviewList = Datasource.readArrayFromCsv(fileName);

    if (reviewList == null) {
      Helper.logger("MovieDatasource.getReviews", "No serialized data available");
      return reviews;
    }

    for (JsonElement review : reviewList) {
      JsonObject r = review.getAsJsonObject();

      String id = r.get("id").getAsString();
      int movieId = r.get("movieId").getAsInt();
      String author = r.get("author").getAsString();
      String content = r.get("content").getAsString();
      int rating = r.get("rating").getAsInt();

      /// Initialize and append Review object
      reviews.add(new Review(
          id,
          movieId,
          content,
          author,
          rating
      ));
    }

    return reviews;
  }

  /**
   * Parse and retrieve list of movies from serialized data
   * Requests from API if no serialized data available
   *
   * @return movies:List<Movie>
   */
  public List<Movie> getMovies() {
    List<Movie> movies = new ArrayList<Movie>();

    String fileName = "movies.csv";
    JsonArray movieList = Datasource.readArrayFromCsv(fileName);

    if (movieList == null) {
      if (Constants.DEBUG_MODE && !API_KEY.isEmpty()) {
        Helper.logger("MovieDatasource.getMovies", "No serialized data available, requesting from API");
        movies = this.fetchMovies();
      }
      return movies;
    }

    for (JsonElement movie : movieList) {
      JsonObject m = movie.getAsJsonObject();

      int id = m.get("id").getAsInt();
      String title = m.get("title").getAsString();
      String synopsis = m.get("synopsis").getAsString();
      String releaseDate = m.get("releaseDate").getAsString();
      int runtime = m.get("runtime").getAsInt();

      String status = m.get("showStatus").getAsString();
      boolean isValidStatus = EnumUtils.isValidEnum(MovieConstants.ShowStatus.class, status);
      if (!isValidStatus) continue;
      MovieConstants.ShowStatus showStatus = MovieConstants.ShowStatus.valueOf(status);

      String director = m.get("director").getAsString();
      List<String> castList = List.of(StringUtils.substringBetween(m.get("cast").getAsString(), "[", "]").split(","));
      boolean isBlockbuster = m.get("isBlockbuster").getAsBoolean();

      List<String> reviewIds = List.of(StringUtils.substringBetween(m.get("reviewIds").getAsString(), "[", "]").split(","));

      /// Initialize and append Movie object
      movies.add(new Movie(
          id,
          title,
          synopsis,
          releaseDate,
          runtime,
          showStatus,
          director,
          castList,
          isBlockbuster,
          reviewIds,
          new ArrayList<Showtime>()
      ));
    }

    Helper.logger("MovieDatasource.getMovies", "Total movies: " + movies.size());
    return movies;
  }

  // Fetch list of movies (and it's reviews) in the theaters
  // https://api.themoviedb.org/3/movie/now_playing?api_key=&language=en-US
  public List<Movie> fetchMovies() {
    List<Movie> movies = new ArrayList<Movie>();
    List<Review> reviews = new ArrayList<Review>();

    // API Request to get the list of movies in theaters
    String queryMovieList = "/movie/now_playing?language=en-US";
    JsonArray movieList = Datasource.requestPagination(queryMovieList, 1, 2);
    if (movieList == null) return movies;

    // Iterate through API payload to map as Movie object
    for (JsonElement movie : movieList) {
      JsonObject m = movie.getAsJsonObject();

      /// Raw values
      int id = m.get("id").getAsInt();
      String title = m.get("title").getAsString();
      String synopsis = m.get("overview").getAsString();
      String releaseDate = m.get("release_date").getAsString();
      int voteAverage = m.get("vote_average").getAsInt();

      // API request to get movie details
      String queryMovieDetails = "/movie/" + id + "?append_to_response=credits,reviews";
      JsonObject jsonDetails = Datasource.request(queryMovieDetails);

      /// Raw values
      int runtime = jsonDetails.get("runtime").getAsInt();
      String status = jsonDetails.get("status").getAsString();
      JsonArray castArray = jsonDetails.get("credits").getAsJsonObject().get("cast").getAsJsonArray();
      JsonArray crewArray = jsonDetails.get("credits").getAsJsonObject().get("crew").getAsJsonArray();
      JsonArray reviewArray = jsonDetails.get("reviews").getAsJsonObject().get("results").getAsJsonArray();

      /// Derived
      //// Blockbuster Status
      boolean isBlockbuster = voteAverage >= 7.5;

      //// ShowingStatus
      boolean isValidStatus = EnumUtils.isValidEnum(MovieConstants.ShowStatus.class, status);
      if (!isValidStatus) continue;
      MovieConstants.ShowStatus showingStatus = MovieConstants.ShowStatus.valueOf(status);

      //// Cast List
      List<String> castList = new ArrayList<String>();
      if (castArray.size() > 0) {
        for (JsonElement cast : castArray) {
          JsonObject c = cast.getAsJsonObject();
          String name = c.get("name").getAsString();
          String job = c.get("known_for_department").getAsString();

          boolean isValidRole = EnumUtils.isValidEnum(MovieConstants.Role.class, job);
          if (!isValidRole) continue;

          MovieConstants.Role role = MovieConstants.Role.valueOf(job);
          if (role == MovieConstants.Role.Acting) castList.add(name);
        }
      }

      //// Director
      String director = null;
      if (crewArray.size() > 0) {
        for (JsonElement crew : crewArray) {
          JsonObject c = crew.getAsJsonObject();
          String name = c.get("name").getAsString();
          String job = c.get("job").getAsString();

          boolean isValidRole = EnumUtils.isValidEnum(MovieConstants.Role.class, job);
          if (!isValidRole) continue;

          MovieConstants.Role role = MovieConstants.Role.valueOf(job);
          if (role == MovieConstants.Role.Director) {
            director = name;
            break;
          }
        }
      }

      /// Reviews
      List<String> reviewIds = new ArrayList<String>();
      if (reviewArray.size() > 0) {
        for (JsonElement review : reviewArray) {
          JsonObject r = review.getAsJsonObject();
          String rId = r.get("id").getAsString();
          String content = r.get("content").getAsString();
          String author = r.get("author").getAsString();
          String username = r.get("author_details").getAsJsonObject().get("username").getAsString();
          if (author.isEmpty()) author = username;
          JsonElement ratingObject = r.get("author_details").getAsJsonObject().get("rating");
          if (ratingObject.isJsonNull()) continue;
          int rating = ratingObject.getAsInt();

          reviews.add(new Review(
              rId,
              id,
              content,
              author,
              rating
          ));
          reviewIds.add(rId);
        }
      }

      /// Initialize and append Movie object
      movies.add(new Movie(
          id,
          title,
          synopsis,
          releaseDate,
          runtime,
          showingStatus,
          director,
          castList,
          isBlockbuster,
          reviewIds,
          new ArrayList<Showtime>()
      ));
    }
    Helper.logger("MovieDatasource.fetchMovies", "Total movies: " + movies.size());

    // Serialize data to CSV
    String outputFileName = "movies.csv";
    Datasource.serializeDataToCSV(Datasource.convertToJsonArray(movies), outputFileName, Constants.DEBUG_MODE);
    Helper.logger("MovieDatasource.fetchMovies", "Exported to " + outputFileName);

    outputFileName = "reviews.csv";
    Datasource.serializeDataToCSV(Datasource.convertToJsonArray(reviews), outputFileName, Constants.DEBUG_MODE);
    Helper.logger("MovieDatasource.fetchMovies", "Exported to " + outputFileName);


    return movies;
  }
}
