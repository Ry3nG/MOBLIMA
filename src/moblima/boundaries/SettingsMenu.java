package moblima.boundaries;

import moblima.control.handlers.SettingsHandler;
import moblima.entities.Booking;
import moblima.entities.Cinema;
import moblima.entities.Settings;
import moblima.entities.Showtime;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static moblima.utils.Helper.colorizer;
import static moblima.utils.Helper.logger;
import static moblima.utils.deserializers.LocalDateDeserializer.dateFormatter;

/**
 * The type Settings menu.
 */
public class SettingsMenu extends Menu {

  private static SettingsHandler handler;
  private static SettingsMenu instance;
  private Settings settings;

  private SettingsMenu() {
    super();

    handler = new SettingsHandler();
    this.settings = handler.getCurrentSystemSettings();

    // Menu
    this.menuMap = new LinkedHashMap<String, Runnable>() {{
      put("View Current Settings", () -> viewCurrentSettings());
      put("Edit Adult (Standard) Ticket Price", () -> editAdultTicketPrice());
      put("Edit Blockbuster Movie Surcharge", () -> editBlockbusterSurcharge());
      put("Edit Show Surcharges", () -> editShowSurcharges());
      put("Edit Ticket Surcharges", () -> editTicketSurcharges());
      put("Edit Cinema Surcharges", () -> editCinemaSurcharges());
      put("Edit Holidays", () -> editPublicHolidays());
      put("Discard changes", () -> {
        settings = handler.getCurrentSystemSettings();
        System.out.println(colorizer("[REVERTED] Changes discarded", Preset.SUCCESS));
      });
      put("Save changes", () -> {
        handler.updateSettings(settings);
        System.out.println(colorizer("[UPDATED] Settings updated", Preset.SUCCESS));
      });
      put("Return to previous menu", () -> {
        settings = handler.getCurrentSystemSettings();
        System.out.println("\t>>> Returning to previous menu . . .");
      });
    }};
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
// +getInstance() : SettingsMenu
  public static SettingsMenu getInstance() {
    if (instance == null) instance = new SettingsMenu();
    return instance;
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

  /**
   * Gets handler.
   *
   * @return the handler
   */
  public SettingsHandler getHandler() {
    return handler;
  }

  // CD: -viewCurrentSettings()
  private void viewCurrentSettings() {
    System.out.println("---------------------------------------------------------------------------");
    System.out.println(settings);
  }

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
      checkInput = Helper.checkPriceInput(input, true); // check for character input, - input, 0 input, <0 input
      if (checkInput > 0) {
        boolean changed = (settings.getAdultTicket() != checkInput);
        settings.setAdultTicket(checkInput);
        if (changed) {
          System.out.println(colorizer("\n[CHANGED] Adult / Standard Ticket Price changed to " + settings.formatPrice(settings.getAdultTicket()), Preset.SUCCESS));
        } else
          System.out.println("\n[NO CHANGE] Adult / Standard Ticket Price remains at " + settings.formatPrice(settings.getAdultTicket()));
      }
    } while (checkInput == 0);
  }

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
      checkInput = Helper.checkPriceInput(input, false); // check for character input, - input, 0 input, <0 input
      if (checkInput >= 0) {
        boolean changed = (settings.getBlockbusterSurcharge() != checkInput);
        settings.setBlockbusterSurcharge(checkInput);
        if (changed) {
          System.out.println(colorizer("\n[CHANGED] Blockbuster Surcharge changed to " + settings.formatPrice(settings.getBlockbusterSurcharge()), Preset.SUCCESS));
        } else
          System.out.println("\n[NO CHANGE] Blockbuster Surcharge remains at SGD " + settings.formatPrice(settings.getBlockbusterSurcharge()));
      }
    } while (checkInput < -1);
  }

  private EnumMap editSurcharges(
      EnumMap surcharges
  ) {
    // Setup
    scanner.nextLine();
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("Enter the new surcharge, or press - to return to the menu\n");

    // Loop through surcharges
    for (var surchargeSet : surcharges.entrySet()) {
      Map.Entry<Enum, Double> surcharge = (Map.Entry) surchargeSet;

      System.out.println("- " + surcharge.getKey().toString() + " Surcharge");
      System.out.println("Current Surcharge: " + settings.formatPrice(surcharge.getValue()));

      int checkInput;
      do {
        System.out.print("New Surcharge: SGD ");
        String input = scanner.nextLine();
        checkInput = Helper.checkNoCharacters(input); // check for character input
        if (checkInput == 0) {
          double newSurcharge = Double.parseDouble(input);
          if (newSurcharge != surcharge.getValue()) {
            System.out.println(colorizer("\n[CHANGED] " + surcharge.getKey().toString() + " Surcharge changed to " + newSurcharge, Preset.SUCCESS));
            surcharge.setValue(newSurcharge);
          } else
            System.out.println("\n[NO CHANGE] " + surcharge.getKey().toString() + " Surcharge remains at " + surcharge.getValue());
        }
      } while (checkInput < -1);
      System.out.println();
    }
    return surcharges;
  }

  /**
   * Edit show surcharges.
   */
  public void editShowSurcharges() {
    EnumMap<Showtime.ShowType, Double> showSurcharges = settings.getShowSurcharges();
    showSurcharges = this.editSurcharges(showSurcharges);
  }

  /**
   * Edit ticket surcharges.
   */
  public void editTicketSurcharges() {
    EnumMap<Booking.TicketType, Double> ticketSurcharges = settings.getTicketSurcharges();
    ticketSurcharges = this.editSurcharges(ticketSurcharges);
  }

  /**
   * Edit cinema surcharges.
   */
  public void editCinemaSurcharges() {
    EnumMap<Cinema.ClassType, Double> cinemaSurcharges = settings.getCinemaSurcharges();
    cinemaSurcharges = this.editSurcharges(cinemaSurcharges);
  }


  /**
   * Edit public holidays boolean.
   *
   * @return the boolean
   */
  public boolean editPublicHolidays() {
    boolean status = false;
    List<LocalDate> holidays = this.settings.getHolidays();


    while (!status) {
      List<String> proceedOptions = holidays.stream()
          .map(h -> h.format(dateFormatter) + ", " + h.getDayOfWeek().toString())
          .collect(Collectors.toList());
      proceedOptions.add("Add new public holiday");
      proceedOptions.add("Return to previous menu");

      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);

      // Save changes & return OR Return to previous menu
      if (proceedSelection == proceedOptions.size() - 1) {
        System.out.println("\t>>> " + "Returning to previous menu...");
        return status;
      }

      // Add new public holiday
      if (proceedSelection == proceedOptions.size() - 2) {
        this.addPublicHoliday();
      }

      // Update / Remove selected holiday
      else {
        LocalDate selectedHoliday = holidays.get(proceedSelection);
        System.out.println("Selected Holiday: " + selectedHoliday.format(dateFormatter));

        //TODO: Extract as separate function
        List<String> updateOptions = new ArrayList<String>() {
          {
            add("Update holiday");
            add("Remove holiday");
            add("Return to previous menu");
          }
        };

        System.out.println("Update by:");
        this.displayMenuList(updateOptions);
        int selectionIdx = getListSelectionIdx(updateOptions, false);

        // Remove holiday
        if (selectionIdx >= updateOptions.size() - 2) {
          // Remove holiday
          if ((selectionIdx == updateOptions.size() - 2)) {
            holidays.remove(proceedSelection);
            System.out.println(colorizer("[SUCCESS] Holiday removed", Preset.SUCCESS));
          }

          // Return to previous menu
          System.out.println("\t>>> " + "Returning to previous menu...");
          continue;
        }

        // Update holiday
        else if (selectionIdx == 0) {
          LocalDate prevStatus = selectedHoliday;
          System.out.println("[CURRENT] Holiday: " + prevStatus.format(dateFormatter));

          //TODO: Extract as separate function
          scanner = new Scanner(System.in).useDelimiter("\n");
          System.out.print("Set to (dd-MM-yyyy):");
          String date = scanner.next().trim();
          if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
            LocalDate holidayDate = LocalDate.parse(date, dateFormatter);

            if (holidays.contains(holidayDate)) {
              System.out.println("[NO CHANGE] Given date is already marked as an existing Public Holiday");
            } else {
              LocalDate curStatus = holidayDate;

              holidays.set(proceedSelection, curStatus);
              logger("SettingsMenu.editPublicHolidays", "Holidays: \n" + holidays);

              if (prevStatus.isEqual(curStatus)) {
                System.out.println("[NO CHANGE] Datetime: " + prevStatus.format(dateFormatter));
              } else {
                System.out.println("[UPDATED] Datetime: " + prevStatus.format(dateFormatter) + " -> " + curStatus.format(dateFormatter));
              }
            }
          } else {
            System.out.println("Invalid input, expected format (dd-MM-yyyy)");
          }
        }
      }
    }


    return status;
  }

  private void addPublicHoliday() {

    // Setup
    scanner.nextLine(); // consume any remaining input in buffer
    System.out.println("---------------------------------------------------------------------------");

    // Get date
    System.out.println("Enter the date of the public holiday:\n-------------------------------------");
    int validDate = 0;
    do {
      System.out.print("Date (dd-MM-yyyy) or enter - to cancel: ");
      String dateInput = scanner.nextLine();

      if (dateInput.equals("-")) break; // if Staff wants to cancel

      validDate = handler.addPublicHoliday(settings, dateInput); // check if date is valid, and add if valid

      if (validDate >= 0) {
        System.out.println(colorizer(String.format("\n[ADDED] Public holiday %s has been added successfully.", dateInput), Preset.SUCCESS));
      } else if (validDate == -1)
        System.out.println("[ERROR] Date is in the past. Please enter today's date or a date after today, or enter -  to cancel and return to the menu");
      else
        System.out.println("[ERROR] Date is invalid. Please enter a valid date, or enter - to cancel and return to the menu.");
    } while (validDate != 1);
  }

}
