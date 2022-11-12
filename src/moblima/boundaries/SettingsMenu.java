package moblima.boundaries;

import moblima.control.handlers.SettingsHandler;
import moblima.entities.Booking;
import moblima.entities.Cinema;
import moblima.entities.Settings;
import moblima.entities.Showtime;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static moblima.utils.Helper.*;
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
      put("Edit Ranked Types", () -> editRankedTypes());
      put("Edit Holidays", () -> editPublicHolidays());
      put("Discard changes", () -> {
        settings = handler.getCurrentSystemSettings();
        colorPrint("Changes discarded", Preset.WARNING);
      });
      put("Save changes", () -> {
        handler.updateSettings(settings);
        colorPrint("Settings updated", Preset.SUCCESS);
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
    double prevStatus = settings.getAdultTicket();
    colorPrint("Adult ticket price: " + prevStatus, Preset.CURRENT);

    double price = this.setDouble("Set to: ");
    settings.setAdultTicket(price);

    double curStatus = settings.getAdultTicket();
    this.printChanges("Adult ticket price: ", (prevStatus == curStatus), Double.toString(prevStatus), Double.toString(curStatus));
  }

  // CD: -editBlockbusterSurcharge()
  private void editBlockbusterSurcharge() {
    double prevStatus = settings.getBlockbusterSurcharge();
    colorPrint("Blockbuster surcharge: " + prevStatus, Preset.CURRENT);

    double price = this.setDouble("Set to: ");
    settings.setBlockbusterSurcharge(price);

    double curStatus = settings.getBlockbusterSurcharge();
    this.printChanges("Blockbuster surcharge: ", (prevStatus == curStatus), Double.toString(prevStatus), Double.toString(curStatus));
  }

  private EnumMap editSurcharges(
      EnumMap surcharges
  ) {
    // Loop through surcharges
    for (var surchargeSet : surcharges.entrySet()) {
      Map.Entry<Enum, Double> surcharge = (Map.Entry) surchargeSet;

      Enum key = surcharge.getKey();
      Double val = surcharge.getValue();

      double prevStatus = val;
      colorPrint( key + " surcharge: " + prevStatus, Preset.CURRENT);

      double price = this.setDouble("Set to: ");
      surcharge.setValue(price);

      double curStatus = surcharge.getValue();
      this.printChanges(key + " surcharge: ", (prevStatus == curStatus), Double.toString(prevStatus), Double.toString(curStatus));
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
   * Edit ranked types.
   */
  public void editRankedTypes() {
    EnumMap<Settings.RankedType, Boolean> rankedTypes = settings.getRankedTypes();

    // Setup
    scanner.nextLine();
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("Enter the new ranked types, or press - to return to the menu\n");

    // Loop through surcharges
    for (var surchargeSet : rankedTypes.entrySet()) {
      Map.Entry<Enum, Boolean> surcharge = (Map.Entry) surchargeSet;

      boolean prevValue = surcharge.getValue();

      //TODO: tweak this
      System.out.println("- " + surcharge.getKey().toString());
      colorPrint("Current Visibility: " + prevValue, Preset.HIGHLIGHT);

      List<String> updateOptions = Arrays.asList("True", "False");
      int selectionIdx = -1;
      while (selectionIdx < 0) {
        System.out.println("Set to: ");
        this.displayMenuList(updateOptions);
        selectionIdx = getListSelectionIdx(updateOptions, false);

        if (selectionIdx >= 0) {
          boolean curValue = selectionIdx == 0;
          surcharge.setValue(curValue);
          this.printChanges(surcharge.getKey().toString() + ": ", (prevValue == curValue), Boolean.toString(prevValue), Boolean.toString(curValue));
        }
      }
      System.out.println();
    }
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
            colorPrint("Holiday removed", Preset.SUCCESS);
          }

          // Return to previous menu
          System.out.println("\t>>> " + "Returning to previous menu...");
          continue;
        }

        // Update holiday
        else if (selectionIdx == 0) {
          LocalDate prevStatus = selectedHoliday;
          colorPrint("Holiday: " + prevStatus.format(dateFormatter), Preset.CURRENT);

          //TODO: Extract as separate function
          scanner = new Scanner(System.in).useDelimiter("\n");
          System.out.print("Set to (dd-MM-yyyy):");
          String date = scanner.next().trim();
          if (date.matches("^\\d{2}-\\d{2}-\\d{4}")) {
            LocalDate holidayDate = LocalDate.parse(date, dateFormatter);

            if (holidays.contains(holidayDate)) {
              colorPrint("Given date is already marked as an existing Public Holiday", Preset.WARNING);
            } else {
              LocalDate curStatus = holidayDate;

              holidays.set(proceedSelection, curStatus);
              logger("SettingsMenu.editPublicHolidays", "Holidays: \n" + holidays);

              this.printChanges("Datetime: ", (prevStatus.isEqual(curStatus)), prevStatus.format(dateFormatter), curStatus.format(dateFormatter));
            }
          } else {
            colorPrint("Invalid input, expected format (dd-MM-yyyy)", Preset.ERROR);
          }
        }
      }
    }


    return status;
  }

  private void addPublicHoliday() {
    List<LocalDate> holidays = settings.getHolidays();

    boolean status = false;
    while (!status) {
      LocalDate holiday = this.setDate("Add Holiday (dd-MM-yyyy): ", true);

      if (holidays.contains(holiday)) {
        colorPrint("Date is already marked as a holiday", Preset.ERROR);
        continue;
      }

      settings.addHoliday(holiday);
      colorPrint("Date is marked as a holiday", Preset.SUCCESS);
    }
  }

}
