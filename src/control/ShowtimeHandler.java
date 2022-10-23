package control;

import entity.Showtime;
import tmdb.control.Datasource;
import utils.Helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeHandler {
  protected List<Showtime> showtimes;
  protected int selectedShowtimeIdx = -1;

  public ShowtimeHandler() {
    this.showtimes = new ArrayList<Showtime>();
  }

  // +getSelectedShowtimeIdx():int
  public int getSelectedShowtimeIdx() {
    return selectedShowtimeIdx;
  }

  // + setSelectedShowtimeIdx(showtimeIdx:int) :void
  public void setSelectedShowtimeIdx(int showtimeIdx) {
    this.selectedShowtimeIdx = showtimeIdx;
  }

  //  + getSelected Showtime() : Showtime
  public Showtime getSelectedShowtime() {
    return (this.showtimes.size() < 1 || this.selectedShowtimeIdx < 0) ? null : this.showtimes.get(this.selectedShowtimeIdx);
  }

  //+ getShowtimeIdx (showtimeId:String) : int
  public int getShowtimeIdx(
      String showtimeId
  ) {
    int showtimeIdx = -1;
    if (this.showtimes.size() < 1 || showtimeId.isEmpty()) return showtimeIdx;

    for (int i = 0; i < this.showtimes.size(); i++) {
      Showtime showtime = this.showtimes.get(i);
      if (showtime.getId().equals(showtimeId)) {
        showtimeIdx = i;
        break;
      }
    }

    return showtimeIdx;
  }


  //+ getShowtime (showtimeldx : int) : Showtime
  public Showtime getShowtime(int showtimeIdx) {
    return (this.showtimes.size() < 1 || showtimeIdx < 0) ? null : this.showtimes.get(showtimeIdx);
  }

  // + getShowtime (showtimeId: String) : Showtime
  public Showtime getShowtime(String showtimeId) {
    Showtime showtime = null;
    if (this.showtimes.size() < 1) return showtime;

    for (Showtime s : showtimes) {
      if (s.getId().equals(showtimeId)) {
        showtime = s;
        break;
      }
    }
    return showtime;
  }

  //  + getShowtimes(movield : int) : List <Showtime>
  public List<Showtime> getShowtimes(int movieId) {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.showtimes.size() < 1 || movieId < 0) {
      System.out.println("No cinemas available to host showtimes");
      return showtimes;
    }

    for (Showtime showtime : this.showtimes) {
      if (showtime.getMovieId() == movieId) showtimes.add(showtime);
    }
    return showtimes;
  }


  //+ getShowtimes(cinemaId : int) : List <Showtime>
  public List<Showtime> getCinemaShowtimes(int cinemaId) {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.showtimes.size() < 1 || cinemaId < 0) {
      System.out.println("No cinemas available to host showtimes");
      return showtimes;
    }

    for (Showtime showtime : this.showtimes) {
      if (showtime.getCinemaId() == cinemaId) showtimes.add(showtime);
    }
    return showtimes;
  }

  //1+ updateShowtime(showtime Showtime) : boolean
  public boolean updateShowtime(String cineplexId, int cinemaId, int movieId, LocalDateTime datetime) {
    boolean status = false;
    if (this.showtimes.size() < 1 || this.selectedShowtimeIdx < 0) return status;

    Showtime showtime = this.showtimes.get(this.selectedShowtimeIdx);
    if (showtime == null) return status;

    return this.updateShowtime(cineplexId, cinemaId, movieId, datetime, showtime.getSeats());
  }

  public boolean updateShowtime(String cineplexId, int cinemaId, int movieId, LocalDateTime datetime, boolean[][] seats) {
    boolean status = false;
    if (this.showtimes.size() < 1 || this.selectedShowtimeIdx < 0) return status;

    Showtime showtime = this.showtimes.get(this.selectedShowtimeIdx);
    if (showtime == null) return status;

    showtime.setCineplexId(cineplexId);
    showtime.setCinemaId(cinemaId);
    showtime.setMovieId(movieId);
    showtime.setDatetime(datetime);
    showtime.setSeats(seats);

    this.showtimes.set(this.selectedShowtimeIdx, showtime);
//    printSeats(this.selectedShowtimeIdx);
//    printShowtimeDetails(this.selectedShowtimeIdx);
    Helper.logger("ShowtimeHandler.updateShowtime", "AVAIL SEATS: " + getAvailableSeatCount(this.selectedShowtimeIdx));


    status = true;

    // Serialize data
    this.saveShowtimes();

    return status;
  }

  //+ removeShowtime(showtimeldx : String) : boolean
  public boolean removeShowtime(int showtimeIdx) {
    boolean status = false;
    if (this.showtimes.size() < 1 || showtimeIdx < 0) return status;

    this.showtimes.remove(showtimeIdx);
    status = true;
    return status;
  }

  //+printShowtimeDetails(showtimeIdx : int) : void
  public void printShowtimeDetails(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return;
    this.selectedShowtimeIdx = showtimeIdx;
    System.out.println(showtime.toString());
  }

  //  +printShowtimeDetails(showtimeId : String) : void

  //+ printShowtimes() : void
  public void printShowtimes() {
    if (this.showtimes.isEmpty()) {
      System.out.println("No showtimes available");
      return;
    }

    for (int i = 0; i < this.showtimes.size(); i++) {
      Showtime showtime = this.showtimes.get(i);
      System.out.println("> " + i + " " + showtime.getId());
    }
  }

