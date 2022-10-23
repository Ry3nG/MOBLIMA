package main.boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import main.entity.SystemSettings;
import main.control.SettingsHandler;

/**
 * System Settings Menu
 * 
 * @author SS11 Group 1
 * @version 1.1
 * @since 18 October 2022
 */
public class SettingsMenu extends Menu {

  /**
   * An instance of SettingsHandler
   */
  private static SettingsHandler handler = null;
  private static final String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  
  /**
   * Constructor for SettingsMenu
   * 
   * Initialises SettingsMenu and obtains an instance of SettingsHandler.
   */
  public SettingsMenu() {
    super();
    if (handler == null) handler = SettingsHandler.getInstance();

    this.menuMap = new LinkedHashMap<String,Runnable>() {{
    put("View Current Settings", () -> viewCurrentSettings());
    put("Edit Ticket Prices", () -> editTicketPrices());
    put("Edit Surcharges", () -> editSurcharges());
    put("Add Public Holiday", () -> addPublicHoliday());
    put("Remove Public Holiday", () -> removePublicHoliday());
    put("Return to Main Menu", () -> {
      System.out.println("\t>>> Returning to Main Menu...");
      // Menu navigator over here
    });
    }};
  }

  /**
   * Displays the menu to Staff
   * 
   * @see main.boundary.Menu#showMenu()
   */
  @Override
  public void showMenu() {
    this.displayMenu();
  }

  /**
   * Obtains the system settings using the SettingsHandler and displays the system settings to Staff.
   * 
   * @since 1.1
   */
  private void viewCurrentSettings() {

    // Obtain settings
    SystemSettings settings = handler.getSettings();

    // Print settings
    System.out.println("---------------------------------------------------------------------------");

    // Check if settings is null
    if (settings == null) {
      System.out.println("Nothing is set yet! Please set the settings:\n");
      createSettings();
      return;
    }

    // Ticket prices
    System.out.printf("Ticket Prices:\n--------------\n" +
      "- %-15s: SGD %5.2f\n" +
      "- %-15s: SGD %5.2f\n" +
      "- %-15s: SGD %5.2f\n\n",
      "Adult", settings.getAdultTicket(),
      "Child", settings.getChildTicket(),
      "Senior", settings.getSeniorTicket());

    // Surcharges
    System.out.printf("Surcharges:\n-----------\n" +
      "- %-15s: SGD %5.2f (per ticket)\n" +
      "- %-15s: SGD %5.2f (per ticket)\n" +
      "- %-15s: SGD %5.2f (per ticket)\n\n",
      "Premium Class", settings.getCinemaSurchage(),
      "Special Movies", settings.getMovieSurcharge(),
      "Weekend / PH", settings.getWeekendSurcharge());

    // Public Holidays
    System.out.println("Public Holidays:\n----------------");
    ArrayList<String> ph = settings.getPublicHolidays();
    Collections.sort(ph); // sort in ascending order

    ph.forEach(x -> { // display each ph formatted
      String year = x.substring(0,4);
      int month = Integer.parseInt(x.substring(4,6));
      String day = x.substring(6,8);
      System.out.printf("- %s %s %s\n", day, monthList[month-1], year);
    });
  }

  private void createSettings() {

    // Setup
    scanner.nextLine(); // consume any remaining input in buffer - good practice before any method that gets input
    LinkedHashMap<String,Double> settings = new LinkedHashMap<String,Double>(); // setup
    settings.put("Adult Ticket", 0.0);
    settings.put("Child Ticket", 0.0);
    settings.put("Senior Ticket", 0.0);
    settings.put("Premium Class Surcharge", 0.0);
    settings.put("Special Movies Surcharge", 0.0);
    settings.put("Weekend / PH Surcharge", 0.0);

    // Get prices
    settings.forEach((key,value) ->{
      double validInput = 0;
      do {
        System.out.printf("%-24s: SGD ", key);
        String input = scanner.nextLine();
        try {
          validInput = Double.parseDouble(input);
          settings.put(key, validInput);
        } catch (Exception e) {
          System.out.printf("Invalid price. Please re-enter the price for %s.\n", key);
        }
      } while (validInput==0);
    });

    handler.createNew(settings);

    System.out.println("\nSettings saved.");
    return;
  }

