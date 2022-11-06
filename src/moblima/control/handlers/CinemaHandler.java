package moblima.control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import moblima.entities.Cinema;
import moblima.entities.Cinema.ClassType;
import moblima.entities.Movie;
import moblima.entities.Showtime;
import moblima.entities.Showtime.ShowType;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;
import moblima.utils.datasource.Datasource;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static moblima.utils.Helper.colorizer;
import static moblima.utils.Helper.formatAsTable;
import static moblima.utils.deserializers.LocalDateTimeDeserializer.dateTimeFormatter;

/**
 * The type Cinema handler.
 */
public class CinemaHandler extends ShowtimeHandler {
  /**
   * The Cinemas.
   */
  protected List<Cinema> cinemas;
  /**
   * The Cineplex codes.
   */
  protected List<String> cineplexCodes;
  /**
   * The Selected cinema idx.
   */
  protected int selectedCinemaIdx = -1;

  /**
   * Instantiates a new Cinema handler.
   */
  public CinemaHandler() {
    super();
    this.cineplexCodes = this.getCineplexCodes();
    this.cinemas = this.getCinemas();
    this.showtimes = this.getShowtimes();
  }

  /**
   * Sets selected cinema id.
   *
   * @param cinemaId the cinema id
   */
// + setSelectedCinemaId(cinemaId:int) :void
  public void setSelectedCinemaId(int cinemaId) {
    this.selectedCinemaIdx = cinemaId;
  }

  /**
   * Gets cinema.
   *
   * @param cinemaId the cinema id
   * @return the cinema
   */
//+ getCinema(cinemaId : int) : Cinema
  public Cinema getCinema(int cinemaId) {
    this.selectedCinemaIdx = cinemaId < 0 ? -1 : cinemaId;
    Helper.logger("CinemaHandler.getCinema", "Cinema: " + this.cinemas.get(cinemaId));
    Helper.logger("CinemaHandler.getCinema", "Cloned Cinema: " + new Cinema(this.cinemas.get(cinemaId)));
    return (this.cinemas.size() < 1 || cinemaId < 0) ? null : new Cinema(this.cinemas.get(cinemaId));
  }

  /**
   * Generate cinemas list.
   *
   * @param min the min
   * @return the list
   */
//+ generateCinemas(min:int) : List<Cinema>
  public List<Cinema> generateCinemas(int min) {
    List<Cinema> cinemas = new ArrayList<Cinema>();
    if (min < 1) return cinemas;

    SecureRandom random = new SecureRandom();
    int totalCinemas = min * min;
    while(cinemas.size() < totalCinemas){

      if(cinemas.size() % min == 0){
        // Generate min cinemas for each Cineplex code
        String cineplexCode = RandomStringUtils.random(3, true, false).toUpperCase();
        this.addCineplexCode(cineplexCode);
      }

      ClassType classType = ClassType.values()[random.nextInt(ClassType.values().length)];
      List<Showtime> showtimes = new ArrayList<Showtime>();

      List<String> cineplexCodes = this.getCineplexCodes();
      int cineplexCodeIdx = cineplexCodes.size() - 1;

      // Appending new Cinema to this.cinema
      this.addCinema(classType, showtimes, cineplexCodes.get(cineplexCodeIdx));
      cinemas = this.cinemas;

    }

    return cinemas;
  }

  /**
   * Generate showtimes list.
   *
   * @param min the min
   * @return the list
   */
//+ generateShowtimes(min:int) : List<Showtime>
  public List<Showtime> generateShowtimes(int min) {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.cinemas.size() < 1 || min < 1) return showtimes;

    SecureRandom random = new SecureRandom();
    MovieHandler movieHandler = new MovieHandler();
    List<Movie> movies = movieHandler.getMovies();
    Helper.logger("CinemaHandler.generateShowtimes", "Movies: \n" + movies);
    for (Movie movie : movies) {
      for (int s = 0; s < min; s++) {
        int cinemaId = random.nextInt(0, this.cinemas.size() - 1);
        int movieId = movie.getId();
        LocalDateTime showDatetime = (LocalDateTime.now()).plusDays(s).plusHours(s + 1).plusMinutes((s / 6) * 60L);
        ShowType[] showTypes = ShowType.values();
        ShowType showType = showTypes[random.nextInt(0, showTypes.length)];
        int showtimeIdx = this.addShowtime(cinemaId, movieId, showDatetime, showType);
        Helper.logger("CinemaHandler.generateShowtimes", "Generated: \n" + this.getShowtime(showtimeIdx));

      }
    }

