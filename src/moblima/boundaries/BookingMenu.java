package moblima.boundaries;

import moblima.control.handlers.BookingHandler;
import moblima.entities.Booking;
import moblima.entities.Cinema;
import moblima.entities.Movie;
import moblima.entities.Movie.ShowStatus;
import moblima.entities.Showtime;
import moblima.utils.Helper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static moblima.utils.Helper.colorizer;
import static moblima.utils.deserializers.LocalDateTimeDeserializer.dateTimeFormatter;

/**
 * The type Booking menu.
 */
public class BookingMenu extends Menu {
  private static BookingHandler handler;
  private static BookingMenu instance;

  private BookingMenu() {
    super();
    handler = new BookingHandler();
    this.refreshMenu(this.getShowtimeMenu(handler.getShowtimes()));
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static BookingMenu getInstance() {
    if (instance == null) instance = new BookingMenu();
    return instance;
  }

  /**
   * Gets handler.
   *
   * @return the handler
   */
  public static BookingHandler getHandler() {
    return handler;
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

  /**
   * Gets showtime menu.
   *
   * @param showtimes the showtimes
   * @return the showtime menu
   */
//+ getShowtimeMenu(showtimes:List<Showtime>):LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getShowtimeMenu(List<Showtime> showtimes) {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
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
   * Gets booking menu.
   *
   * @param customerId the customer id
   * @return the booking menu
   */
//+ getBookingMenu(customerId:String):LinkedHashMap<String, Runnable>
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
   * Gets cinema menu.
   *
   * @return the cinema menu
   */
//+ getCinemaMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getCinemaMenu() {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    List<Cinema> cinemas = handler.getCinemas();
    if (cinemas.size() < 1) {
      System.out.println("No cinemas available.");
    } else {
      for (int i = 0; i < cinemas.size(); i++) {
        Cinema cinema = cinemas.get(i);
        menuMap.put((i + 1) + ". " + cinema.getClassType(), () -> {
          handler.setSelectedCinemaId(cinema.getId());
          System.out.println(cinema);
//          this.editCinema(cinema.getId());
        });
      }
    }
    menuMap.put((menuMap.size() + 1) + ". Add new cinema", this::registerCinema);
    menuMap.put((menuMap.size() + 1) + ". Return to previous menu", () -> System.out.println("\t>>> " + "Returning to previous menu..."));
    return menuMap;
  }

  /**
   * Select booking idx int.
   *
   * @param customerId the customer id
   * @return the int
   */
//+ selectBookingIdx(customerId:String):int
  public int selectBookingIdx(String customerId) {
    this.refreshMenu(this.getBookingMenu(customerId));

    this.displayMenu();
    return -1;
  }

  /**
   * Select cinema idx int.
   *
   * @return the int
   */
//+ selectCinemaIdx():int
  public int selectCinemaIdx() {
//    this.refreshMenu(this.getCinemaMenu());
//    this.showMenu();

    // Initialize options with a return at the end
    List<Cinema> cinemas = handler.getCinemas();
    List<String> cinemaOptions = cinemas.stream()
        .map(c -> "\n" + c.toString())
        .collect(Collectors.toList());
    cinemaOptions.add((cinemaOptions.size()), "Add new cinema");
    cinemaOptions.add((cinemaOptions.size()), "Return to previous menu");

    // Display options and get selection input
    this.displayMenuList(cinemaOptions);
    int selectedIdx = this.getListSelectionIdx(cinemaOptions, false);

    // Return to previous menu
    if (selectedIdx == (cinemaOptions.size() - 1)) {
      System.out.println("\t>>> " + "Returning to previous menu...");
      return -1;
    }

    // Add new cinema
    else if (selectedIdx == (cinemaOptions.size() - 2)) {
      selectedIdx = this.registerCinema();
    }


    // Display selected
    Cinema cinema = cinemas.get(selectedIdx);
    System.out.println(cinema);

    return selectedIdx;
  }

  public int selectShowtimeIdx(List<Showtime> showtimes) {
    this.refreshMenu(this.getShowtimeMenu(showtimes));

    // Initialize options with a return at the end
    List<String> showtimeOptions = showtimes.stream()
        .map((s) -> handler.printedShowtime(s.getId()))
        .collect(Collectors.toList());
    showtimeOptions.add((showtimeOptions.size()), "Return to previous menu");

    // Display options and get selection input
    this.displayMenuList(showtimeOptions);
    int selectedIdx = this.getListSelectionIdx(showtimeOptions, false);

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
   * Select showtime idx int.
   *
   * @param showtimes the showtimes
   * @return the int
   */
//+ selectShowtimeIdx(showtimes:List<Showtime>):int
  public int selectMovieShowtimeIdx(int movieId, List<Showtime> showtimes, boolean showAdd) {
    this.refreshMenu(this.getShowtimeMenu(showtimes));

    // Initialize options with a return at the end
    List<String> showtimeOptions = showtimes.stream()
        .map((s) -> handler.printedShowtime(s.getId()))
        .collect(Collectors.toList());
    if (showAdd) showtimeOptions.add((showtimeOptions.size()), "Add showtime");
    showtimeOptions.add((showtimeOptions.size()), "Return to previous menu");

    // Display options and get selection input
    this.displayMenuList(showtimeOptions);
    int selectedIdx = this.getListSelectionIdx(showtimeOptions, false);

    // Return to previous menu
    if (selectedIdx == (showtimeOptions.size() - 1)) {
      System.out.println("\t>>> " + "Returning to previous menu...");
      return -1;
    }

    // Add showtime
    if (showAdd && (selectedIdx == (showtimeOptions.size() - 2))) {
      return this.createMovieShowtime(movieId);
    }


    // Retrieve showtime idx from showtime id
    Showtime showtime = showtimes.get(selectedIdx);
    int showtimeIdx = handler.getShowtimeIdx(showtime.getId());
    // Store selection idx
    handler.setSelectedShowtimeIdx(showtimeIdx);

    return showtimeIdx;
  }

  /**
   * Select ticket booking . ticket type.
   *
   * @param ticketOptions the ticket options
   * @return the booking . ticket type
   */
  public Booking.TicketType selectTicket(List<String> ticketOptions) {

    int selectionIdx = -1;
    while (selectionIdx < 0) {
      System.out.println("Select ticket type:");
      this.displayMenuList(ticketOptions);
      selectionIdx = getListSelectionIdx(ticketOptions, false);
    }

    // Retrieve ticket type
    Booking.TicketType selectedTicket = Booking.TicketType.values()[selectionIdx];
    return selectedTicket;
  }

  /**
   * Select seat list.
   *
   * @param showtimeIdx the showtime idx
   * @return the list
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
        case 0 -> {
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
        }


        // Selection Confirmation
        case 1 -> {
          // Finalize the seat selection
          System.out.println("Confirmed Seat Selection");
          handler.printSeats(showtimeSeats);
          return selectedSeats;
        }


        // Discard Selection, Return without saving
        default -> {
          handler.bulkAssignSeat(showtimeIdx, selectedSeats, false);
          showtimeSeats = handler.getShowtime(showtimeIdx).getSeats();
          selectedSeats = new ArrayList<int[]>();

          // Return to previous menu
          if (confirmationSelection == confirmationOptions.size() - 1) return selectedSeats;
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
   * Seat selection int [ ].
   *
   * @param showtimeIdx the showtime idx
   * @return the int [ ]
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

  public int createMovieShowtime(int movieId) {
    int showtimeIdx = -1;

    //TODO:
    while (showtimeIdx < 0) {
      scanner = new Scanner(System.in).useDelimiter("\n");

      System.out.print("Cinema ID: ");
      int cinemaId = this.setCinemaId();
      System.out.println("Cinema ID: " + cinemaId + "\n");

      LocalDateTime showDateTime = this.setDateTime("Show Datetime (dd-MM-yyyy hh:mm[AM/PM]):");

      Showtime.ShowType showType = this.setShowType();
      System.out.println("Show Type: " + showType);

      showtimeIdx = handler.addShowtime(cinemaId, movieId, showDateTime, showType);
      if (showtimeIdx < 0) System.out.println(colorizer("[FAILED] Unable to add showtime", Helper.Preset.ERROR));
      else System.out.println(colorizer("[SUCCESS] Added new showtime", Helper.Preset.SUCCESS));

      this.awaitContinue();
    }

    return showtimeIdx;
  }

  public int setCinemaId() {
    List<String> updateOptions = handler.getCinemas().stream()
        .map(Cinema::toString)
        .collect(Collectors.toList());

    System.out.println("Set to:");
    this.displayMenuList(updateOptions);
    int selectionIdx = getListSelectionIdx(updateOptions, false);

    return selectionIdx;
  }

  public int setMovieId() {
    List<Movie> movies = MovieMenu.getHandler().getMovies(ShowStatus.NOW_SHOWING);
    List<String> updateOptions = movies.stream()
        .map(Movie::getTitle)
        .collect(Collectors.toList());

    System.out.println("Set to:");
    this.displayMenuList(updateOptions);
    int selectionIdx = getListSelectionIdx(updateOptions, false);

    return (selectionIdx < 0) ? selectionIdx : movies.get(selectionIdx).getId();
  }

  public Showtime.ShowType setShowType() {
    List<String> updateOptions = Stream.of(Showtime.ShowType.values())
        .map(Showtime.ShowType::toString)
        .collect(Collectors.toList());

    System.out.println("Set to:");
    this.displayMenuList(updateOptions);
    int selectionIdx = getListSelectionIdx(updateOptions, false);

    return Showtime.ShowType.values()[selectionIdx];
  }

  /**
   * Edit showtime boolean.
   *
   * @param showtimeId the showtime id
   * @return the boolean
   */
//+ editShowtime(showtimeId:int) : boolean
  public boolean editShowtime(String showtimeId) {
    boolean status = false;

    Showtime showtime = handler.getShowtime(showtimeId);
    if (showtime == null) return status;

    int showtimeIdx = handler.getShowtimeIdx(showtimeId);
    handler.printShowtimeDetails(showtimeIdx);

    //TODO: Update with ShowType
    List<String> proceedOptions = new ArrayList<String>() {
      {
        add("Set Cinema ID");
        add("Set Movie ID");
        add("Set Datetime");
        add("Set Show Type");
        add("Discard changes");
        add("Remove showtime");
        add("Save changes & return");
        add("Return to previous menu");
      }
    };

    while (!status) {
      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);

      // Save changes & return OR Return to previous menu
      if (proceedSelection >= proceedOptions.size() - 3) {
        // Save changes
        if (proceedSelection == proceedOptions.size() - 2) {
          handler.updateShowtime(
              showtime.getCinemaId(),
              showtime.getMovieId(),
              showtime.getType(),
              showtime.getDatetime(),
              showtime.getSeats()
          );
          status = true;
          System.out.println(colorizer("[UPDATED] Showtime updated", Helper.Preset.SUCCESS));
        }
        // Remove movie
        else if (proceedSelection == proceedOptions.size() - 3) {
          // VALIDATION: Check if showtime has associated bookings
          if (handler.checkIfShowtimeHasBooking(showtime.getId())) {
            System.out.println(colorizer("[FAILED] Unable to remove showtime with associated bookings", Helper.Preset.ERROR));
            continue;
          }

          System.out.println(colorizer("[UPDATED] Showtime removed", Helper.Preset.SUCCESS));
          handler.removeShowtime(showtime.getId());
        }

        System.out.println("\t>>> " + "Returning to previous menu...");
        return status;
      }

      // Discard changes
      else if (proceedSelection == proceedOptions.size() - 4) {
        System.out.println("[REVERTED] Changes discarded");
        showtime = handler.getShowtime(showtimeIdx);
        System.out.println(showtime);
      }

      // Set Cinema ID
      else if (proceedSelection == 0) {
        int prevStatus = showtime.getCinemaId();
        System.out.println("[CURRENT] Cinema ID: " + prevStatus);

        //TODO: Extract as separate function
//        List<String> updateOptions = handler.getCinemas().stream()
//            .map(Cinema::toString)
//            .collect(Collectors.toList());
//
//        System.out.println("Set to:");
//        this.displayMenuList(updateOptions);
//        int selectionIdx = getListSelectionIdx(updateOptions, false);

        int cinemaId = this.setCinemaId();

        if (handler.checkClashingShowtime(cinemaId, showtime.getDatetime())) {
          System.out.println("[NO CHANGE] Cinema already has a showing at the given datetime");
          continue;
        }
        showtime.setCinemaId(cinemaId);
        int curStatus = showtime.getCinemaId();

        if (prevStatus == curStatus) {
          System.out.println("[NO CHANGE] Cinema ID: " + prevStatus);
        } else {
          System.out.println("[UPDATED] Cinema ID: " + prevStatus + " -> " + curStatus);
        }
      }

      // Set Movie ID
      else if (proceedSelection == 1) {
        int prevStatus = showtime.getMovieId();
        System.out.println("[CURRENT] Movie ID: " + prevStatus);

        //TODO: Extract as separate function
//        List<Movie> movies = MovieMenu.getHandler().getMovies(ShowStatus.NOW_SHOWING);
//        List<String> updateOptions = movies.stream()
//            .map(Movie::getTitle)
//            .collect(Collectors.toList());
//
//        System.out.println("Set to:");
//        this.displayMenuList(updateOptions);
//        int selectionIdx = getListSelectionIdx(updateOptions, false);

        int movieId = this.setMovieId();

        // VALIDATION: Check if showtime has associated bookings
        if (handler.checkIfShowtimeHasBooking(showtime.getId())) {
          System.out.println("[NO CHANGE] Unable to change movie ID of showtime with associated bookings");
          continue;
        }

        showtime.setMovieId(movieId);
        int curStatus = showtime.getMovieId();

        if (prevStatus == curStatus) {
          System.out.println("[NO CHANGE] Movie ID: " + prevStatus);
        } else {
          System.out.println("[UPDATED] Movie ID: " + prevStatus + " -> " + curStatus);
        }
      }

      // Set Datetime
      else if (proceedSelection == 2) {
        LocalDateTime prevStatus = showtime.getDatetime();
        System.out.println("[CURRENT] Datetime: " + prevStatus.format(dateTimeFormatter));

        //TODO: Extract as separate function
        LocalDateTime showDatetime = this.setDateTime("Set to (dd-MM-yyyy hh:mm[AM/PM]):");
        if (handler.checkClashingShowtime(showtimeIdx, showDatetime)) {
          System.out.println("[NO CHANGE] Cinema already has a showing at the given datetime");
        } else {
          showtime.setDatetime(showDatetime);
          if (prevStatus.isEqual(showDatetime)) {
            System.out.println("[NO CHANGE] Datetime: " + prevStatus.format(dateTimeFormatter));
          } else {
            System.out.println("[UPDATED] Datetime: " + prevStatus.format(dateTimeFormatter) + " -> " + showDatetime.format(dateTimeFormatter));
          }
        }
      }

      // Set Show Type
      else if (proceedSelection == 3) {
        Showtime.ShowType prevStatus = showtime.getType();
        System.out.println("[CURRENT] Show Type: " + prevStatus.toString());

        //TODO: Extract as separate function
//        List<String> updateOptions = Stream.of(Showtime.ShowType.values())
//            .map(Showtime.ShowType::toString)
//            .collect(Collectors.toList());
//
//        System.out.println("Set to:");
//        this.displayMenuList(updateOptions);
//        int selectionIdx = getListSelectionIdx(updateOptions, false);

        Showtime.ShowType showType = this.setShowType();

        // VALIDATION: Check if showtime has associated bookings
        if (handler.checkIfShowtimeHasBooking(showtime.getId())) {
          System.out.println("[NO CHANGE] Unable to change movie ID of showtime with associated bookings");
          continue;
        }

        showtime.setType(showType);
        Showtime.ShowType curStatus = showtime.getType();

        if (prevStatus == curStatus) {
          System.out.println("[NO CHANGE] Show Type: " + prevStatus);
        } else {
          System.out.println("[UPDATED] Show Type: " + prevStatus + " -> " + curStatus);
        }

      }
    }
    return status;
  }

  /**
   * Edit cinema boolean.
   *
   * @param cinemaId the cinema id
   * @return the boolean
   */
//+ editCinema(cinemaId:int) : boolean
  public boolean editCinema(int cinemaId) {
    boolean status = false;

    Cinema cinema = handler.getCinema(cinemaId);
    List<Showtime> cinemaShowtimes = handler.getCinemaShowtimes(cinemaId);
    cinema.setShowtimes(cinemaShowtimes);
    Helper.logger("BookingMenu.editCinema", "Cinema: " + cinema);
    Helper.logger("BookingMenu.editCinema", "Cinema Showtimes: " + cinemaShowtimes);
    if (cinema == null) return status;

    List<String> proceedOptions = new ArrayList<String>() {
      {
        add("Set Class");
        add("Set Showtimes");
        add("Set Cineplex Code");
        add("Discard changes");
        add("Remove cinema");
        add("Save changes & return");
        add("Return to previous menu");
      }
    };

    while (!status) {
      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);

      // Save changes & return OR Return to previous menu
      if (proceedSelection >= proceedOptions.size() - 3) {
        // Save changes
        if (proceedSelection == proceedOptions.size() - 2) {
          handler.updateCinema(
              cinema.getClassType(),
              cinema.getShowtimes(),
              cinema.getCineplexCode()
          );
          status = true;
        }
        // Remove movie
        else if (proceedSelection == proceedOptions.size() - 3) {
          // VALIDATION: Check if cinema has associated bookings
          if (handler.checkIfCinemaHasBooking(cinema.getId())) {
            System.out.println("Unable to remove cinema with associated bookings");
            continue;
          }

          System.out.println("[UPDATED] Cinema removed");
          handler.removeCinema(cinema.getId());
        }

        System.out.println("\t>>> " + "Returning to previous menu...");
        return status;
      }

      // Discard changes
      else if (proceedSelection == proceedOptions.size() - 4) {
        System.out.println("[REVERTED] Changes discarded");
        cinema = handler.getCinema(cinemaId);
        cinemaShowtimes = handler.getCinemaShowtimes(cinemaId);
        cinema.setShowtimes(cinemaShowtimes);
      }

      // Set Class Type
      else if (proceedSelection == 0) {
        Cinema.ClassType prevStatus = cinema.getClassType();
        System.out.println("[CURRENT] Class: " + prevStatus);

        List<Cinema.ClassType> classTypes = new ArrayList<Cinema.ClassType>(EnumSet.allOf(Cinema.ClassType.class));
        List<String> typeOptions = Stream.of(Cinema.ClassType.values())
            .map(Enum::toString)
            .collect(Collectors.toList());

        System.out.println("Set to:");
        this.displayMenuList(typeOptions);
        int selectionIdx = getListSelectionIdx(typeOptions, false);

        cinema.setClassType(classTypes.get(selectionIdx));
        Cinema.ClassType curStatus = cinema.getClassType();

        if (prevStatus == curStatus) {
          System.out.println("[NO CHANGE] Class: " + prevStatus);
        } else {
          System.out.println("[UPDATED] Class: " + prevStatus + " -> " + curStatus);
        }
      }

      // Set Showtimes
      else if (proceedSelection == 1) {
        List<Showtime> showtimes = cinema.getShowtimes();
        int showtimeIdx = this.selectShowtimeIdx(showtimes);
        while (showtimeIdx >= 0) {
          Showtime showtime = handler.getShowtime(showtimeIdx);
          this.editShowtime(showtime.getId());

//          // Print updated showtime
//          System.out.println(showtime);

          // Refresh showtimes and display options
          showtimes = handler.getCinemaShowtimes(cinemaId);
          showtimeIdx = this.selectShowtimeIdx(showtimes);
        }
      }

      // Set Cineplex Code
      else if (proceedSelection == 2) {
        String prevStatus = cinema.getCineplexCode();
        System.out.println("[CURRENT] Cineplex Code: " + prevStatus);

        List<String> cineplexCodes = handler.getCineplexCodes().stream()
            .filter(c -> !c.equals(prevStatus))
            .collect(Collectors.toList());

        System.out.println("Set to:");
        this.displayMenuList(cineplexCodes);
        int selectionIdx = getListSelectionIdx(cineplexCodes, false);

        cinema.setCineplexCode(cineplexCodes.get(selectionIdx));
        String curStatus = cinema.getCineplexCode();

        if (prevStatus.equals(curStatus)) {
          System.out.println("[NO CHANGE] Cineplex Code: " + prevStatus);
        } else {
          System.out.println("[UPDATED] Cineplex Code: " + prevStatus + " -> " + curStatus);
        }
      }

      // Print updated cinema
      System.out.println(cinema);

    }

    return status;
  }

