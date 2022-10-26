package boundary;

import java.util.EnumMap;
import java.util.LinkedHashMap;

import entity.Booking;
import entity.Cinema;
import entity.SystemSettings;
import utils.Helper.Preset;

import control.handlers.SettingsHandler;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * System Settings Menu
 * 
 * @author SS11 Group 1
 * @version 1.2
 * @since 27 October 2022
 */
public class SettingsMenu extends Menu {

  /**
   * An instance of SettingsHandler
   */
  private static final SettingsHandler handler = new SettingsHandler();
  /**
   * An instance of SettingsMenu - for singleton pattern implementation
   */
  private static SettingsMenu instance = null;
  /**
   * Clone of SystemSettings for use across various functions
   */
  private SystemSettings settings;
  /**
   * Bit to track modification of settings
   */
  private boolean mod;
  
  /**
   * Constructor for SettingsMenu
   * 
   * Initialises SettingsMenu and obtains an instance of SettingsHandler.
   * 
   * @since 1.0
   */
  private SettingsMenu() {
    super();

    // Setup
    this.settings = handler.getCurrentSystemSettings();
    this.mod = false;

    // Menu
    this.menuMap = new LinkedHashMap<String,Runnable>() {{
    put("View Current Settings", () -> viewCurrentSettings());
    put("Edit Adult (Standard) Ticket Price", () -> editAdultTicketPrice());
    put("Edit Blockbuster Movie Surcharge", () -> editBlockbusterSurcharge());
    put("Edit Ticket Surcharges", () -> editTicketSurcharges());
    put("Edit Cinema Surcharges", () -> editCinemaSurcharges());
    put("Add Public Holiday", () -> addPublicHoliday());
    put("Remove Public Holiday", () -> removePublicHoliday());
    put("Discard changes", () -> {
      System.out.println(colorize("[REVERTED] Changes discarded", Preset.SUCCESS.color));
      settings = handler.getCurrentSystemSettings();
      // System.out.println("\n" + settings); - can go viewCurrentSettings if wish to see the new settings
      mod = false;
    });
    put("Save changes and return", () -> {
      handler.updateSystemSettings(settings);
      mod = false;
      System.out.println("\t>>> Returning to Main Menu...");
    });
    put("Return to Main Menu", () -> {
      if (mod) {
        System.out.print("Any changes will be discarded! Are you sure? [YES / NO] : ");
        String resp = scanner.next();
        if (resp.equals("YES") || resp.equals("Y")) {
          settings = handler.getCurrentSystemSettings();
          System.out.println("\t>>> Returning to Main Menu...");
        }
        mod = false;
      } else {
        System.out.println("\t>>> Returning to Main Menu...");
      }
    });
    }};
  }

  /**
   * Displays the menu to Staff
   * 
   * @see main.boundary.Menu#showMenu()
   * @since 1.0
   */
  @Override
  public void showMenu() {
    this.displayMenu();
  }

  /**
   * Return instance of SettingsMenu - for singleton pattern implementation
   * 
   * @return instance - instance of SettingsMenu
   */
  // +getInstance() : SettingsMenu
  public static SettingsMenu getInstance() {
    if (instance == null) instance = new SettingsMenu();
    return instance;
  }

  // KIV - no references, see MovieBookingController
  // public SettingsHandler getHandler() {
  //   return handler;
  // }

  /**
   * Prints settings
   * 
   * @since 1.1
   */
  // CD: -viewCurrentSettings()
  private void viewCurrentSettings() {
    System.out.println("---------------------------------------------------------------------------");
    System.out.println(settings);
  }

