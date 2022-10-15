package main.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.entity.Movie;
import main.types.MovieConstants;
import main.utils.Constants;
import main.utils.Datasource;
import main.utils.Helper;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieDatasource extends Datasource {

  // Fetch list of movies in the theaters
  // https://api.themoviedb.org/3/movie/now_playing?api_key=&language=en-US
  public List<Movie> fetchMovies() {
    List<Movie> movies = new ArrayList<Movie>();

    // API Request to get the list of movies in theaters
    String queryMovieList = "/movie/now_playing?language=en-US";
    JsonArray movieList = Datasource.requestPagination(queryMovieList, 1, 2);
    if (movieList == null) return movies;

    // Iterate through API payload to map as Movie object
    for (JsonElement movie : movieList) {
      JsonObject m = movie.getAsJsonObject();

      /// Raw values
      String id = m.get("id").getAsString();
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
          isBlockbuster
      ));
    }
    Helper.logger("MovieDatasource.fetchMovies", "Total movies: " + movies.size());

    // Serialize data to CSV
    String outputFileName = "movies.csv";
    Datasource.serializeDataToCSV(Datasource.convertToJsonArray(movies), outputFileName, Constants.DEBUG_MODE);
    Helper.logger("MovieDatasource.fetchMovies", "Exported to " + outputFileName);

    return movies;
  }
}
