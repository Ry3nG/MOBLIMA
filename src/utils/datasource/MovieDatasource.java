package utils.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entities.Movie;
import entities.Movie.ContentRating;
import entities.Movie.ShowStatus;
import entities.Review;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import utils.Constants;
import utils.Helper;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static utils.deserializers.LocalDateDeserializer.dateFormatter;

/**
 * Real-world datasource to fetch movie data from the TMDB api
 *
 * @author Crystal Cheong
 */
public class MovieDatasource extends Datasource {
  protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public MovieDatasource() {
    ENDPOINT = "https://api.themoviedb.org/3";
    API_KEY = Constants.getEnv("TMDB_API_KEY");
  }

  /**
   * Parse and retrieve list of reviews from serialized data
   *
   * @return reviews:List<Review>
   */
  public List<Review> getReviews() {
    List<Review> reviews = new ArrayList<Review>();

    //Source from serialized datasource
    String fileName = "reviews.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("MovieDatasource.getReviews", "Null and void filename provided, no data retrieved.");
      return reviews;
    }

    JsonArray reviewList = Datasource.readArrayFromCsv(fileName);
    if (reviewList == null) {
      Helper.logger("MovieDatasource.getReviews", "No serialized data available");
      return reviews;
    }

    String strReviewList = Datasource.getGson().toJson(reviewList);
    Type typeReviewList = new TypeToken<List<Review>>() {
    }.getType();
    reviews = Datasource.getGson().fromJson(strReviewList, typeReviewList);

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
      if (API_KEY != null) {
        Helper.logger("MovieDatasource.getMovies", "No serialized data available, requesting from API");
        try {
          movies = this.fetchMovies();
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
      return movies;
    }

    for (JsonElement movie : movieList) {
      JsonObject m = movie.getAsJsonObject();

      int id = m.get("id").getAsInt();
      String title = m.get("title").getAsString();
      String synopsis = m.get("synopsis").getAsString();
      String director = m.get("director").getAsString();
      List<String> castList = List.of(StringUtils.substringBetween(m.get("castList").getAsString(), "[", "]").split(","));
      int runtime = m.get("runtime").getAsInt();
      boolean isBlockbuster = m.get("isBlockbuster").getAsBoolean();

      String releaseDate = m.get("releaseDate").getAsString();
      LocalDate dateRelease = LocalDate.parse(releaseDate, dateFormatter);

      String status = m.get("showStatus").getAsString();
      boolean isValidStatus = EnumUtils.isValidEnum(ShowStatus.class, status);
      if (!isValidStatus) continue;
      ShowStatus showStatus = ShowStatus.valueOf(status);

      String rating = m.get("contentRating").getAsString();
      boolean isValidRating = EnumUtils.isValidEnum(ContentRating.class, rating);
      if (!isValidRating) continue;
      ContentRating contentRating = ContentRating.valueOf(rating);

      double overallRating = m.get("overallRating").getAsDouble();

      // Initialise and append to list
      movies.add(new Movie(
          id,
          title,
          synopsis,
          director,
          castList,
          runtime,
          dateRelease,
          isBlockbuster,
          showStatus,
          contentRating,
          overallRating
      ));
    }

    Helper.logger("MovieDatasource.getMovies", "Total movies: " + movies.size());
    return movies;
  }