    return this.showtimes;
  }

  /**
   * Gets cinemas.
   *
   * @return the cinemas
   */
//+getCinemas() : List<Cinema>
  public List<Cinema> getCinemas() {
    List<Cinema> cinemas = new ArrayList<Cinema>();

    Helper.logger("CinemaHandler.getCinemas", "Cinemas: \n" + cinemas);
    Helper.logger("CinemaHandler.getCinemas", "Cinemas: \n" + this.cinemas);

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
      cinemas = this.generateCinemas(3);
      this.cinemas = cinemas;
      return cinemas;
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
      cinemas.add(new Cinema(id, classType, showtimes, cineplexCode));
      this.addCineplexCode(cineplexCode);
    }

    this.cinemas = cinemas;
    if (this.showtimes != null && this.showtimes.size() > 0) {
      this.showtimes = this.getShowtimes();
    }

    return cinemas;
  }


  /**
   * Update cinema boolean.
   *
   * @param classType    the class type
   * @param showtimes    the showtimes
   * @param cineplexCode the cineplex code
   * @return the boolean
   */
//+updateCinema( classType : ClassType, showtimes : List<Showtime>):boolean
  public boolean updateCinema(ClassType classType, List<Showtime> showtimes, String cineplexCode) {
    boolean status = false;
    if (this.cinemas.size() < 1 || this.selectedCinemaIdx < 0) return status;

    Cinema cinema = this.getCinema(this.selectedCinemaIdx);
    if (cinema == null) return status;

    this.cinemas.set(this.selectedCinemaIdx, new Cinema(selectedCinemaIdx, classType, showtimes, cineplexCode));

    status = true;

    //Serialize data
    this.saveCinemas();

    return status;
  }

  /**
   * Add cinema int.
   *
   * @param classType    the class type
   * @param showtimes    the showtimes
   * @param cineplexCode the cineplex code
   * @return the int
   */
//  +addCinema(classType:ClassType, showtimes : List<Showtime>) : int
  public int addCinema(ClassType classType, List<Showtime> showtimes, String cineplexCode) {
    List<Cinema> cinemas = new ArrayList<Cinema>();
    if (this.cinemas != null) cinemas = this.cinemas;

    // Append to existing list of Cineplex code only if it didn't already exist
    this.addCineplexCode(cineplexCode);

    // Append to existing list of Cinemas
    cinemas.add(new Cinema(cinemas.size(), classType, showtimes, cineplexCode));

    this.cinemas = cinemas;
    this.saveCinemas();
    return this.cinemas.size() - 1;
  }

  /**
   * Remove cinema boolean.
   *
   * @param cinemaIdx the cinema idx
   * @return the boolean
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
   * Add showtimes boolean.
   *
   * @param cinemaId  the cinema id
   * @param showtimes the showtimes
   * @return the boolean
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
    Helper.logger("CinemaHandler.cinema", this.getCinema(cinemaId).toString());

    status = true;

    // Serialize data
    this.saveShowtimes();
    this.saveCinemas();

    return status;
  }

  /**
   * Gets showtimes.
   *
   * @return the showtimes
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

      /// ShowType
      String showType = s.get("type").getAsString();
      boolean isValidStatus = EnumUtils.isValidEnum(ShowType.class, showType);
      if (!isValidStatus) continue;
//      ShowType type = Arrays.stream(ShowType.values()).find
      ShowType type = ShowType.valueOf(showType);

      /// Seats
      String seatsArr = s.get("seats").getAsString();
      Type seatsType = new TypeToken<boolean[][]>() {
      }.getType();
      boolean[][] seats = Datasource.getGson().fromJson(seatsArr, seatsType);

      Showtime cinemaShowtime = new Showtime(id, cinemaId, movieId, dateTime, type, seats);
      showtimes.add(cinemaShowtime);
    }

    this.showtimes = showtimes;

    // Serialize data
    this.saveShowtimes();

    // Append showtimes to existing cinemas
    for (Cinema cinema : this.cinemas) {
      List<Showtime> cinemaShowtimes = this.getCinemaShowtimes(cinema.getId());
      this.addShowtimes(cinema.getId(), cinemaShowtimes);
      Helper.logger("CinemaHandler.getShowtimes", "Cinema: " + this.getCinema(cinema.getId()));
    }

    return showtimes;
  }

  /**
   * Printed showtime string.
   *
   * @param showtimeId the showtime id
   * @return the string
   */
  public String printedShowtime(String showtimeId) {

    Showtime showtime = this.getShowtime(showtimeId);

    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Datetime:", showtime.getDay() + ", " + showtime.getFormattedDatetime()));
//    rows.add(Arrays.asList("Movie ID:", Integer.toString(showtime.getMovieId())));
    rows.add(Arrays.asList("Cineplex Code:", this.getShowtimeCinema(showtime.getId()).getCineplexCode()));
    rows.add(Arrays.asList("Cinema ID:", Integer.toString(showtime.getCinemaId())));
    rows.add(Arrays.asList("Show Type:", showtime.getType().toString()));
    rows.add(Arrays.asList("Booked Seats:", showtime.getSeatCount(false) + "/" + showtime.getSeatCount()));

    return formatAsTable(rows);
  }

  /**
   * Gets showtime cinema.
   *
   * @param showtimeId the showtime id
   * @return the showtime cinema
   */
