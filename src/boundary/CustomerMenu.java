package boundary;

import control.handlers.CustomerHandler;
import control.menu.CustomerController;
import entity.Booking;
import entity.Customer;
import entity.Showtime;
import utils.Helper;
import utils.Helper.Preset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class CustomerMenu extends Menu {
  private static CustomerHandler handler;
  private static CustomerMenu instance;
  private static CustomerController controller;

  private CustomerMenu() {
    super();
    handler = new CustomerHandler();
    controller = CustomerController.getInstance();

    this.refreshMenu(this.getCustomerMenu());
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
   * Gets full customer menu list
   *
   * @return menuMap:LinkedHashMap<String, Runnable>
   */
  //# getCustomerMenu(): LinkedHashMap<String, Runnable>
  protected LinkedHashMap<String, Runnable> getCustomerMenu() {
    LinkedHashMap<String, Runnable> menuMap = controller.getCustomerMenu();
    LinkedHashMap<String, Runnable> addMenuMap = new LinkedHashMap<String, Runnable>() {{
      put("Book and purchase ticket", () -> makeBooking());
      put("View booking history", () -> viewBookings());
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
    }};

    menuMap.putAll(addMenuMap);
    return menuMap;
  }

  /**
   * Retrieve currently selected / active customer via login/registration
   *
   * @return customerIdx:int
   */
  //+ getCurrentCustomer():int
  public int getCurrentCustomer() {
    int customerIdx = -1;

    // Currently selected customer idx
    Customer customer = handler.getCurrentCustomer();
    if (customer != null) {
      customerIdx = handler.getCustomerIdx(customer.getId());
      handler.setCurrentCustomer(customerIdx);
      return customerIdx;
    }

    // Prompt for customer via login or register
    List<String> accountOptions = new ArrayList<String>() {{
      add("Already have an account? Login");
      add("Create account");
    }};
    while (customerIdx == -1) {
      System.out.println("Next steps:");
      this.displayMenuList(accountOptions);
      int accountSelection = getListSelectionIdx(accountOptions, false);

      // Login account
      if (accountSelection == 0) {
        customerIdx = this.login();
      }
      // Create account
      else if (accountSelection == 1) {
        customerIdx = this.register();
      }
    }

    // Save current customer
    handler.setCurrentCustomer(customerIdx);
    return customerIdx;
  }

  /**
   * Facilitates customer account registration
   *
   * @return customerIdx:int
   */
  //+ register():int
  public int register() {
    int customerIdx = -1;

    System.out.println("Account Registration");
    String name = null, contactNumber = null;
    while (customerIdx == -1 && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        // Prompt for registration requirements
        if (name == null) {
          System.out.print("Name: ");
          name = scanner.next().trim();
        }

        // Prompt for registration requirements
        if (contactNumber == null) {
          System.out.print("Contact No.: ");
          contactNumber = scanner.next().trim();
          /// VALIDATION: SG Phone Numbers requires exactly 8 digits
          if (!handler.validatePhoneNumber(contactNumber)) {
            System.out.println(colorize("Invalid input, SG phone numbers requires exactly 8 digits.", Preset.ERROR.color));
            contactNumber = null;
            continue;
          }
        }

        // Initialize and append to existing customer list
        customerIdx = handler.addCustomer(name, contactNumber);
        if (customerIdx < -1)
          throw new Exception("Unable to register, account with phone number already exists");

        System.out.println(colorize("Successful account registration", Preset.SUCCESS.color));
        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(colorize(e.getMessage(), Preset.ERROR.color));
        name = contactNumber = null;

        // Prompt for proceed options
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
        }
      }
    }

    return customerIdx;
  }

  /**
   * Facilitates customer account login
   *
   * @return customerIdx:int
   */
  //+ login():int
  public int login() {
    int customerIdx = -1;

    System.out.println("Account Login");
    while (customerIdx == -1 && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        // Prompt for account credentials
        System.out.print("Account Contact No.: ");
        String contactNumber = scanner.next().trim();
        /// VALIDATION: SG Phone Numbers requires exactly 8 digits
        if (!handler.validatePhoneNumber(contactNumber)) {
          System.out.println(colorize("Invalid input, SG phone numbers requires exactly 8 digits.", Preset.ERROR.color));
          continue;
        }

        // Retrieve customer idx of contact number
        customerIdx = handler.checkIfAccountExists(contactNumber);
        if (customerIdx == -1)
          throw new Exception("Invalid login credentials, unable to authenticate");

        System.out.println(colorize("Successful account login", Preset.SUCCESS.color));
        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(colorize(e.getMessage(), Preset.ERROR.color));

        // Prompt for proceed options
        List<String> proceedOptions = new ArrayList<String>() {{
          add("Proceed with login");
          add("Register account instead");
          add("Return to previous menu");
        }};
        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        // Return to previous menu
        if (proceedSelection == proceedOptions.size() - 1)
          return -1;
        else if (proceedSelection == 1) {
          this.register();
          return this.getCurrentCustomer();
        }
      }
    }

    return customerIdx;
  }

  /**
   * Facilitates customer booking
   *
   * @return bookingIdx:int
   */
  //+ makeBooking():int
  public int makeBooking() {
    int bookingIdx = -1;

    while (bookingIdx == -1) {
      // Redirect to controller for interactivity
      int showtimeIdx = controller.viewShowtimeAvailability();
      Helper.logger("CustomerMenu.makeBooking", "showtimeIdx: " + showtimeIdx);
      if (showtimeIdx < 0) return bookingIdx;
      Showtime showtime = controller.bookingHandler().getShowtime(showtimeIdx);
      Helper.logger("CustomerMenu.makeBooking", "showtime: " + showtime);

      // Confirmation before proceeding
      List<String> proceedOptions = new ArrayList<String>() {{
        add("Proceed to make booking");
        add("Return to previous menu");
      }};
      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);

      // Proceed to make booking
      if (proceedSelection == 0) {
        // Get customer idx via login/register
        int customerIdx = this.getCurrentCustomer();
        Helper.logger("CustomerMenu.makeBooking", "customerIdx: " + customerIdx);
        if (customerIdx < 0) return bookingIdx;

        // Retrieve customer from idx
        Customer customer = handler.getCustomer(customerIdx);
        if (customer == null) return bookingIdx;

        // Redirect to controller for interactivity
        bookingIdx = controller.makeBooking(customer.getId(), showtime);
        Helper.logger("CustomerMenu.makeBooking", "BookingIdx: " + bookingIdx);
        if (bookingIdx < 0) return bookingIdx;

        // Print out Booking transaction id
        Booking booking = controller.bookingHandler().getBooking(bookingIdx);
        if (booking == null) return bookingIdx;
        Helper.logger("CustomerMenu.makeBooking", "Booking: " + booking);
        System.out.println(colorize("Successfully booked. Reference: " + booking.getTransactionId(), Preset.SUCCESS.color));
      }
    }

    return bookingIdx;
  }

  public void viewBookings() {
    // Get customer idx via login/register
    int customerIdx = this.getCurrentCustomer();
    Helper.logger("CustomerMenu.viewBookings", "customerIdx: " + customerIdx);
    if (customerIdx < 0) return;

    // Retrieve customer from customer list
    Customer customer = handler.getCustomer(customerIdx);
    if (customer == null) return;
    Helper.logger("CustomerMenu.viewBookings", "Customer ID: " + customer.getId());

    // Redirect to controller for interactivity
    controller.viewBookings(customer.getId());
  }
}
