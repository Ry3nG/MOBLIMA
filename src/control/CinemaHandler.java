package control;

import boundary.MovieMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entity.Cinema;
import entity.Cinema.ClassType;
import entity.Showtime;
import tmdb.control.Datasource;
import tmdb.entities.Movie;
import utils.Helper;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CinemaHandler extends ShowtimeHandler {
  protected List<Cinema> cinemas;
  protected int selectedCinemaIdx = -1;


  public CinemaHandler() {
    cinemas = this.getCinemas();
    showtimes = this.getShowtimes();
  }

  //+ getCinema(cinemeaId | cinemaId : int) : Cinema
  public Cinema getCinema(int cinemaId) {
    return (this.cinemas.size() < 1 || cinemaId < 0) ? null : this.cinemas.get(cinemaId);
  }

  //+ generateCinemas() : List<Cinema>
  public List<Cinema> generateCinemas() {
    List<Cinema> cinemas = new ArrayList<Cinema>();
    // Generate at least 3 cinemas
    int MIN_CINEMAS = 3;
    SecureRandom random = new SecureRandom();
    if (cinemas.size() < MIN_CINEMAS) {
      for (int c = 0; c <= MIN_CINEMAS; c++) {
        ClassType classType = ClassType.values()[random.nextInt(ClassType.values().length)];
        List<Showtime> showtimes = new ArrayList<Showtime>();

        // Appending new Cinema to this.cinema
        this.addCinema(classType, showtimes);
      }
      cinemas = this.cinemas;
    }
    return cinemas;
  }

  //+ generateShowtimes() : List<Showtime>
  public List<Showtime> generateShowtimes() {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if(this.cinemas.size() < 1) return showtimes;

    SecureRandom random = new SecureRandom();
    List<Movie> movies = (MovieMenu.getHandler()).getMovies();
    for (int i = 0; i < movies.size(); i++) {
      // Generate at least 4 showtimes
      int MIN_SHOWTIMES = this.cinemas.size();
      for (int s = 0; s < MIN_SHOWTIMES; s++) {
        String cineplexId = "XYZ";
        int cinemaId = random.nextInt(0, this.cinemas.size() - 1);
        int movieId = movies.get(i).getId();
        LocalDateTime showDatetime = (LocalDateTime.now()).plusDays(s).plusHours(s + 1).plusMinutes((s/6) * 60);
        this.addShowtime(cineplexId, cinemaId, movieId, showDatetime);
      }
    }
    return this.showtimes;
  }

  //+getCinemas() : List <Cinema>
  public List<Cinema> getCinemas() {
    List<Cinema> cinemas = new ArrayList<Cinema>();

    //TODO: Source from serialized datasource
    String fileName = "cinemas.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("CinemaHandler.getCinemas", "Null and void filename provided, no data retrieved.");
      return cinemas;
    }

    JsonArray cinemaList = Datasource.readArrayFromCsv(fileName);
    if (cinemaList == null) {
      Helper.logger("CinemaHandler.getCinemas", "No serialized data available, generating data instead");
      this.generateCinemas();
      return this.cinemas;
    }

    for (JsonElement cinema : cinemaList) {
      JsonObject c = cinema.getAsJsonObject();

      int id = c.get("id").getAsInt();

      /// ClassType
      String classTypeStr = c.get("classType").getAsString();
      ClassType classType = Datasource.getGson().fromJson(classTypeStr, ClassType.class);

      /// Showtimes
      List<Showtime> showtimes = new ArrayList<Showtime>();

      cinemas.add(new Cinema(
          id,
          classType,
          showtimes
      ));
    }

    this.cinemas = cinemas;
    return cinemas;
  }


  //  +addCinema(showtimes : List<Showtime>) : int
  public int addCinema(ClassType classType, List<Showtime> showtimes) {
    List<Cinema> cinemas = new ArrayList<Cinema>();
    if(this.cinemas != null) cinemas = this.cinemas;

    cinemas.add(new Cinema(
        cinemas.size(),
        classType,
        showtimes
    ));

    this.cinemas = cinemas;
    this.saveCinemas();
    return this.cinemas.size() - 1;
  }

  //+updateCinema(id : int, showtimes : List<Showtime>) : boolean
  public boolean updateCinema(ClassType classType, List<Showtime> showtimes) {
    boolean status = false;
    if (this.cinemas.size() < 1 || this.selectedCinemaIdx < 0) return status;

    Cinema cinema = this.cinemas.get(this.selectedCinemaIdx);

    this.cinemas.set(this.selectedCinemaIdx, new Cinema(
        selectedCinemaIdx,
        classType,
        showtimes
    ));
    status = true;

    return status;
  }

  //+removeCinema(id : int) : boolean
  public boolean removeCinema(int cinemaIdx) {
    boolean status = false;
    if (this.cinemas.size() < 1 || cinemaIdx < 0) return status;

    this.cinemas.remove(cinemaIdx);
    status = true;
    return status;
  }

  //+addShowtimes(showtimes : List<Showtime>) : boolean
  public boolean addShowtimes(int cinemaId, List<Showtime> showtimes) {
    boolean status = false;

    Cinema cinema = this.getCinema(cinemaId);
    if (cinema == null) return status;

    cinema.setShowtimes(showtimes);
    status = true;

    // Replace all showtimes of cinemaID
    List<Showtime> cinemaShowtimes = this.getCinemaShowtimes(cinemaId);
    this.showtimes.removeAll(cinemaShowtimes);
    this.showtimes.addAll(showtimes);

    // Serialize data
    this.saveShowtimes();
    this.saveCinemas();

    return status;
  }

  //+ getShowtimes() : List<Showtime>
  public List<Showtime> getShowtimes() {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    List<Cinema> cinemas = this.getCinemas();
    if (cinemas == null || cinemas.size() < 0) {
      System.out.println("No cinemas available to host showtimes");
      return showtimes;
    }

    //TODO: Source from serialized datasource
    String fileName = "showtimes.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("CinemaHandler.getShowtimes", "Null and void filename provided, no data retrieved.");
      return showtimes;
    }
    JsonArray showtimeList = Datasource.readArrayFromCsv(fileName);

    if (showtimeList == null) {
      Helper.logger("CinemaHandler.getShowtimes", "No serialized data available, generating data instead");
      this.generateShowtimes();
      return this.showtimes;
    }

    for (JsonElement showtime : showtimeList) {
      JsonObject s = showtime.getAsJsonObject();

      String id = s.get("id").getAsString();
      String cineplexId = s.get("cineplexId").getAsString();
      int cinemaId = s.get("cinemaId").getAsInt();
      int movieId = s.get("movieId").getAsInt();
      String datetimeStr = s.get("datetime").getAsString();
      LocalDateTime dateTime = LocalDateTime.parse(datetimeStr);

      /// Seats
      String seatsArr = s.get("seats").getAsString();
      Type seatsType = new TypeToken<boolean[][]>() {
      }.getType();
      boolean[][] seats = Datasource.getGson().fromJson(seatsArr, seatsType);

      Showtime cinemaShowtime = new Showtime(
          id,
          cineplexId,
          cinemaId,
          movieId,
          dateTime,
          seats
      );
      showtimes.add(cinemaShowtime);

      // Append showtime to cinema
      Cinema cinema = cinemas.get(cinemaId);
      List<Showtime> cinemaShowtimes = cinema.getShowtimes();
      cinemaShowtimes.add(cinemaShowtime);

      cinemas.set(cinemaId, cinema);
    }

    this.showtimes = showtimes;
    this.cinemas = cinemas;
    return showtimes;
  }

  //+addShowtime(showtime Showtime) : int showtimeIdx
  public int addShowtime(String cineplexId, int cinemaId, int movieId, LocalDateTime datetime) {
    List<Showtime> showtimes = this.getCinemaShowtimes(cinemaId);
    if (showtimes.size() < 0) {
      System.out.println("No cinemas available to host showtimes");
      return -1;
    }

    Showtime showtime = new Showtime(
        UUID.randomUUID().toString(),
        cineplexId,
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

  public boolean saveCinemas() {
    return Datasource.serializeData(this.cinemas, "cinemas.csv");
  }


//+ unassignSeat(showtimeldx : int, seatCode int[2]):boolean
}