//+ getShowtimeCinema(showtimeId : String) : Cinema
  public Cinema getShowtimeCinema(String showtimeId) {
    Cinema cinema = null;

    Showtime showtime = this.getShowtime(showtimeId);
    if (showtime == null) return cinema;

    return this.getCinema(showtime.getCinemaId());
  }

  /**
   * Gets cinema showtimes.
   *
   * @param cinemaId the cinema id
   * @return the cinema showtimes
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
   * Add showtime int.
   *
   * @param cinemaId the cinema id
   * @param movieId  the movie id
   * @param datetime the datetime
   * @param type     the type
   * @return the int
   */
//+addShowtime(cineplexId:String, cinemaId:int, movieId:int, datetime:LocalDateTime) : int
  public int addShowtime(int cinemaId, int movieId, LocalDateTime datetime, ShowType type) {
    List<Showtime> showtimes = this.getCinemaShowtimes(cinemaId);
    if (showtimes.size() < 0) {
      System.out.println(colorizer("No cinemas available to host showtimes", Preset.ERROR));
      return -1;
    } else if (this.checkClashingShowtime(cinemaId, datetime)) {
      System.out.println(colorizer("Cinema already has a showing at the given datetime", Preset.ERROR));
      return -1;
    }

    // Initializes new showtime
    Showtime showtime = new Showtime(UUID.randomUUID().toString(), cinemaId, movieId, datetime, type);
    showtimes.add(showtime);

    // Append showtime to existing
    boolean isAdded = this.addShowtimes(cinemaId, showtimes);
    if (!isAdded) return -1;

    // Serialize bookings
    this.saveShowtimes();

    return this.showtimes.size() - 1;
  }

  /**
   * Check clashing showtime boolean.
   *
   * @param cinemaId the cinema id
   * @param datetime the datetime
   * @return the boolean
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
   * Get cineplex codes list.
   *
   * @return the list
   */
  public List<String> getCineplexCodes() {
    List<String> cineplexCodes = new ArrayList<String>();
    if (this.cineplexCodes != null) cineplexCodes = this.cineplexCodes;

    Helper.logger("CinemaHandler.getCineplexCodes", "Cineplexes: " + cineplexCodes);
    return cineplexCodes;
  }

  /**
   * Add cineplex code.
   *
   * @param cineplexCode the cineplex code
   */
  public void addCineplexCode(String cineplexCode) {
    List<String> cineplexCodes = this.getCineplexCodes();
    if (cineplexCodes.size() < 1 || !cineplexCodes.contains(cineplexCode)) cineplexCodes.add(cineplexCode);

    Helper.logger("CinemaHandler.addCineplexCode", "Cineplexes: " + cineplexCodes);
    this.cineplexCodes = cineplexCodes;
  }

  /**
   * Save cinemas boolean.
   *
   * @return the boolean
   */
//# saveCinemas():boolean
  protected boolean saveCinemas() {
    return Datasource.serializeData(this.cinemas, "cinemas.csv");
  }
}