//+ printShowtimes(movield : String) : void


  // +printSeats(showtimeId:int):void
  public void printSeats(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtimeIdx < 0 || showtime == null) return;
    boolean seats[][] = showtime.getSeats();
    this.printSeats(seats);
  }

  // +printSeats(seats:boolean[][]):void
  public void printSeats(boolean[][] seats) {
    System.out.println();
    System.out.print("  ");
    for (int header = 0; header < seats[0].length; header++) {
      System.out.print(" " + (header + 1) + "  ");
    }
    System.out.println();
    for (int row = 0; row < seats.length; row++) {
      System.out.print((row + 1) + " ");
      for (int col = 0; col < seats[row].length; col++) {
        String seat = (seats[row][col]) ? "[O]" : "[X]";
        System.out.print(seat + " ");
      }
      System.out.println();
    }
    System.out.println();
  }

  //+bulkAssignSeat(showtimeIdx:int, seatCode:List<int[]>):boolean
  public boolean bulkAssignSeat(int showtimeIdx, List<int[]> seatCodes, boolean availabilityAssignment) {
    boolean status = false;
    for (int[] seatCode : seatCodes) {
      status = assignSeat(showtimeIdx, seatCode, availabilityAssignment);
      if (!status) break;
    }
    return status;
  }

  //+assignSeat (showtimeldx : int, seatCode : int[2]) : boolean
  public boolean assignSeat(int showtimeIdx, int[] seatCode, boolean availabilityAssignment) {
    boolean status = false;
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtimeIdx < 0 || showtime == null || seatCode.length != 2) return status;

    boolean seats[][] = showtime.getSeats();
    seats = assignSeat(seats, seatCode, availabilityAssignment);

    status = true;
    showtime.setSeats(seats);
    this.selectedShowtimeIdx = showtimeIdx;

    this.updateShowtime(showtime.getCineplexId(), showtime.getCinemaId(), showtime.getMovieId(), showtime.getDatetime(), seats);
//    this.showtimes.set(showtimeIdx, showtime);

    Helper.logger("ShowtimeHandler.assignSeat", "Showtime-SEATS: " + this.getAvailableSeatCount(showtimeIdx));
    Helper.logger("ShowtimeHandler.assignSeat", "SEATS: " + this.getAvailableSeatCount(showtimeIdx));

    return status;
  }

  public boolean[][] assignSeat(boolean[][] seats, int[] seatCode, boolean availabilityAssignment) {
    seats[seatCode[0]][seatCode[1]] = !availabilityAssignment;
    return seats;
  }

  //  +getAvailableSeatCount(showtimeIdx:int) : int
  public int getAvailableSeatCount(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return -1;

    int seatCount = 0;
    boolean[][] seats = showtime.getSeats();
    for (int row = 0; row < seats.length; row++) {
      for(int col = 0; col < seats[row].length; col++){
        if(seats[row][col]) seatCount++;
      }
    }

    return seatCount;
  }

  public boolean saveShowtimes() {
    return Datasource.serializeData(this.showtimes, "showtimes.csv");
  }

//+ assignSeat (showtimeld : String, seatCode : int[2]) boolean
//+ unassignSeat(showtimeld String, seatCode : int[2]) boolean
}