  /**
   * Register cinema int.
   *
   * @return the int
   */
//+ registerCinema():int
  public int registerCinema() {
    int cinemaId = -1;

    System.out.println("Cinema Registration");
    while (cinemaId == -1 && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        List<Cinema.ClassType> classTypes = new ArrayList<Cinema.ClassType>(EnumSet.allOf(Cinema.ClassType.class));
        List<String> typeOptions = Stream.of(Cinema.ClassType.values())
            .map(Enum::toString)
            .collect(Collectors.toList());
        typeOptions.add("Return to previous menu");

        System.out.println("Class type:");
        this.displayMenuList(typeOptions);
        int typeSelection = getListSelectionIdx(typeOptions, false);

        // Prompt for Cineplex Code
        String cineplexCode = null;
        while (cineplexCode == null) {
          System.out.print("Cineplex Code (i.e, XYZ):");
          String inputCineplexCode = scanner.next().trim();

          // VALIDATION: Check if it's exactly 3 characters
          if (!inputCineplexCode.matches("^([A-Z-0-9]{3})\\b")) continue;

          cineplexCode = inputCineplexCode;
        }

        // Return to previous menu
        if (typeSelection == typeOptions.size() - 1) {
          System.out.println("\t>>> " + "Returning to previous menu...");
          return cinemaId;
        }
        Cinema.ClassType classType = classTypes.get(typeSelection);
        cinemaId = handler.addCinema(classType, new ArrayList<Showtime>(), cineplexCode);
        this.refreshMenu(this.getCinemaMenu());

        System.out.println("Successful cinema registration");
        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    return cinemaId;
  }

}