  /**
   * Obtains and displays current ticket prices, and updates ticket prices according based on Staff input
   * 
   * @since 1.1
   */
  private void editTicketPrices() {

    // Obtain settings
    SystemSettings settings = handler.getSettings();

    // Setup
    scanner.nextLine(); // consume any remaining input in buffer - good practice before any method that gets input
    System.out.println("---------------------------------------------------------------------------");
    LinkedHashMap<String,Double> prices = new LinkedHashMap<String,Double>(); // setup
    prices.put("Adult", settings.getAdultTicket());
    prices.put("Child", settings.getChildTicket());
    prices.put("Senior", settings.getSeniorTicket());

    // Obtain new settings from Staff
    System.out.println("Enter the new price, or - to keep the current price");

    prices.forEach((key, value) -> { // loop through
      double checkInput;
      do {
        System.out.printf("- %-8s [Current: SGD %.2f]: SGD ", key, value);
        String input = scanner.nextLine();
        checkInput = checkPriceInput(input); // check for character input, - input, 0 input, <0 input
        if (checkInput > 0) {
          prices.put(key, checkInput); // update
        }
      } while (checkInput == 0);
    });

    // Call changeTicketPrices()
    handler.changeTicketPrices(prices);
    System.out.println("\nTicket prices updated successfully.");
  }
  
  /**
   * Obtains and displays the current surcharges, and updates the surcharges based on Staff input
   * 
   * @since 1.1
   */
  private void editSurcharges() {
    // Obtain settings
    SystemSettings settings = handler.getSettings();

    // Setup
    scanner.nextLine(); // consume any remaining input in buffer - good practice before any method that gets input
    System.out.println("---------------------------------------------------------------------------");
    LinkedHashMap<String,Double> surcharges = new LinkedHashMap<String,Double>(); // setup
    surcharges.put("Premium Class", settings.getCinemaSurchage());
    surcharges.put("Special Movies", settings.getMovieSurcharge());
    surcharges.put("Weekend / PH", settings.getWeekendSurcharge());

    // Obtain new settings from Staff
    System.out.println("Enter the new surcharge amount, or - to keep the current amount");

    surcharges.forEach((key, value) -> { // loop through
      double checkInput;
      do {
        System.out.printf("- %-15s [Current: SGD %.2f]: SGD ", key, value);
        String input = scanner.nextLine();
        checkInput = checkPriceInput(input); // check for character input, - input, 0 input, <0 input
        if (checkInput > 0) {
          surcharges.put(key, checkInput); // update
        }
      } while (checkInput == 0);
    });

    // Call changeSurcharges()
    handler.changeSurcharges(surcharges);
    System.out.println("\nSurcharges updated successfully.");
  }

  /**
   * Helper method to check validity of price input
   * 
   * @param input - String input obained from Staff
   * @return -1 if Staff does not want to change the price, 0 if the price entered is invalid, price in double format if valid price
   */
  private double checkPriceInput(String input) {
    if (input.equals("-")) return -1; // Staff does not want to change
    try {
      double price = Double.parseDouble(input);
      if (price <= 0) { // 0 or less
        System.out.println("Please enter a price that is more than SGD 0, or - to keep the current pric1e.");
        return 0;
      }
      else return price; // valid
    } catch (NumberFormatException e) { // characters other than -
      System.out.println("Please enter integers and decimal point (if needed) only, or - to keep the current price.");
      return 0;
    }
  }

  /**
   * Obtains date and adds to the list of public holidays
   * 
   * @since 1.1
   */
  private void addPublicHoliday() {

    // Setup
    scanner.nextLine(); // consume any remaining input in buffer - good practice before any method that gets input
    System.out.println("---------------------------------------------------------------------------");

    // Get date
    System.out.println("Enter the date of the public holiday:\n-------------------------------------");
    boolean validDate = false;
    do {
      System.out.print("Date [Format: DD/MM/YYYY] or enter - to cancel: ");
      String dateInput = scanner.nextLine();

      if (dateInput.equals("-")) break; // if Staff wants to cancel

      validDate = handler.addPublicHoliday(dateInput); // check if date is valid, and add if valid

      if (validDate) System.out.printf("\nPublic holiday %s has been added successfully.\n", dateInput);
      else System.out.println("\nDate is invalid. Please enter a valid date, or enter - to cancel and return to the menu.");
    } while (!validDate);
  }

  /**
   * Obtains date and removes from the list of public holidays
   * 
   * @since 1.1
   */
  private void removePublicHoliday() {
    
    // Setup
    scanner.nextLine(); // consume any remaining input in buffer - good practice before any method that gets input
    System.out.println("---------------------------------------------------------------------------");

    // Get date
    System.out.println("Enter the date of the public holiday to be removed:\n---------------------------------------------------");
    boolean dateExist = false;
    do {
      System.out.print("Date [Format: DD/MM/YYYY] or enter - to cancel: ");
      String dateInput = scanner.nextLine();

      if (dateInput.equals("-")) break; // if Staff wants to cancel

      dateExist = handler.removePublicHoliday(dateInput); // check if date exists in the settings, if yes, delete

      if (dateExist) System.out.printf("\nPublic Holiday %s has been removed successfully\n", dateInput);
      else System.out.println("\nDate is invalid. Please enter a valid date from the list of public holidays, or enter - to cancel and return to the menu");
    } while (!dateExist);
  }
}