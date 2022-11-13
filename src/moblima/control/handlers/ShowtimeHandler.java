package moblima.control.handlers;

import moblima.entities.Showtime;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;
import moblima.utils.datasource.Datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static moblima.utils.Helper.*;

/**
 * The type Showtime handler.
 */
public class ShowtimeHandler {
  /**
   * The Showtimes.
   */
  protected List<Showtime> showtimes = new ArrayList<Showtime>();
  /**
   * The Selected showtime idx.
   */
  protected int selectedShowtimeIdx = -1;

  /**
   * Sets selected showtime idx.
   *
   * @param showtimeIdx the showtime idx
   */
// + setSelectedShowtimeIdx(showtimeIdx:int) :void
  public void setSelectedShowtimeIdx(int showtimeIdx) {
    this.selectedShowtimeIdx = showtimeIdx;
  }

  /**
   * Gets showtime idx.
   *
   * @param showtimeId the showtime id
   * @return the showtime idx
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
   * Gets showtime.
   *
   * @param showtimeIdx the showtime idx
   * @return the showtime
   */
//+ getShowtime (showtimeldx : int) : Showtime
  public Showtime getShowtime(int showtimeIdx) {
    return (this.showtimes.size() < 1 || showtimeIdx < 0) ? null : new Showtime(this.showtimes.get(showtimeIdx));
  }

  /**
   * Gets showtime.
   *
   * @param showtimeId the showtime id
   * @return the showtime
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
   * Gets showtimes.
   *
   * @param movieId the movie id
   * @return the showtimes
   */
//+ getShowtimes(movield : int) : List<Showtime>
  public List<Showtime> getShowtimes(int movieId) {
    List<Showtime> showtimes = new ArrayList<Showtime>();
    if (this.showtimes.size() < 1 || movieId < 0) {
      colorPrint("No cinemas available to host showtimes", Preset.ERROR);
      return showtimes;
    }

    for (Showtime showtime : this.showtimes) {
      if (showtime.getMovieId() == movieId) showtimes.add(showtime);
    }
    return showtimes;
  }

  /**
   * Update showtime seats boolean.
   *
   * @param showtimeIdx the showtime idx
   * @param seats       the seats
   * @return the boolean
   */
  public boolean updateShowtimeSeats(int showtimeIdx, boolean[][] seats) {
    boolean status = false;
    if (this.showtimes.size() < 1 || showtimeIdx < 0) return status;

    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return status;

    showtime.setSeats(seats);
    this.showtimes.set(showtimeIdx, showtime);

    Helper.logger("ShowtimeHandler.updateShowtime", "AVAIL SEATS: " + getAvailableSeatCount(showtimeIdx));

    status = true;

    // Serialize data
    this.saveShowtimes();

    return status;
  }

//  /**
//   * Update showtime boolean.
//   *
//   * @param cinemaId the cinema id
//   * @param movieId  the movie id
//   * @param showType the show type
//   * @param datetime the datetime
//   * @param seats    the seats
//   * @return the boolean
//   */
////+ updateShowtime(cinemaId:int, movieId:int, datetime:LocalDateTime, seats:boolean[][]):boolean
//  public boolean updateShowtime(int cinemaId, int movieId, Showtime.ShowType showType, LocalDateTime datetime, boolean[][] seats) {
//    boolean status = false;
//    if (this.showtimes.size() < 1 || this.selectedShowtimeIdx < 0) return status;
//
//    Showtime showtime = this.showtimes.get(this.selectedShowtimeIdx);
//    if (showtime == null) return status;
//
//    showtime.setCinemaId(cinemaId);
//    showtime.setMovieId(movieId);
//    showtime.setType(showType);
//    showtime.setDatetime(datetime);
//    showtime.setSeats(seats);
//    this.showtimes.set(this.selectedShowtimeIdx, showtime);
//    Helper.logger("ShowtimeHandler.updateShowtime", "AVAIL SEATS: " + getAvailableSeatCount(this.selectedShowtimeIdx));
//
//    status = true;
//
//    // Serialize data
//    this.saveShowtimes();
//
//    return status;
//  }

