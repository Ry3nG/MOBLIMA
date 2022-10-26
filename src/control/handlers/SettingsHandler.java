package control.handlers;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import entity.Booking;
import entity.Cinema;
import entity.SystemSettings;
import entity.Booking.TicketType;
import moblima.control.Datasource;
import utils.Helper;

/**
 * Settings Handler
 * 
 * @author SS11 Group 1
 * @version 1.0
 * @since 21 October 2022
 */
public class SettingsHandler {

  /**
   * Static variable to store system settings for operations
   */
  private SystemSettings currentSystemSettings;

  /**
   * Constructor for SettingsHandler
   * 
   * Calls loadData() to load saved settings into settings variable
   */
  public SettingsHandler() {
    this.getCurrentSystemSettings();
  }

  public boolean changeAdultPrice(SystemSettings clone, double newPrice) {
    if (clone.getAdultTicket() == newPrice) return false;
    clone.setAdultTicket(newPrice);
    return true;
  }

  public boolean changeBlockbusterSurcharge(SystemSettings clone, double newSurcharge) {
    if (clone.getBlockbusterSurcharge() == newSurcharge) return false;
    clone.setBlockbusterSurcharge(newSurcharge);
    return true;
  }

  public void changeTicketSurcharges(SystemSettings clone, EnumMap<Booking.TicketType, Double> newTicketSurcharges) {
    clone.setTicketSurcharges(newTicketSurcharges);
  }

  public void changeCinemaSurcharges(SystemSettings clone, EnumMap<Cinema.ClassType, Double> newCinemaSurcharges) {
    clone.setCinemaSurcharges(newCinemaSurcharges);
  }
  
  /**
   * Checks whether public holiday date entered is a valid date, and adds the date to the list of public holidays if valid
   * 
   * @param clone:SystemSettings - clone of SystemSettings being used in Menu
   * @param date:String - date to be added
   * @return 1 - date is valid
   * @return 0 - date is invalid
   * @since 1.0
   */
  public int addPublicHoliday(SystemSettings clone, String date) {
    try {
      // Check if valid date
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      LocalDate dateAdd = LocalDate.parse(date, formatter);

      // tentative - because there are some movies which might be showing on a date that has passed
      if (dateAdd.isBefore(LocalDate.now())) return -1;

      // Add date
      clone.addHoliday(dateAdd);
      return 1;
    } catch (DateTimeParseException e) { // Invalid date
      return 0;
    }
  }

