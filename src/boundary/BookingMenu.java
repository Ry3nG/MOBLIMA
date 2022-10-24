package boundary;

import control.BookingHandler;
import entity.Booking;
import entity.Menu;
import entity.Showtime;
import utils.Helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookingMenu extends Menu {
  private static BookingHandler handler;
  private static BookingMenu instance;

  private BookingMenu() {
    super();
    handler = new BookingHandler();
    this.refreshMenu(this.getShowtimeMenu());
  }

  public static BookingMenu getInstance() {
    if (instance == null) instance = new BookingMenu();
    return instance;
  }

  /**
   * Get booking handler
   *
   * @return bookingHandler:BookingHandler
   */
  public static BookingHandler getHandler() {
    return handler;
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

  /**
   * Get the updated showtime list to be displayed
   *
   * @return menuMap:LinkedHashMap<String, Runnable>
   */
  //+ getShowtimeMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getShowtimeMenu() {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    List<Showtime> showtimes = handler.getShowtimes();
    if (showtimes.size() < 1) {
      System.out.println("No showtimes available.");
    } else {
      for (int i = 0; i < showtimes.size(); i++) {
        Showtime showtime = showtimes.get(i);
        int showtimeIdx = i;
        menuMap.put((i + 1) + ". " + showtime.toString(), () -> {
          handler.setSelectedShowtimeIdx(showtimeIdx);
          handler.printShowtimeDetails(showtimeIdx);
        });
      }
    }
    menuMap.put((menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
    return menuMap;
  }

  /**
   * Get the updated booking list to be displayed
   *
   * @return menuMap:LinkedHashMap<String, Runnable>
   */
  //+ getBookingMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getBookingMenu(String customerId) {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    List<Booking> bookings = handler.getBookings(customerId);
    if (bookings.size() < 1) {
      System.out.println("No bookings available.");
    } else {
      for (int i = 0; i < bookings.size(); i++) {
        Booking booking = bookings.get(i);
        int bookingIdx = i;
        menuMap.put((i + 1) + ". " + booking.getTransactionId(), () -> {
          handler.setSelectedBookingIdx(bookingIdx);
          handler.printBooking(booking.getTransactionId());
        });
      }
    }
    menuMap.put((menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
    return menuMap;
  }

  /**
   * Retrieves the user booking selection idx with specified customer id
   *
   * @param customerId:String
   * @return selectedBookingIdx:int
   */
  //+ selectBookingIdx(customerId:String):int
  public int selectBookingIdx(String customerId) {
    this.refreshMenu(this.getBookingMenu(customerId));

    this.displayMenu();
    return -1;
  }

  /**
   * Retrieves the user showtime selection idx with specified movie id
   *
   * @param movieId:int
   * @return selectedShowtimeIdx:int
   */
  //+ selectShowtimeIdx(movieId:int):int
  public int selectShowtimeIdx(int movieId) {
    this.refreshMenu(this.getShowtimeMenu());
    List<Showtime> showtimes = handler.getShowtimes(movieId);

    // Initialize options with a return at the end
    List<String> showtimeOptions = showtimes.stream()
        .map(s -> s.toString())
        .collect(Collectors.toList());
    showtimeOptions.add((showtimeOptions.size()), "Return to previous menu");

    // Display options and get selection input
    this.displayMenuList(showtimeOptions);
    int selectedIdx = this.getListSelectionIdx(showtimes, false);

    // Return to previous menu
    if (selectedIdx == (showtimeOptions.size() - 1)) {
      System.out.println("\t>>> " + "Returning to previous menu...");
      return -1;
    }

    // Retrieve showtime idx from showtime id
    Showtime showtime = showtimes.get(selectedIdx);
    int showtimeIdx = handler.getShowtimeIdx(showtime.getId());
    // Store selection idx
    handler.setSelectedShowtimeIdx(showtimeIdx);

    return showtimeIdx;
  }

  /**
   * Retrieves the user list of seat selection idx with specified showtime idx
   *
   * @param showtimeIdx:int
   * @return seats:List<int[]>
   */
  //+ selectSeat(showtimeIdx:int):List<int[]>
  public List<int[]> selectSeat(int showtimeIdx) {
    List<String> confirmationOptions = new ArrayList<String>() {{
      add("Continue selecting more seats");
      add("Confirm booking");
      add("Discard selection");
      add("Return to previous menu");
    }};

    boolean[][] showtimeSeats = handler.getShowtime(showtimeIdx).getSeats();
    List<int[]> selectedSeats = new ArrayList<int[]>();

    int confirmationSelection = 0;
    while (confirmationSelection != confirmationOptions.size()) {

      // Seat selection
      switch (confirmationSelection) {
        case 0: {
          int[] selectedSeat = this.seatSelection(showtimeIdx);

          // VALIDATION: Check if seat was previously selected
          if (!showtimeSeats[selectedSeat[0]][selectedSeat[1]]) {
            System.out.println("Seat is already selected. Try another");
            continue;
          }

          selectedSeats.add(selectedSeat);

          // Sudo seat assignment
          handler.assignSeat(showtimeSeats, selectedSeat, true);
          handler.printSeats(showtimeSeats);

          break;
        }

        // Selection Confirmation
        case 1: {
          // Finalize the seat selection
          System.out.println("Confirmed Seat Selection");
          handler.printSeats(showtimeSeats);
          return selectedSeats;
        }

        // Discard Selection, Return without saving
        default: {
          handler.bulkAssignSeat(showtimeIdx, selectedSeats, false);
          showtimeSeats = handler.getShowtime(showtimeIdx).getSeats();
          selectedSeats = new ArrayList<int[]>();

          // Return to previous menu
          if (confirmationSelection == confirmationOptions.size() - 1) return selectedSeats;
          break;
        }
      }

      System.out.println("Next steps:");
      this.displayMenuList(confirmationOptions);
      confirmationSelection = getListSelectionIdx(confirmationOptions, false);

      Helper.logger("BookingMenu.confirmationSelection", "Max: " + (confirmationOptions.size() - 1));
      Helper.logger("BookingMenu.confirmationSelection", "Selected: " + confirmationSelection);

    }

    return selectedSeats;
  }

  /**
   * Retrieves the user seat selection idx with specified showtime idx
   *
   * @param showtimeIdx:int
   * @return seat:int[]
   */
  //+ seatSelection(showtimeIdx:int):int[]
  public int[] seatSelection(int showtimeIdx) {
    boolean[][] seats = handler.getShowtime(showtimeIdx).getSeats();
    handler.printSeats(seats);

    int[] seatCode = new int[2];

    List<Integer> rowRange = IntStream.rangeClosed(0, seats.length).boxed().toList();
    List<Integer> colRange = IntStream.rangeClosed(0, seats[0].length).boxed().toList();

    System.out.println("Enter the seat row: ");
    seatCode[0] = this.getListSelectionIdx(rowRange, false);

    System.out.println("Enter the seat column: ");
    seatCode[1] = this.getListSelectionIdx(colRange, false);

    return seatCode;
  }
}