  /**
   * Fetch list of movies (and it's reviews) in the theaters
   *
   * @return movies:List<Movie>
   * <a href="https://api.themoviedb.org/3/movie/now_playing?api_key=&language=en-US">TMDB Endpoint</a>
   */
  public List<Movie> fetchMovies() throws ParseException {
    List<Movie> movies = new ArrayList<Movie>();
    List<Review> reviews = new ArrayList<Review>();

    // API Request to get the list of movies in theaters
    String queryMovieList = "/movie/now_playing?language=en-US";
    JsonArray movieList = this.requestPagination(queryMovieList, 1, 2);
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
      String queryMovieDetails = "/movie/" + id + "?append_to_response=credits,reviews,release_dates";
      JsonObject jsonDetails = this.request(queryMovieDetails).getAsJsonObject();
      if (jsonDetails == null) continue;

      /// Raw values
      int runtime = jsonDetails.get("runtime").getAsInt();
      String status = jsonDetails.get("status").getAsString();
      JsonArray castArray = jsonDetails.get("credits").getAsJsonObject().get("cast").getAsJsonArray();
      JsonArray crewArray = jsonDetails.get("credits").getAsJsonObject().get("crew").getAsJsonArray();
      JsonArray reviewArray = jsonDetails.get("reviews").getAsJsonObject().get("results").getAsJsonArray();
      JsonArray releaseArray = jsonDetails.get("release_dates").getAsJsonObject().get("results").getAsJsonArray();

      /// Derived
      //// Release Date
      LocalDate dateRelease = LocalDate.parse(releaseDate, formatter);

      //// Blockbuster Status
      boolean isBlockbuster = voteAverage >= 7.5;

      //// Overall Rating
      double overallRating = (voteAverage / 2);

      //// ShowingStatus
      //// @see https://stackoverflow.com/a/8995988
      ShowStatus showStatus = ShowStatus.PREVIEW;
      HashMap<String, ShowStatus> statusMap = new HashMap<String, ShowStatus>() {{
        put("Planned", ShowStatus.COMING_SOON);
        put("Released", ShowStatus.NOW_SHOWING);
      }};
      for (String key : statusMap.keySet()) {
        if (status.equals(key)) showStatus = statusMap.get(key);
      }

      //// Content Rating
      ContentRating contentRating = ContentRating.PG13;
      for (JsonElement release : releaseArray) {
        JsonObject r = release.getAsJsonObject();
        String iso = r.get("iso_3166_1").getAsString();
        JsonArray releases = r.get("release_dates").getAsJsonArray();
        if (iso.equals("SG") && releases.size() > 0) {
          JsonObject certObject = releases.get(0).getAsJsonObject();
          if (certObject.isJsonNull()) {
            boolean isAdult = m.get("adult").getAsBoolean();
            if (isAdult) {
              contentRating = ContentRating.R21;
            }
            break;
          }
          String certification = certObject.get("certification").getAsString();
          boolean isValidCertification = EnumUtils.isValidEnum(ContentRating.class, certification);
          if (!isValidCertification) continue;
          contentRating = ContentRating.valueOf(certification);
          break;
        }
      }

      //// Cast List
      List<String> castList = new ArrayList<String>();
      if (castArray.size() > 0) {
        for (JsonElement cast : castArray) {
          JsonObject c = cast.getAsJsonObject();
          String name = c.get("name").getAsString();
          String job = c.get("known_for_department").getAsString();

          if (job.equals("Acting")) castList.add(name);
        }
      }

      //// Director
      String director = null;
      if (crewArray.size() > 0) {
        for (JsonElement crew : crewArray) {
          JsonObject c = crew.getAsJsonObject();
          String name = c.get("name").getAsString();
          String job = c.get("job").getAsString();

          if (job.equals("Director")) {
            director = name;
            break;
          }
        }
      }

      /// Reviews
      if (reviews != null && reviewArray.size() > 0) {
        for (JsonElement review : reviewArray) {
          JsonObject r = review.getAsJsonObject();

          // Raw values
          String rId = r.get("id").getAsString();
          String reviewContent = r.get("content").getAsString();

          String authorName = r.get("author").getAsString();
          String username = r.get("author_details").getAsJsonObject().get("username").getAsString();
          if (authorName.isEmpty()) authorName = username;

          String authorId = UUID.randomUUID().toString();

          JsonElement ratingObject = r.get("author_details").getAsJsonObject().get("rating");
          if (ratingObject.isJsonNull()) continue;
          int rating = ratingObject.getAsInt();

          // Initialise and append to list
          reviews.add(new Review(
              rId,
              id,
              reviewContent,
              rating,
              authorName,
              authorId
          ));
        }
      }

      // Initialise and append to list
      movies.add(new Movie(
          id,
          title,
          synopsis,
          director,
          castList,
          runtime,
          dateRelease,
          isBlockbuster,
          showStatus,
          contentRating,
          overallRating
      ));
    }

    Helper.logger("MovieDatasource.fetchMovies", "Total movies: " + movies.size());

    // Serialize data to CSV
    serializeData(movies, "movies.csv");
    serializeData(reviews, "reviews.csv");

    return movies;
  }
}