  /**
   * Checks whether public holiday date entered is in the list of public holidays, and removes if found.
   * 
   * @param date - date to be removed
   * @return whether date is in the list of public holidays
   * @since 1.0
   */
  public boolean removePublicHoliday(SystemSettings clone, String date) {

    try {
      // Check if valid date
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      LocalDate dateRemove = LocalDate.parse(date, formatter);

      // Remove date
      boolean dateExist = clone.removeHoliday(dateRemove);
      return dateExist;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  /**
   * Updates current system settings
   *
   * @param settings:SystemSettings
   */
  //+updatePrice(settings : SystemSettings) : void
  public void updateSystemSettings(SystemSettings settings) {
    // Replace current price
    this.currentSystemSettings = settings;
    Helper.logger("SettingsHandler.updateSystemSettings", "Settings: \n" + this.currentSystemSettings);

    // Serialize data
    this.saveSystemSettingss();
  }

  /**
   * Returns current settings (CLONE)
   *
   * @return systemSettings:SystemSettings | null
   */
  // + getCurrentPrice():Price
  public SystemSettings getCurrentSystemSettings() {
    this.getSystemSettings();
    return new SystemSettings(this.currentSystemSettings);
  }

  /**
   * Generates default pricing scheme
   *
   * @return defaultPrice:Price
   */
  //- getDefaultPricingScheme():Price
  private SystemSettings getDefaultSettings() {
    double adultTicketPrice = 10;
    double blockbusterSurcharge = 8;

    EnumMap<Booking.TicketType, Double> ticketSurcharges = new EnumMap<Booking.TicketType, Double>(Booking.TicketType.class) {{
      put(Booking.TicketType.CHILD, -5.0);
      put(Booking.TicketType.SENIOR, -5.0);
      put(Booking.TicketType.PEAK, 5.0);
      put(Booking.TicketType.NON_PEAK, 0.0);
    }};

    EnumMap<Cinema.ClassType, Double> cinemaSurcharges = new EnumMap<Cinema.ClassType, Double>(Cinema.ClassType.class) {{
      put(Cinema.ClassType.Normal, 0.0);
      put(Cinema.ClassType.Premium, 8.0);
    }};

    ArrayList<LocalDate> publicHolidays = new ArrayList<LocalDate>();

    return new SystemSettings(adultTicketPrice, blockbusterSurcharge, ticketSurcharges, cinemaSurcharges, publicHolidays);
  }

  /**
   * Compute single ticket price with the associated surcharges
   *
   * @param isBlockbuster:boolean
   * @param classType:ClassType
   * @param ticketType:TicketType
   * @return
   */
  //+ computeTicketPrice(isBlockbuster:boolean, classType:ClassType, ticketType:TicketType):double
  public double computeTicketPrice(boolean isBlockbuster, Cinema.ClassType classType, Booking.TicketType ticketType, LocalDateTime showDateTime) {
    Helper.logger("SettingsHandler.computeTicketPrice", "currentPrice:\n" + this.currentSystemSettings.toString());
    double price = this.currentSystemSettings.getAdultTicket();

    // Blockbuster Surcharges
    if (isBlockbuster) price += this.currentSystemSettings.getBlockbusterSurcharge();

    // Cinema Surcharges
    EnumMap<Cinema.ClassType, Double> cinemaSurcharge = this.currentSystemSettings.getCinemaSurcharges();
    if (cinemaSurcharge.containsKey(classType)) price += cinemaSurcharge.get(classType);

    boolean peakTime = false;
    // Check Weekend
    DayOfWeek day = showDateTime.getDayOfWeek();
    if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
      // ticketType = TicketType.PEAK; // do not overwrite the ticket type of student or senior, suggest to take it out of tickettype and put it as individual setting (ref blockbuster surcharge)
      peakTime = true;
    }
    
    // Check Holiday
    for (LocalDate holiday : this.currentSystemSettings.getHolidays()) {
      // LocalDate holidayDate = LocalDate.parse(holiday, LocalDateDeserializer.dateFormatter);
      if (holiday.isEqual(showDateTime.toLocalDate())) {
        // ticketType = TicketType.PEAK;
        peakTime = true;
      }
    }

    // Ticket Surcharges
    EnumMap<Booking.TicketType, Double> ticketSurcharges = this.currentSystemSettings.getTicketSurcharges();
    if (ticketSurcharges.containsKey(ticketType)) price += ticketSurcharges.get(ticketType);
    if (peakTime) if (ticketSurcharges.containsKey(TicketType.PEAK)) price += ticketSurcharges.get(TicketType.PEAK);  // peak surcharge

    return price;
  }

  //+ computeTotalCost(
  public double computeTotalCost(boolean isBlockbuster, Cinema.ClassType classType, Booking.TicketType ticketType, LocalDateTime showDateTime, int seatCount) {
    // NOTE; this current implementation of computeTotalCost assumes that all tickets booked are of the same type, which is NOT TRUE
    // final double GST_PERCENT = 0.07;

    if (seatCount <= 0) return 0;
    double totalCost = this.computeTicketPrice(isBlockbuster, classType, ticketType, showDateTime) * seatCount;
    // totalCost += totalCost * GST_PERCENT;

    return totalCost;
  }

  //+ getPrices():Price
  public SystemSettings getSystemSettings() {
    List<SystemSettings> settings = new ArrayList<SystemSettings>();

    // Intialize with default pricing
    this.currentSystemSettings = this.getDefaultSettings();

    //Source from serialized datasource
    String fileName = "settings.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("SettingsHandler.getSystemSettingss", "Null and void filename provided, no data retrieved.");
      return this.currentSystemSettings;
    }

    JsonArray settingsList = Datasource.readArrayFromCsv(fileName);
    if (settingsList == null) {
      Helper.logger("SettingsHandler.getSystemSettingss", "No serialized data available");
      return this.currentSystemSettings;
    }

    for (JsonElement setting : settingsList) {
      JsonObject p = setting.getAsJsonObject();

      double adultTicket = p.get("adultTicket").getAsDouble();
      double blockbusterSurcharge = p.get("blockbusterSurcharge").getAsDouble();

      // Ticket Surcharge
      String strTicketSurcharges = p.get("ticketSurcharges").getAsString();
      Type typeTicketSurcharges = new TypeToken<EnumMap<Booking.TicketType, Double>>() {
      }.getType();
      EnumMap<Booking.TicketType, Double> ticketSurcharges = Datasource.getGson().fromJson(strTicketSurcharges, typeTicketSurcharges);

      // Cinema Surcharge
      String strCinemaSurcharges = p.get("cinemaSurcharges").getAsString();
      Type typeCinemaSurcharges = new TypeToken<EnumMap<Cinema.ClassType, Double>>() {
      }.getType();
      EnumMap<Cinema.ClassType, Double> cinemaSurcharges = Datasource.getGson().fromJson(strCinemaSurcharges, typeCinemaSurcharges);

      String strPublicHolidays = p.get("publicHolidays").getAsString();
      Type typePublicHolidays = new TypeToken<ArrayList<LocalDate>>(){}.getType();
      ArrayList<LocalDate> publicHolidays = Datasource.getGson().fromJson(strPublicHolidays, typePublicHolidays);

      this.currentSystemSettings = new SystemSettings(
        adultTicket,
        blockbusterSurcharge,
        ticketSurcharges,
        cinemaSurcharges,
        publicHolidays
      );
    }

    if (settings.size() < 1) return this.currentSystemSettings;

    // Update current price
    this.updateSystemSettings(settings.get(0));

    return this.currentSystemSettings;
  }

  /**
   * Serialize price data to CSV
   */
  //# saveSystemSettingss():boolean
  protected boolean saveSystemSettingss() {
    List<SystemSettings> settings = new ArrayList<SystemSettings>();
    settings.add(this.currentSystemSettings);
    return Datasource.serializeData(settings, "settings.csv");
  }
  
}
