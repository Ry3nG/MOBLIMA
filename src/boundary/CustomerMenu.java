package boundary;

import control.CustomerHandler;
import entity.Booking;
import entity.Customer;
import entity.Menu;
import entity.Showtime;
import tmdb.entities.Movie;
import utils.Helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu extends Menu {
  private CustomerHandler handler;
  private MovieMenu movieMenu;
  private BookingMenu bookingMenu;

  private static CustomerMenu instance;

  private CustomerMenu() {
    super();
    this.movieMenu = MovieMenu.getInstance();
    this.bookingMenu = BookingMenu.getInstance();

    this.handler = new CustomerHandler();

    this.menuMap = new LinkedHashMap<String, Runnable>() {
      {
        put("Search/List Movies", movieMenu.getHandler()::printMovies);
        put("View movie details – including reviews and ratings", movieMenu::showMenu);
        // put("Check seat availability and selection of seat/s.", () -> {
        //
        // });
        put("Book and purchase ticket", () -> {
          makeBooking();
        });
        put("View booking history", () -> {
          viewBookings();

        });
        // put("List the Top 5 ranking by ticket sales OR by overall reviewers’
        // ratings", () -> {
        // });
        put("Exit", () -> {
          System.out.println("\t>>> Quitting application...");
          System.out.println("---------------------------------------------------------------------------");
          System.out.println("Thank you for using MOBLIMA. We hope to see you again soon!");
          scanner.close();
          System.exit(0);
        });
      }
    };
  }

  public static CustomerMenu getInstance() {
    if (instance == null)
      instance = new CustomerMenu();
    return instance;
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

  /**
   * Get customer handler
   * @return customerHandler:CustomerHandler
   */
  //+ getHandler():CustomerHandler
  public CustomerHandler getHandler() {
    return this.handler;
  }

  /**
   * Retrieve currently selected / active customer via login/registration
   * @return customerIdx:int
   */
  //+ getCurrentCustomer():int
  public int getCurrentCustomer() {
    int customerIdx = -1;

    Customer customer = this.handler.getCurrentCustomer();
    if (customer != null) {
      customerIdx = this.handler.getCustomerIdx(customer.getId());
      this.handler.setCurrentCustomer(customerIdx);

      return customerIdx;
    }

    List<String> accountOptions = new ArrayList<String>() {
      {
        add("Already have an account? Login");
        add("Create account");
        // add("Discard selection");
      }
    };

    while (customerIdx == -1) {
      System.out.println("Next steps:");
      this.displayMenuList(accountOptions);
      int accountSelection = getListSelectionIdx(accountOptions, false);

      // Login account
      if (accountSelection == 0) {
        customerIdx = this.login();
      }

      // Create account
      if (accountSelection == 1) {
        customerIdx = this.register();
      }
    }

    this.handler.setCurrentCustomer(customerIdx);
    return customerIdx;
  }

  /**
   * Facilitates customer account registration
   * @return customerIdx:int
   */
  //+ register():int
  public int register() {
    int customerIdx = -1;

    String name = null, contactNumber = null;
    System.out.println("Account Registration");
    while (customerIdx == -1 && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        if (name == null) {
          System.out.print("Name: ");
          name = scanner.next().trim();
        }

        if (contactNumber == null) {
          System.out.print("Contact No.: ");
          contactNumber = scanner.next().trim();
          // VALIDATION: SG Phone Numbers requires exactly 8 digits
          if (!this.handler.validatePhoneNumber(contactNumber)) {
            System.out.println("Invalid input, SG phone numbers requires exactly 8 digits.");
            contactNumber = null;
            continue;
          }
        }

        // Initialize and append to existing customer list
        customerIdx = handler.addCustomer(name, contactNumber);
        if (customerIdx == -1)
          throw new Exception("Unable to register, account with phone number already exists");

        System.out.println("Successful account registration");

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        name = contactNumber = null;

        List<String> proceedOptions = new ArrayList<String>() {
          {
            add("Proceed with registration");
            add("Login with account instead");
            add("Return to previous menu");
          }
        };

        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        // Return to previous menu
        if (proceedSelection == proceedOptions.size() - 1)
          return -1;
        else if (proceedSelection == 1) {
          this.login();
          return this.getCurrentCustomer();
        } else
          continue;
      }
    }

    return customerIdx;
  }

  /**
   * Facilitates customer account login
   * @return customerIdx:int
   */
  //+ login():int
  public int login() {
    int customerIdx = -1;

    System.out.println("Account Login");
    while (customerIdx == -1 && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        System.out.print("Account Contact No.: ");
        String contactNumber = scanner.next().trim();
        // VALIDATION: SG Phone Numbers requires exactly 8 digits
        if (!this.handler.validatePhoneNumber(contactNumber)) {
          System.out.println("Invalid input, SG phone numbers requires exactly 8 digits.");
          continue;
        }

        customerIdx = this.handler.checkIfAccountExists(contactNumber);
        if (customerIdx == -1)
          throw new Exception("Invalid login credentials, unable to authenticate");

        System.out.println("Successful account login");

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(e.getMessage());

        List<String> proceedOptions = new ArrayList<String>() {
          {
            add("Proceed with login");
            add("Register account instead");
            add("Return to previous menu");
          }
        };

        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        // Return to previous menu
        if (proceedSelection == proceedOptions.size() - 1)
          return -1;
        else if (proceedSelection == 1){
          this.register();
          return this.getCurrentCustomer();
        }
        else
          continue;

      }
    }

    return customerIdx;
  }

  /**
   * Facilitates customer booking
   * @return bookingIdx:int
   */
  //+ makeBooking():int
  public int makeBooking() {
    int bookingIdx = -1;

    // Select movie
    System.out.println("Select movie: ");
    int movieIdx = this.movieMenu.selectMovieIdx();
    if (movieIdx < 0)
      return bookingIdx;
    Movie selectedMovie = this.movieMenu.getHandler().getMovie(movieIdx);

    // Select showtimes for selected movie
    System.out.println("Select showtime slot: ");
    int showtimeIdx = this.bookingMenu.selectShowtimeIdx(selectedMovie.getId());
    if (showtimeIdx < 0)
      return bookingIdx;
    Showtime showtime = this.bookingMenu.getHandler().getShowtime(showtimeIdx);

    // Print showtime details
    this.bookingMenu.getHandler().printShowtimeDetails(showtimeIdx);

    // Select seats
    List<int[]> seats = this.bookingMenu.selectSeat(showtimeIdx);
    Helper.logger("CustomerMenu.makeBooking", "NO. OF SEATS: " + seats.size());
    if (seats.size() < 1)
      return bookingIdx;

    // Get customer idx via login/register
    int customerIdx = this.getCurrentCustomer();
    Helper.logger("CustomerMenu.makeBooking", "customerIdx: " + customerIdx);
    if (customerIdx < 0)
      return bookingIdx;

    Customer customer = this.handler.getCustomer(customerIdx);
    if (customer == null)
      return bookingIdx;

    bookingIdx = this.bookingMenu.getHandler().addBooking(customer.getId(), showtime.getCinemaId(), showtime.getMovieId(),
        showtime.getId(), seats, 10.0, Booking.TicketType.PEAK);

    // Print out tx id
    Booking booking = this.bookingMenu.getHandler().getBooking(bookingIdx);
    if (booking == null) return bookingIdx;
    System.out.println("Successfully booked. Reference: " + booking.getTransactionId());

    return bookingIdx;
  }

  public void viewBookings() {
    // Get customer idx via login/register
    int customerIdx = this.getCurrentCustomer();
    Helper.logger("CustomerMenu.viewBookings", "customerIdx: " + customerIdx);
    if (customerIdx < 0)
      return;

    Customer customer = this.handler.getCustomer(customerIdx);
    if (customer == null)
      return;
    Helper.logger("CustomerMenu.viewBookings", "Customer ID: " + customer.getId());
    // this.bookingHandler.printBookings(customer.getId());
    this.bookingMenu.selectBookingIdx(customer.getId());
  }
}
