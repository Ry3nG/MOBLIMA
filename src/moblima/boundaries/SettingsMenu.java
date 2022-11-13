package moblima.boundaries;

import moblima.control.handlers.SettingsHandler;
import moblima.entities.Booking;
import moblima.entities.Cinema;
import moblima.entities.Settings;
import moblima.entities.Showtime;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static moblima.utils.Helper.Preset;
import static moblima.utils.Helper.colorPrint;
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
    this.settings = handler.getCurrentSettings();

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
        settings = handler.getCurrentSettings();
        colorPrint("Changes discarded", Preset.WARNING);
      });
      put("Save changes", () -> {
        handler.updateSettings(settings);
        colorPrint("Settings updated", Preset.SUCCESS);
      });
      put("Return to previous menu", () -> {
        settings = handler.getCurrentSettings();
        colorPrint("Any unsaved changes will be discarded", Preset.WARNING);
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

  private EnumMap editSurcharges(EnumMap surcharges) {
    // Loop through surcharges
    for (var surchargeSet : surcharges.entrySet()) {
      Map.Entry<Enum, Double> surcharge = (Map.Entry) surchargeSet;

      Enum key = surcharge.getKey();
      Double val = surcharge.getValue();

      double prevStatus = val;
      colorPrint(key + " surcharge: " + prevStatus, Preset.CURRENT);

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
    settings.setShowSurcharges(showSurcharges);
  }

  /**
   * Edit ticket surcharges.
   */
  public void editTicketSurcharges() {
    EnumMap<Booking.TicketType, Double> ticketSurcharges = settings.getTicketSurcharges();
    ticketSurcharges = this.editSurcharges(ticketSurcharges);
    settings.setTicketSurcharges(ticketSurcharges);
  }

  /**
   * Edit cinema surcharges.
   */
  public void editCinemaSurcharges() {
    EnumMap<Cinema.ClassType, Double> cinemaSurcharges = settings.getCinemaSurcharges();
    cinemaSurcharges = this.editSurcharges(cinemaSurcharges);
    settings.setCinemaSurcharges(cinemaSurcharges);
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
      colorPrint("Display " + surcharge.getKey().toString() + ": " + prevValue, Preset.CURRENT);

      List<String> updateOptions = Arrays.asList("True", "False");
      int selectionIdx = -1;
      while (selectionIdx < 0) {
        System.out.println("Set to: ");
        this.displayMenuList(updateOptions);
        selectionIdx = getListSelectionIdx(updateOptions, false);

        if (selectionIdx >= 0) {
          boolean curValue = selectionIdx == 0;
          surcharge.setValue(curValue);
          this.printChanges("Display " + surcharge.getKey().toString() + ": ", (prevValue == curValue), Boolean.toString(prevValue), Boolean.toString(curValue));
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
  public List<LocalDate> editPublicHolidays() {
    List<LocalDate> holidays = this.settings.getHolidays();
    boolean status = false;

    while (!status) {
      // Prompt selection
      holidays = this.settings.getHolidays();
      List<String> proceedOptions = holidays.stream().map(h -> h.format(dateFormatter) + ", " + h.getDayOfWeek().toString()).collect(Collectors.toList());
      proceedOptions.add("Add new public holiday");
      proceedOptions.add("Return to previous menu");

      System.out.println("Next steps:");
      this.displayMenuList(proceedOptions);
      int proceedSelection = getListSelectionIdx(proceedOptions, false);

      // Return to previous menu
      if (proceedSelection == proceedOptions.size() - 1) {
        System.out.println("\t>>> " + "Returning to previous menu...");
        status = true;
        break;
      }

      // Add new public holiday
      else if (proceedSelection == proceedOptions.size() - 2) {
        holidays = this.addPublicHoliday();
        continue;
      }

      // Selected Holiday: Update or Remove
      LocalDate selectedHoliday = holidays.get(proceedSelection);
      System.out.println("Selected Holiday: " + selectedHoliday.format(dateFormatter));
      List<String> updateOptions = new ArrayList<String>() {
        {
          add("Update holiday");
          add("Remove holiday");
          add("Return to previous menu");
        }
      };

      System.out.println("Update by:");
      this.displayMenuList(updateOptions);
      int updateSelection = getListSelectionIdx(updateOptions, false);
      int updateExit = updateOptions.size() - 1;

      while (updateSelection != updateExit) {
        // Remove holiday || Return to previous menu
        if (updateSelection >= updateExit - 1) {
          // Remove holiday
          if (updateSelection == updateExit - 1) {
            holidays.remove(selectedHoliday);
            colorPrint("Holiday removed", Preset.SUCCESS);
          }

          // Return to previous menu
          System.out.println("\t>>> " + "Returning to previous menu...");
          break;
        }

        // Update holiday
        LocalDate prevStatus = selectedHoliday;
        colorPrint("Holiday: " + prevStatus.format(dateFormatter), Preset.CURRENT);

        LocalDate curStatus = this.setDate("Set to (dd-MM-yyyy):", true);
        while (holidays.contains(curStatus)) {
          colorPrint("Given date is already marked as an existing Public Holiday", Preset.WARNING);
          curStatus = this.setDate("Set to (dd-MM-yyyy):", true);
        }

        /// Replace holiday
        holidays.set(proceedSelection, selectedHoliday);
        this.printChanges("Datetime: ", (prevStatus.isEqual(curStatus)), prevStatus.format(dateFormatter), curStatus.format(dateFormatter));
      }
    }

    settings.setHolidays(holidays);
    return holidays;
  }

  private List<LocalDate> addPublicHoliday() {
    List<LocalDate> holidays = settings.getHolidays();

    int holidayCount = holidays.size();
    while (holidays.size() == holidayCount) {
      LocalDate holiday = this.setDate("Add Holiday (dd-MM-yyyy): ", true);

      //VALIDATION: Check if holiday is already a public holiday
      if (holidays.contains(holiday)) {
        colorPrint("Date is already marked as a holiday", Preset.ERROR);
        continue;
      }

      // Append to holidays
      holidays.add(holiday);
      colorPrint("Date is marked as a holiday", Preset.SUCCESS);
    }
    return holidays;
  }
}
