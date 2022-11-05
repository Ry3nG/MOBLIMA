package control.handlers;

import entities.Showtime;
import utils.Helper;
import utils.Helper.Preset;
import utils.datasource.Datasource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static utils.Helper.colorizer;

public class ShowtimeHandler {
  protected List<Showtime> showtimes = new ArrayList<Showtime>();
  protected int selectedShowtimeIdx = -1;

  /**
   * Set selected showtime idx
   *
   * @param showtimeIdx:int
   */
  // + setSelectedShowtimeIdx(showtimeIdx:int) :void
  public void setSelectedShowtimeIdx(int showtimeIdx) {
    this.selectedShowtimeIdx = showtimeIdx;
  }

  /**
   * Get idx of the specified showtime id
   *
   * @param showtimeId:String
   * @return showtimeIdx:int
   */
  //+ getShowtimeIdx (showtimeId:String) : int
  public int getShowtimeIdx(String showtimeId) {
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


  /**
   * Get showtime of specified idx
   *
   * @param showtimeIdx:int
   * @return showtime:Showtime
   */
  //+ getShowtime (showtimeldx : int) : Showtime
  public Showtime getShowtime(int showtimeIdx) {
    return (this.showtimes.size() < 1 || showtimeIdx < 0) ? null : new Showtime(this.showtimes.get(showtimeIdx));
  }

  /**
   * Get showtime of specified id
   *
   * @param showtimeId:String
   * @return showtime:Showtime
   */
  //+ getShowtime (showtimeId: String) : Showtime
  public Showtime getShowtime(String showtimeId) {
    Showtime showtime = null;
    if (this.showtimes.size() < 1) return showtime;

    for (Showtime s : showtimes) {
      if (s.getId().equals(showtimeId)) {
        showtime = new Showtime(s);
        break;
      }
    }
    return showtime;
  }

  /**
   * Get showtime list of specified movie
   *
   * @param movieId:int
   * @return showtimes:List<Showtime>
   */
  //+ getShowtimes(movield : int) : List<Showtime>
  public List<Showtime> getShowtimes(int movieId) {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.showtimes.size() < 1 || movieId < 0) {
      System.out.println(colorizer("No cinemas available to host showtimes", Preset.ERROR));
      return showtimes;
    }

    for (Showtime showtime : this.showtimes) {
      if (showtime.getMovieId() == movieId) showtimes.add(showtime);
    }
    return showtimes;
  }

  /**
   * Updates showtime (by selectedShowtimeIdx)
   *
   * @param cinemaId:int
   * @param movieId:int
   * @param datetime:LocalDateTime
   * @param seats:boolean[][]
   * @return status:boolean
   */
  //+ updateShowtime(cinemaId:int, movieId:int, datetime:LocalDateTime, seats:boolean[][]):boolean
  public boolean updateShowtime(int cinemaId, int movieId, Showtime.ShowType showType, LocalDateTime datetime, boolean[][] seats) {
    boolean status = false;
    if (this.showtimes.size() < 1 || this.selectedShowtimeIdx < 0) return status;

    Showtime showtime = this.showtimes.get(this.selectedShowtimeIdx);
    if (showtime == null) return status;

    showtime.setCinemaId(cinemaId);
    showtime.setMovieId(movieId);
    showtime.setType(showType);
    showtime.setDatetime(datetime);
    showtime.setSeats(seats);
    this.showtimes.set(this.selectedShowtimeIdx, showtime);
    Helper.logger("ShowtimeHandler.updateShowtime", "AVAIL SEATS: " + getAvailableSeatCount(this.selectedShowtimeIdx));

    status = true;

    // Serialize data
    this.saveShowtimes();

    return status;
  }

  /**
   * Remove specified showtime by idx
   *
   * @param showtimeId:String
   * @return status:boolean
   */
  //+ removeShowtime(showtimeId : String) : boolean
  public boolean removeShowtime(String showtimeId) {
    boolean status = false;
    if (this.showtimes.size() < 1 || showtimeId.isEmpty()) return status;

    int showtimeIdx = this.getShowtimeIdx(showtimeId);
    if (showtimeIdx < 0) return status;
    this.showtimes.remove(showtimeIdx);

    //Serialize data
    this.saveShowtimes();

    status = true;
    return status;
  }

  /**
   * Prints showtime details
   *
   * @param showtimeIdx:int
   */
  //+ printShowtimeDetails(showtimeIdx : int) : void
  public void printShowtimeDetails(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return;
    this.selectedShowtimeIdx = showtimeIdx;

    System.out.println(colorizer("/// SHOWTIME DETAILS ///", Preset.HIGHLIGHT));
    System.out.println(colorizer(showtime.toString(), Preset.HIGHLIGHT));
  }

