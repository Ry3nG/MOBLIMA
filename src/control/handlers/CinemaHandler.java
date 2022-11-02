package control.handlers;

import boundary.MovieMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entity.Cinema;
import entity.Cinema.ClassType;
import entity.Movie;
import entity.Showtime;
import org.apache.commons.lang3.RandomStringUtils;
import utils.Helper;
import utils.Helper.Preset;
import utils.datasource.Datasource;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static utils.Helper.colorizer;
import static utils.Helper.formatAsTable;
import static utils.LocalDateTimeDeserializer.dateTimeFormatter;

public class CinemaHandler extends ShowtimeHandler {
  protected List<Cinema> cinemas;
  protected int selectedCinemaIdx = -1;

  public CinemaHandler() {
    super();
    cinemas = this.getCinemas();
    showtimes = this.getShowtimes();
  }

  /**
   * Set selected cinema id
   *
   * @param cinemaId:int
   */
  // + setSelectedCinemaId(cinemaId:int) :void
  public void setSelectedCinemaId(int cinemaId) {
    this.selectedCinemaIdx = cinemaId;
  }

  //+ getSelectedCinema()

  /**
   * Retrieves cinema by specified id
   *
   * @param cinemaId:int
   * @return cinema:Cinema | null
   */
  //+ getCinema(cinemaId : int) : Cinema
  public Cinema getCinema(int cinemaId) {
    this.selectedCinemaIdx = cinemaId < 0 ? -1 : cinemaId;
    Helper.logger("CinemaHandler.getCinema", "Cinema: " + this.cinemas.get(cinemaId));
    Helper.logger("CinemaHandler.getCinema", "Cloned Cinema: " + new Cinema(this.cinemas.get(cinemaId)));
    return (this.cinemas.size() < 1 || cinemaId < 0) ? null : new Cinema(this.cinemas.get(cinemaId));
  }

  /**
   * Generate cinema list
   *
   * @param min:int
   * @return cinemas:List<Cinema>
   */
  //+ generateCinemas(min:int) : List<Cinema>
  public List<Cinema> generateCinemas(int min) {
    List<Cinema> cinemas = new ArrayList<Cinema>();
    if (min < 1) return cinemas;

    SecureRandom random = new SecureRandom();
    if (cinemas.size() < min) {
      for (int c = 0; c <= min; c++) {
        ClassType classType = ClassType.values()[random.nextInt(ClassType.values().length)];
        List<Showtime> showtimes = new ArrayList<Showtime>();
        String cineplexCode = RandomStringUtils.random(3, true, false).toUpperCase();

        // Appending new Cinema to this.cinema
        this.addCinema(classType, showtimes, cineplexCode);
      }
      cinemas = this.cinemas;
    }
    return cinemas;
  }

  /**
   * Generate showtime list
   *
   * @param min:int
   * @return showtimes:List<Showtime>
   */
  //+ generateShowtimes(min:int) : List<Showtime>
  public List<Showtime> generateShowtimes(int min) {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.cinemas.size() < 1 || min < 1) return showtimes;