  /**
   * Obtains and updates ticket price according based on Staff input
   * 
   * @since 1.1
   */
  // CD: -editAdultTicketPrice()
  private void editAdultTicketPrice() {

    // Setup
    scanner.nextLine();
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("Enter the new price, or press - to return to the menu\n");
    System.out.println("Current Price: " + settings.formatPrice(settings.getAdultTicket()));

    // Get new price and update clone
    double checkInput;
    do {
      System.out.print("New Price: SGD ");
      String input = scanner.nextLine();
      checkInput = checkPriceInput(input, true); // check for character input, - input, 0 input, <0 input
      if (checkInput > 0) {
        boolean changed = handler.changeAdultPrice(settings, checkInput);
        if (changed) {
          System.out.println(colorize("\n[CHANGED] Adult / Standard Ticket Price changed to " + settings.formatPrice(settings.getAdultTicket()), Preset.SUCCESS.color));
          mod = true;
        } else System.out.println("\n[NO CHANGE] Adult / Standard Ticket Price remains at " + settings.formatPrice(settings.getAdultTicket()));
      }
    } while (checkInput == 0);

  }

  /**
   * Obtains and updates blockbuster surcharge according based on Staff input
   * 
   * @since 1.2
   */
  // CD: -editBlockbusterSurcharge()
  private void editBlockbusterSurcharge() {

    // Setup
    scanner.nextLine();
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("Enter the new surcharge, or press - to return to the menu\n");
    System.out.println("Current Surcharge: " + settings.formatPrice(settings.getBlockbusterSurcharge()));

    // Get new surcharge and update clone
    double checkInput;
    do {
      System.out.print("New Surcharge: SGD ");
      String input = scanner.nextLine();
      checkInput = checkPriceInput(input, false); // check for character input, - input, 0 input, <0 input
      if (checkInput >= 0) {
        boolean changed = handler.changeBlockbusterSurcharge(settings, checkInput);
        if (changed) {
          System.out.println(colorize("\n[CHANGED] Blockbuster Surcharge changed to " + settings.formatPrice(settings.getBlockbusterSurcharge()), Preset.SUCCESS.color));
          mod = true;
        } else System.out.println("\n[NO CHANGE] Blockbuster Surcharge remains at SGD " + settings.formatPrice(settings.getBlockbusterSurcharge ()));
      }
    } while (checkInput < -1);

  }

  /**
   * Obtains and updates ticket surcharges according based on Staff input
   * 
   * @since 1.2
   */
  private void editTicketSurcharges() {
    
    // Setup
    scanner.nextLine();
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("Enter the new surcharge, or press - to return to the menu\n");
    EnumMap<Booking.TicketType, Double> ticketSurcharges = settings.getTicketSurcharges();

    // Loop through surcharges
    for (var surcharge : ticketSurcharges.entrySet()) {
      System.out.println("- " + surcharge.getKey().toString() + " Surcharge");
      System.out.println("Current Surcharge: " + settings.formatPrice(surcharge.getValue()));

      int checkInput;
      do {
        System.out.print("New Surcharge: SGD ");
        String input = scanner.nextLine();
        checkInput = checkNoCharacters(input); // check for character input
        if (checkInput == 0) {
          double newSurcharge = Double.parseDouble(input);
          if (newSurcharge != surcharge.getValue()) {
            System.out.println(colorize("\n[CHANGED] " + surcharge.getKey().toString() + " Surcharge changed to " + newSurcharge, Preset.SUCCESS.color));
            surcharge.setValue(newSurcharge);
            mod = true;
          } else System.out.println("\n[NO CHANGE] " + surcharge.getKey().toString() + " Surcharge remains at " + surcharge.getValue());
        }
      } while (checkInput < -1);
      System.out.println();
    }

    // Update clone
    handler.changeTicketSurcharges(settings, ticketSurcharges);

  }

  /**
   * Obtains and updates cinema surcharges according based on Staff input
   * 
   * @since 1.2
   */
  private void editCinemaSurcharges() {

    // Setup
    scanner.nextLine();
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("Enter the new surcharge, or press - to return to the menu\n");
    EnumMap<Cinema.ClassType, Double> cinemaSurcharges = settings.getCinemaSurcharges();

    // Loop through surcharges
    for (var surcharge : cinemaSurcharges.entrySet()) {
      System.out.println("- " + surcharge.getKey().toString() + " Class Surcharge");
      System.out.println("Current Surcharge: " + settings.formatPrice(surcharge.getValue()));

      double checkInput;
      do {
        System.out.print("New Surcharge: SGD ");
        String input = scanner.nextLine();
        checkInput = checkPriceInput(input, false); // check for character input and less than 0
        if (checkInput >= 0) {
          if (checkInput != surcharge.getValue()) {
            System.out.println(colorize("\n[CHANGED] " + surcharge.getKey().toString() + " Surcharge changed to " + checkInput, Preset.SUCCESS.color));
            surcharge.setValue(checkInput);
            mod = true;
          } else System.out.println("\n[NO CHANGE] " + surcharge.getKey().toString() + " Surcharge remains at " + surcharge.getValue());
        }
      } while (checkInput < -1);
      System.out.println();
    }

    // Update clone
    handler.changeCinemaSurcharges(settings, cinemaSurcharges);

  }

