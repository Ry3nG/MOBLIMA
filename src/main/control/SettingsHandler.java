package main.control;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import main.entity.SystemSettings;

/**
 * Settings Handler
 * 
 * @author SS11 Group 1
 * @version 1.0
 * @since 21 October 2022
 */
public class SettingsHandler {

  /**
   * Static variable to check if an instance of SettingsHandler has already been initialised
   */
  private static SettingsHandler _settingsHandler = null;
  /**
   * Static variable to store system settings for operations
   */
  private static SystemSettings settings;

  /**
   * Constructor for SettingsHandler
   * 
   * Calls loadData() to load saved settings into settings variable
   */
  private SettingsHandler() {
    loadData();
  }

  /**
   * For the Singleton pattern implementation
   * 
   * @return an instance of SettingsHandler
   * @since 1.0
   */
  public static SettingsHandler getInstance() {
    if (_settingsHandler == null) _settingsHandler = new SettingsHandler();
    return _settingsHandler;
  }

  /**
   * Getter for settings
   * 
   * @return a SystemSettings object containing all system settings
   * @since 1.0
   */
  public SystemSettings getSettings() {
    return settings;
  }

  /**
   * Create new SystemSettings if settings.data cannot be found, or class SystemSettings has been modified
   * 
   * @param input - new settings
   * @since 1.0
   */
  public void createNew(LinkedHashMap<String,Double> input) {
    settings = new SystemSettings(
      input.get("Adult Ticket"),input.get("Child Ticket"),input.get("Senior Ticket"),
      input.get("Premium Class Surcharge"),input.get("Special Movies Surcharge"),input.get("Weekend / PH Surcharge"));

    saveData();
  }

  /**
   * Change ticket prices based on Staff input
   * 
   * @param prices - new prices
   * @since 1.0
   */
  public void changeTicketPrices(LinkedHashMap<String,Double> prices) {
    settings.setAdultTicket(prices.get("Adult"));
    settings.setChildTicket(prices.get("Child"));
    settings.setSeniorTicket(prices.get("Senior"));

    saveData();
  }

  /**
   * Change surcharges based on Staff input
   * 
   * @param surcharges - new surcharges
   * @since 1.0
   */
  public void changeSurcharges(LinkedHashMap<String,Double> surcharges) {
    settings.setCinemaSurchage(surcharges.get("Premium Class"));
    settings.setMovieSurcharge(surcharges.get("Special Movies"));
    settings.setWeekendSurcharge(surcharges.get("Weekend / PH"));

    saveData();
  }

  /**
   * Checks whether public holiday date entered is a valid date, and adds the date to the list of public holidays if valid
   * 
   * @param date - date to be added
   * @return whether date is valid
   * @since 1.0
   */
  public boolean addPublicHoliday(String date) {
    try {
      // Check if valid date
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      LocalDate.parse(date, formatter);

      // Tokeniser
      StringTokenizer tokens = new StringTokenizer(date, "/");
      String dateAdd = "";
      while (tokens.hasMoreTokens()) dateAdd = tokens.nextToken() + dateAdd;

      // Add date
      settings.addPublicHoliday(dateAdd);
      saveData();

      return true;
    } catch (DateTimeParseException e) { // Invalid date
      return false;
    }
  }

  /**
   * Checks whether public holiday date entered is in the list of public holidays, and removes if found.
   * 
   * @param date - date to be removed
   * @return whether date is in the list of public holidays
   * @since 1.0
   */
  public boolean removePublicHoliday(String date) {
    // Tokenising
    StringTokenizer tokens = new StringTokenizer(date, "/");
    String dateRemove = "";
    while (tokens.hasMoreTokens()) dateRemove = tokens.nextToken() + dateRemove;

    // Remove date
    boolean dateExist = settings.removePublicHoliday(dateRemove);
    if (dateExist) saveData();
    return dateExist;
  }

  /**
   * Load serialised settings from data file and store in settings variable
   * 
   * @since 1.0
   */
  private void loadData() {
    try (
      // Creating the streams for loading data
      FileInputStream fis = new FileInputStream("data/settings.data");
      ObjectInputStream ois = new ObjectInputStream(fis);
    ) {
      // Load data
      settings = (SystemSettings)ois.readObject();
      ois.close();
      fis.close();
    } catch (Exception e) {
      // Failed
      // e.printStackTrace();
      settings = null;
    }
  }

  /**
   * Serialise settings and save into data file
   * 
   * @since 1.0
   */
  private void saveData() {
    try (
      // Creating the streams for saving data
      FileOutputStream fos = new FileOutputStream("data/settings.data");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
    ) {
      // Save data
      oos.writeObject(settings);
      oos.flush();
      oos.close();
      fos.close();
    } catch (Exception e) {
      // Failed
      // e.printStackTrace();
    }
  }
  
}