    SecureRandom random = new SecureRandom();
    List<Movie> movies = (MovieMenu.getHandler()).getMovies();
    for (Movie movie : movies) {
      for (int s = 0; s < min; s++) {
        String cineplexId = RandomStringUtils.random(3, true, false).toUpperCase();
        int cinemaId = random.nextInt(0, this.cinemas.size() - 1);
        int movieId = movie.getId();
        LocalDateTime showDatetime = (LocalDateTime.now()).plusDays(s).plusHours(s + 1).plusMinutes((s / 6) * 60L);
        this.addShowtime(cineplexId, cinemaId, movieId, showDatetime);
      }
    }
    return this.showtimes;
  }

  /**
   * Deserializes and returns cinema list
   *
   * @return cinemas:List<Cinema>
   */
  //+getCinemas() : List<Cinema>
  public List<Cinema> getCinemas() {
    List<Cinema> cinemas = new ArrayList<Cinema>();

    //Source from serialized datasource
    String fileName = "cinemas.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("CinemaHandler.getCinemas", "Null and void filename provided, no data retrieved.");
      this.cinemas = cinemas;
      return cinemas;
    }

    JsonArray cinemaList = Datasource.readArrayFromCsv(fileName);
    if (cinemaList == null) {
      Helper.logger("CinemaHandler.getCinemas", "No serialized data available, generating data instead");
      this.generateCinemas(3);
      return this.cinemas;
    }

    for (JsonElement cinema : cinemaList) {
      JsonObject c = cinema.getAsJsonObject();

      int id = c.get("id").getAsInt();
      String cineplexCode = c.get("cineplexCode").getAsString();

      /// ClassType
      String classTypeStr = c.get("classType").getAsString();
      ClassType classType = Datasource.getGson().fromJson(classTypeStr, ClassType.class);

      /// Showtimes (empty by default)
      List<Showtime> showtimes = new ArrayList<Showtime>();
      cinemas.add(new Cinema(
          id,
          classType,
          showtimes,
          cineplexCode
      ));
    }

    this.cinemas = cinemas;
    if (this.showtimes != null && this.showtimes.size() > 0) {
      this.showtimes = this.getShowtimes();
    }

    return cinemas;
  }


  /**
   * Update cinema of currently selected cinema id/idx
   *
   * @param classType:ClassType
   * @param showtimes:List<Showtime>
   * @return status:boolean
   */
  //+updateCinema( classType : ClassType, showtimes : List<Showtime>):boolean
  public boolean updateCinema(ClassType classType, List<Showtime> showtimes, String cineplexCode) {
    boolean status = false;
    if (this.cinemas.size() < 1 || this.selectedCinemaIdx < 0) return status;

    Cinema cinema = this.getCinema(this.selectedCinemaIdx);
    if (cinema == null) return status;

    this.cinemas.set(this.selectedCinemaIdx, new Cinema(
        selectedCinemaIdx,
        classType,
        showtimes,
        cineplexCode
    ));

    status = true;

    //Serialize data
    this.saveCinemas();

    return status;
  }

  /**
   * Append new cinema to cinema list
   *
   * @param classType:ClassType
   * @param showtimes:List<Showtime>
   * @return showtimeIdx:int
   */
  //  +addCinema(classType:ClassType, showtimes : List<Showtime>) : int
  public int addCinema(ClassType classType, List<Showtime> showtimes, String cineplexCode) {
    List<Cinema> cinemas = new ArrayList<Cinema>();
    if (this.cinemas != null) cinemas = this.cinemas;

    cinemas.add(new Cinema(
        cinemas.size(),
        classType,
        showtimes,
        cineplexCode
    ));

    this.cinemas = cinemas;
    this.saveCinemas();
    return this.cinemas.size() - 1;
  }

  /**
   * Delete specified cinema from cinema list by specified id/idx
   *
   * @param cinemaIdx:int
   * @return status:boolean
   */
  //+removeCinema(cinemaIdx : int) : boolean
  public boolean removeCinema(int cinemaIdx) {
    boolean status = false;
    if (this.cinemas.size() < 1 || cinemaIdx < 0) return status;

    // Early return if cinema does not exist
    if (this.getCinema(cinemaIdx) == null) return status;

    // Remove cinema
    this.cinemas.remove(cinemaIdx);
    status = true;
    return status;
  }

  /**
   * Replace showtime list of specified cinema id
   *
   * @param cinemaId:int
   * @param showtimes:List<Showtime>
   * @return status:boolean
   */
  //+addShowtimes(cinemaId:int, showtimes : List<Showtime>) : boolean
  public boolean addShowtimes(int cinemaId, List<Showtime> showtimes) {
    boolean status = false;

    // Replace all showtimes of cinemaID
    List<Showtime> cinemaShowtimes = this.getCinemaShowtimes(cinemaId);
    this.showtimes.removeAll(cinemaShowtimes);

    // Update cinema
    Cinema cinema = this.getCinema(cinemaId);
    if (cinema == null) return status;
    cinema.setShowtimes(showtimes);
    this.cinemas.set(cinemaId, cinema);

    // Update showtimes
    this.showtimes.addAll(showtimes);
    Helper.logger("CinemaHandler.cinema", this.cinemas.toString());

    status = true;

    // Serialize data
    this.saveShowtimes();
    this.saveCinemas();

    return status;
  }

  /**
   * Deserializes and returns showtime list
   *
   * @return showtimes:List<Showtime>
   */
  //+ getShowtimes() : List<Showtime>
  public List<Showtime> getShowtimes() {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.cinemas == null || this.cinemas.size() < 0) {
      System.out.println(colorizer("No cinemas available to host showtimes", Preset.ERROR));
      return showtimes;
    }

    //Source from serialized datasource
    String fileName = "showtimes.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("CinemaHandler.getShowtimes", "Null and void filename provided, no data retrieved.");
      return showtimes;
    }
    JsonArray showtimeList = Datasource.readArrayFromCsv(fileName);

    if (showtimeList == null) {
      Helper.logger("CinemaHandler.getShowtimes", "No serialized data available, generating data instead");
      this.generateShowtimes(this.cinemas.size());
      return this.showtimes;
    }

    for (JsonElement showtime : showtimeList) {
      JsonObject s = showtime.getAsJsonObject();

      String id = s.get("id").getAsString();
      int cinemaId = s.get("cinemaId").getAsInt();
      int movieId = s.get("movieId").getAsInt();
      String datetimeStr = s.get("datetime").getAsString();
      LocalDateTime dateTime = LocalDateTime.parse(datetimeStr, dateTimeFormatter);

      /// Seats
      String seatsArr = s.get("seats").getAsString();
      Type seatsType = new TypeToken<boolean[][]>() {
      }.getType();
      boolean[][] seats = Datasource.getGson().fromJson(seatsArr, seatsType);

      Showtime cinemaShowtime = new Showtime(
          id,
          cinemaId,
          movieId,
          dateTime,
          seats
      );
      showtimes.add(cinemaShowtime);
    }

    this.showtimes = showtimes;

    // Serialize data
    this.saveShowtimes();

    // Append showtimes to existing cinemas
    for (Cinema cinema : this.cinemas) {
      List<Showtime> cinemaShowtimes = this.getCinemaShowtimes(cinema.getId());
      this.addShowtimes(cinema.getId(), cinemaShowtimes);
    }

    Helper.logger("CinemaHandler.getShowtimes", "Cinema: " + this.cinemas);

    return showtimes;
  }

  public String printedShowtime(String showtimeId) {

    Showtime showtime = this.getShowtime(showtimeId);

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Datetime:", showtime.getDay() + ", " + showtime.getFormattedDatetime()));
    rows.add(Arrays.asList("Movie ID:", Integer.toString(showtime.getMovieId())));
    rows.add(Arrays.asList("Cineplex Code:", this.getShowtimeCinema(showtime.getId()).getCineplexCode()));
    rows.add(Arrays.asList("Cinema ID:", Integer.toString(showtime.getCinemaId())));
    rows.add(Arrays.asList("Booked Seats:", showtime.getSeatCount(false) + "/" + showtime.getSeatCount()));

    return formatAsTable(rows);
  }

  //+ getShowtimeCinema(showtimeId : String) : Cinema
  public Cinema getShowtimeCinema(String showtimeId) {
    Cinema cinema = null;

    Showtime showtime = this.getShowtime(showtimeId);
    if (showtime == null) return cinema;

    return this.getCinema(showtime.getCinemaId());
  }

  /**
   * Get showtime list of specified cinema
   *
   * @param cinemaId:int
   * @return showtimes:List<Showtime>
   */
  //+ getCinemaShowtimes(cinemaId : int) : List <Showtime>
  public List<Showtime> getCinemaShowtimes(int cinemaId) {
    List<Showtime> showtimes = new ArrayList<Showtime>();

    if (this.showtimes.size() < 1 || cinemaId < 0) {
//      System.out.println("No cinemas available to host showtimes");
      return showtimes;
    }

    for (Showtime showtime : this.showtimes) {
      if (showtime.getCinemaId() == cinemaId) showtimes.add(showtime);
    }
    return showtimes;
  }

  /**
   * Append new showtime to cinema list
   *
   * @param cineplexId:String
   * @param cinemaId:int
   * @param movieId:int
   * @param datetime:LocalDateTime
   * @return showtimeIdx:int
   */
  //+addShowtime(cineplexId:String, cinemaId:int, movieId:int, datetime:LocalDateTime) : int
  public int addShowtime(String cineplexId, int cinemaId, int movieId, LocalDateTime datetime) {
    List<Showtime> showtimes = this.getCinemaShowtimes(cinemaId);
    if (showtimes.size() < 0) {
      System.out.println(colorizer("No cinemas available to host showtimes", Preset.ERROR));
      return -1;
    } else if (this.checkClashingShowtime(cinemaId, datetime)) {
      System.out.println(colorizer("Cinema already has a showing at the given datetime", Preset.ERROR));
      return -1;
    }

    // Initializes new showtime
    Showtime showtime = new Showtime(
        UUID.randomUUID().toString(),
        cinemaId,
        movieId,
        datetime
    );
    showtimes.add(showtime);

    // Append showtime to existing
    boolean isAdded = this.addShowtimes(cinemaId, showtimes);
    if (!isAdded) return -1;

    // Serialize bookings
    this.saveShowtimes();

    return this.showtimes.size() - 1;
  }

  /**
   * Checks if cinema by specified id has clashing showtime datetime
   *
   * @param cinemaId:int
   * @param datetime:LocalDateTime
   * @return hasClash:boolean
   */
  //+ checkClashingShowtime(cinemaId:int, dateTime:LocalDateTime):boolean
  public boolean checkClashingShowtime(int cinemaId, LocalDateTime datetime) {
    boolean hasClash = false;

    List<Showtime> cinemaShowtimes = this.getCinemaShowtimes(cinemaId);
    if (cinemaShowtimes.size() < 1) return hasClash;

    for (Showtime showtime : cinemaShowtimes) {
      if (showtime.getDatetime().isEqual(datetime)) {
        hasClash = true;
        break;
      }
    }
    return hasClash;
  }

  /**
   * Serialize cinema data to CSV
   */
  //# saveCinemas():boolean
  protected boolean saveCinemas() {
    return Datasource.serializeData(this.cinemas, "cinemas.csv");
  }
}