  // MIN-SELF-NOTE: possible to move all these to handler for more backend processing?
  /**
   * Helper method to check validity of price input
   * 
   * @param input:String - input obained from Staff
   * @param checkZero:boolean - whether it is required to check for zero
   * @return -1 - if Staff does not want to change the price
   * @return -2 - if the price entered is invalid
   * @return price - if the price entered is valid
   * @since 1.1
   */
  private double checkPriceInput(String input, boolean checkZero) {
    if (input.equals("-")) return -1; // Staff does not want to change
    try {
      double price = Double.parseDouble(input);
      if (price <= 0 && checkZero) { // 0 or less
        System.out.println("[ERROR] Please enter a price that is more than SGD 0, or - to keep the current price.");
        return -2;
      } else if (price < 0) { // less than 0
        System.out.println("[ERROR] Please enter a price that is more than or equal to SGD 0, or - to keep the current price.");
        return -2;
      }
      else return price; // valid
    } catch (NumberFormatException e) { // characters other than -
      System.out.println("[ERROR] Please enter integers and decimal point (if needed) only, or - to keep the current price.");
      return -2;
    }
  }

  /**
   * Helper method to check validity of price input
   * 
   * @param input:String - input obtained from Staff
   * @return 0 - if the price entered is valid
   * @return -2 - if the price entered is invalid (incl. non-digits)
   */
  private int checkNoCharacters(String input) {
    if (input.equals("-")) return -1; // Staff does not want to change
    try {
      Double.parseDouble(input);
      return 0;
    } catch (NumberFormatException e) { // characters other than -
      System.out.println("Please enter integers and decimal point (if needed) only, or - to keep the current price.");
      return -2;
    }
  }

  /**
   * Obtains date and adds to the list of public holidays
   * 
   * @since 1.1
   */
  private void addPublicHoliday() {

    // Setup
    scanner.nextLine(); // consume any remaining input in buffer
    System.out.println("---------------------------------------------------------------------------");

    // Get date
    System.out.println("Enter the date of the public holiday:\n-------------------------------------");
    int validDate = 0;
    do {
      System.out.print("Date [Format: DD/MM/YYYY] or enter - to cancel: ");
      String dateInput = scanner.nextLine();

      if (dateInput.equals("-")) break; // if Staff wants to cancel

      validDate = handler.addPublicHoliday(settings, dateInput); // check if date is valid, and add if valid

      if (validDate == 1) {
        System.out.println(colorize(String.format("\n[ADDED] Public holiday %s has been added successfully.", dateInput), Preset.SUCCESS.color));
        mod = true;
      } else if (validDate == -1) System.out.println("[ERROR] Date is in the past. Please enter today's date or a date after today, or enter -  to cancel and return to the menu");
      else System.out.println("[ERROR] Date is invalid. Please enter a valid date, or enter - to cancel and return to the menu.");
    } while (validDate!=1);
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

      dateExist = handler.removePublicHoliday(settings, dateInput); // check if date exists in the settings, if yes, delete
      if (dateExist) {
        System.out.println(colorize(String.format("\n[REMOVED] Public holiday %s has been removed successfully.", dateInput), Preset.SUCCESS.color));
        mod = true;
      }
      else System.out.println("[ERROR] Date is invalid. Please enter a valid date from the list of public holidays, or enter - to cancel and return to the menu.");
    } while (!dateExist);
  }
}