  /**
   * Remove showtime boolean.
   *
   * @param showtimeId the showtime id
   * @return the boolean
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
   * Print showtime details.
   *
   * @param showtimeIdx the showtime idx
   * @return the string
   */
//+ printShowtimeDetails(showtimeIdx : int) : void
  public String printShowtimeDetails(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return "";
    this.selectedShowtimeIdx = showtimeIdx;

    String header = "\n/// SHOWTIME DETAILS ///";
    colorPrint(header, Preset.HIGHLIGHT);
    colorPrint(showtime.toString(), Preset.HIGHLIGHT);

    return header + "\n" + showtime;
  }

  /**
   * Print seats.
   *
   * @param showtimeIdx the showtime idx
   */
// +printSeats(showtimeId:int):void
  public void printSeats(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtimeIdx < 0 || showtime == null) return;
    boolean[][] seats = showtime.getSeats();
    this.printSeats(seats, new ArrayList<int[]>());
  }

  /**
   * Print seats.
   *
   * @param seats         the seats
   * @param selectedSeats the selected
   */
// +printSeats(seats:boolean[][]):void
  public void printSeats(boolean[][] seats, List<int[]> selectedSeats) {
    //TODO: Current selection color
    boolean hasSelected = selectedSeats.size() > 0;

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

      // Check if row has selected seats
      int currentRow = row;
      List<int[]> rowSelectedSeats = new ArrayList<int[]>();
      if (hasSelected) rowSelectedSeats = selectedSeats.stream().filter(s -> s[0] == currentRow).toList();
//      logger("ShowtimeHandler.printSeats", "Selected seats: " + Arrays.deepToString(selectedSeats.toArray()));
//      logger("ShowtimeHandler.printSeats", "rowSelectedSeats: " + Arrays.deepToString(rowSelectedSeats.toArray()));

      for (int col = 0; col < seats[row].length; col++) {
        if (col > 0 && (col == GAP_COL || col == seats[row].length - GAP_COL)) {
          System.out.print(" - ");
        }
        boolean isAvailable = (seats[row][col]);
        int currentCol = col;
        boolean isSelected = hasSelected && rowSelectedSeats.size() > 0 && rowSelectedSeats.stream().anyMatch(s -> s[1] == currentCol);
//        logger("ShowtimeHandler.printSeats", "isSelected: " + isSelected);

        String seat = isAvailable ? "|O|" : "|X|";
        Preset preset = (isAvailable) ? Preset.HIGHLIGHT : Preset.LOG;
        if (isSelected) preset = Preset.CURRENT;
        System.out.print(colorizer(seat, preset));
      }
      System.out.print(new StringBuilder(strRowIdx).reverse());
      System.out.println();
    }
    System.out.println();
  }

  /**
   * Bulk assign seat boolean.
   *
   * @param showtimeIdx            the showtime idx
   * @param seatCodes              the seat codes
   * @param availabilityAssignment the availability assignment
   * @return the boolean
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
   * Assign seat boolean.
   *
   * @param showtimeIdx            the showtime idx
   * @param seatCode               the seat code
   * @param availabilityAssignment the availability assignment
   * @return the boolean
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

    this.updateShowtimeSeats(showtimeIdx, seats);

    Helper.logger("ShowtimeHandler.assignSeat", "Showtime-SEATS: " + this.getAvailableSeatCount(showtimeIdx));
    Helper.logger("ShowtimeHandler.assignSeat", "SEATS: " + this.getAvailableSeatCount(showtimeIdx));

    return status;
  }

  /**
   * Assign seat boolean [ ] [ ].
   *
   * @param seats                  the seats
   * @param seatCode               the seat code
   * @param availabilityAssignment the availability assignment
   * @return the boolean [ ] [ ]
   */
//+ assignSeat(seats:boolean[][], seatCode:int[], availabilityAssignment:boolean):boolean[][]
  public boolean[][] assignSeat(boolean[][] seats, int[] seatCode, boolean availabilityAssignment) {
    seats[seatCode[0]][seatCode[1]] = !availabilityAssignment;
    return seats;
  }

  /**
   * Gets available seat count.
   *
   * @param showtimeIdx the showtime idx
   * @return the available seat count
   */
//+ getAvailableSeatCount(showtimeIdx:int) : int
  public int getAvailableSeatCount(int showtimeIdx) {
    Showtime showtime = this.getShowtime(showtimeIdx);
    if (showtime == null) return -1;

    return showtime.getSeatCount(true);
  }

  /**
   * Save showtimes boolean.
   *
   * @return the boolean
   */
//# saveShowtimes():boolean
  protected boolean saveShowtimes() {
    return Datasource.serializeData(this.showtimes, "showtimes.csv");
  }
}
