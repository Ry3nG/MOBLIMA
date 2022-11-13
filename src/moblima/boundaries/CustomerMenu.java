package moblima.boundaries;

import moblima.control.controllers.CustomerController;
import moblima.control.handlers.CustomerHandler;
import moblima.entities.Booking;
import moblima.entities.Customer;
import moblima.entities.Showtime;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;
import moblima.utils.services.email.EmailService;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static moblima.utils.Helper.colorPrint;

/**
 * The type Customer menu.
 */
public class CustomerMenu extends Menu {
  private static CustomerHandler handler;
  private static CustomerController controller;
  private static CustomerMenu instance;

  private CustomerMenu() {
    super();

    handler = new CustomerHandler();
    controller = CustomerController.getInstance();

    Helper.logger("CustomerMenu", "Initialization");

    this.refreshMenu(this.getCustomerMenu());
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static CustomerMenu getInstance() {
    Helper.logger("CustomerMenu", "getInstance");
    if (instance == null) instance = new CustomerMenu();
    return instance;
  }

  @Override
  public void showMenu() {
    // Refresh menu
    this.refreshMenu(this.getCustomerMenu());
    Helper.logger("Menu.displayMenu", "MAX: " + (this.menuMap.size() - 1));

    this.displayMenu();
  }

  /**
   * Gets customer menu.
   *
   * @return the customer menu
   */
//# getCustomerMenu(): LinkedHashMap<String, Runnable>
  protected LinkedHashMap<String, Runnable> getCustomerMenu() {
    LinkedHashMap<String, Runnable> menuMap = controller.getCustomerMenu();
    LinkedHashMap<String, Runnable> addMenuMap = new LinkedHashMap<String, Runnable>() {{
      put("Book and purchase ticket", () -> makeBooking());
      put("View booking history", () -> viewBookings());
    }};

    if (controller.settingsHandler().checkIfIsAuthenticated()) {
      addMenuMap.put("View account details", () -> System.out.println(handler.getCurrentCustomer().toString()));
    } else {
      addMenuMap.put("Login / Register Account", this::getCurrentCustomer);
    }

    addMenuMap.put("Exit", () -> {
      System.out.println("\t>>> Quitting application...");
      System.out.println("---------------------------------------------------------------------------");
      System.out.println("Thank you for using MOBLIMA. We hope to see you again soon!");
      scanner.close();
      System.exit(0);
    });

    menuMap.putAll(addMenuMap);
    return menuMap;
  }

  /**
   * Gets current customer.
   *
   * @return the current customer
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
    controller.settingsHandler().setIsAuthenticated(handler.getCurrentCustomer());

    // Refresh menu
    this.refreshMenu(this.getCustomerMenu());
    Helper.logger("Menu.getCurrentCustomer.REFRESHED", "CustomerMenu: " + (this.menuMap.size()));
    Helper.logger("Menu.getCurrentCustomer.REFRESHED", "CustomerMenu MAX: " + (this.menuMap.size() - 1));


    return customerIdx;
  }

  /**
   * Register int.
   *
   * @return the int
   */
//+ register():int
  public int register() {
    int customerIdx = -1;

    System.out.println("Account Registration");
    String name = null, contactNumber = null, emailAddress = null;
    while (customerIdx == -1) {
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
            colorPrint("Invalid input, SG phone numbers requires exactly 8 digits.", Preset.ERROR);
            contactNumber = null;
            continue;
          }
        }

        // Prompt for registration requirements
        if (emailAddress == null) {
          System.out.print("Email: ");
          emailAddress = scanner.next().trim();
          /// VALIDATION: Valid email address
          if (!EmailValidator.getInstance().isValid(emailAddress)) {
            colorPrint("Invalid input, not a valid email address", Preset.ERROR);
            emailAddress = null;
            continue;
          }
        }

        // Initialize and append to existing customer list
        Helper.logger("CustomerMenu.register", name + " | " + contactNumber + " | " + emailAddress);
        customerIdx = handler.addCustomer(name, contactNumber, emailAddress);

        if (customerIdx < 0) throw new Exception("Unable to register, account with phone number already exists");


        // Send registration email
        Customer customer = handler.getCustomer(customerIdx);
        new EmailService().sentRegistrationEmail(customer.getName(), customer.getEmailAddress());

        colorPrint("Successful account registration", Preset.SUCCESS);


        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);
        name = contactNumber = emailAddress = null;

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
        if (proceedSelection == proceedOptions.size() - 1) return -1;
        else if (proceedSelection == 1) {
          this.login();
          return this.getCurrentCustomer();
        }
      }
    }

    return customerIdx;
  }

  /**
   * Login int.
   *
   * @return the int
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
          colorPrint("Invalid input, SG phone numbers requires exactly 8 digits.", Preset.ERROR);
          continue;
        }

        // Retrieve customer idx of contact number
        customerIdx = handler.checkIfAccountExists(contactNumber, "");
        if (customerIdx == -1) throw new Exception("Invalid login credentials, unable to authenticate");

        colorPrint("Successful account login", Preset.SUCCESS);
        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);

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
        if (proceedSelection == proceedOptions.size() - 1) return -1;
        else if (proceedSelection == 1) {
          this.register();
          return this.getCurrentCustomer();
        }
      }
    }

    return customerIdx;
  }

  /**
   * Make booking int.
   *
   * @return the int
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
        String strCustomer = "ACCOUNT: " + customer.getId() + " / NAME: " + customer.getName();
        colorPrint(strCustomer, Preset.SUCCESS);

        // Redirect to controller for interactivity
        bookingIdx = controller.makeBooking(customer, showtime);
        Helper.logger("CustomerMenu.makeBooking", "BookingIdx: " + bookingIdx);
        if (bookingIdx < 0) return bookingIdx;


        // Print out Booking transaction id
        Booking booking = controller.bookingHandler().getBooking(bookingIdx);
        if (booking == null) return bookingIdx;
        Helper.logger("CustomerMenu.makeBooking", "Booking: " + booking);
        colorPrint("Successfully booked. Reference: " + booking.getTransactionId(), Preset.SUCCESS);
      }
    }

    return bookingIdx;
  }

  /**
   * View bookings.
   */
//+ viewBookings():void
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