  /**
   * Prints showtime seats
   *
   * @param showtimeIdx:int
   */
  // +printSeats(showtimeId:int):void
  public void printSeats(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtimeIdx < 0 || showtime == null) return;
    boolean[][] seats = showtime.getSeats();
    this.printSeats(seats);
  }

  /**
   * Print seats (independent of showtime)
   *
   * @param seats:boolean[][]
   */
  // +printSeats(seats:boolean[][]):void
  public void printSeats(boolean[][] seats) {
    int GAP_COL = 2;
    String BUFFER = "=================";

    System.out.println(BUFFER + " SCREEN " + BUFFER);
    System.out.println();
    System.out.print("      ");
    for (int header = 0; header < seats[0].length; header++) {
      if (header > 0) System.out.print((header == GAP_COL || header == seats[0].length - GAP_COL) ? "  -  " : "  ");
      System.out.print((header + 1));
    }
    System.out.println();
    for (int row = 0; row < seats.length; row++) {
      String strRowIdx = (row + 1) + "  - ";
      System.out.print(strRowIdx);
      for (int col = 0; col < seats[row].length; col++) {
        if (col > 0 && (col == GAP_COL || col == seats[row].length - GAP_COL)) {
          System.out.print(" - ");
        }
        boolean isAvailable = (seats[row][col]);
        String seat = isAvailable ? "|O|" : "|X|";
        System.out.print(colorizer(seat, (isAvailable) ? Preset.HIGHLIGHT : Preset.DEFAULT));
      }
      System.out.print(new StringBuilder(strRowIdx).reverse());
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Assigns a list of seat codes to showtime
   *
   * @param showtimeIdx:int
   * @param seatCodes:List<int[]>
   * @param availabilityAssignment:boolean
   * @return status:boolean
   */
  //+bulkAssignSeat(showtimeIdx:int, seatCode:List<int[]>):boolean
  public boolean bulkAssignSeat(int showtimeIdx, List<int[]> seatCodes, boolean availabilityAssignment) {
    boolean status = false;
    for (int[] seatCode : seatCodes) {
      status = assignSeat(showtimeIdx, seatCode, availabilityAssignment);
      if (!status) break;
    }
    return status;
  }

  /**
   * Assign availability to showtime seats
   *
   * @param showtimeIdx:int
   * @param seatCode:int[]
   * @param availabilityAssignment:boolean
   * @return status:boolean
   */
  //+assignSeat (showtimeldx : int, seatCode : int[2]) : boolean
  public boolean assignSeat(int showtimeIdx, int[] seatCode, boolean availabilityAssignment) {
    boolean status = false;
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtimeIdx < 0 || showtime == null || seatCode.length != 2) return status;

    boolean[][] seats = showtime.getSeats();
    seats = assignSeat(seats, seatCode, availabilityAssignment);

    status = true;
    showtime.setSeats(seats);
    this.selectedShowtimeIdx = showtimeIdx;

    this.updateShowtime(showtime.getCinemaId(), showtime.getMovieId(), showtime.getType(), showtime.getDatetime(), seats);

    Helper.logger("ShowtimeHandler.assignSeat", "Showtime-SEATS: " + this.getAvailableSeatCount(showtimeIdx));
    Helper.logger("ShowtimeHandler.assignSeat", "SEATS: " + this.getAvailableSeatCount(showtimeIdx));

    return status;
  }

  /**
   * Assign availability to seats (independent of showtime)
   *
   * @param seats:boolean[][]
   * @param seatCode:int[]
   * @param availabilityAssignment:boolean
   * @return seats:boolean[][]
   */
  //+ assignSeat(seats:boolean[][], seatCode:int[], availabilityAssignment:boolean):boolean[][]
  public boolean[][] assignSeat(boolean[][] seats, int[] seatCode, boolean availabilityAssignment) {
    seats[seatCode[0]][seatCode[1]] = !availabilityAssignment;
    return seats;
  }

  /**
   * Get number of available showtime seats
   *
   * @param showtimeIdx:int
   * @return seatCount:int
   */
  //+ getAvailableSeatCount(showtimeIdx:int) : int
  public int getAvailableSeatCount(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return -1;

    return showtime.getSeatCount(true);
  }

  /**
   * Serialize showtime data to CSV
   */
  //# saveShowtimes():boolean
  protected boolean saveShowtimes() {
    return Datasource.serializeData(this.showtimes, "showtimes.csv");
  }
}
