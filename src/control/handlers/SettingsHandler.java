package control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import common.Datasource;
import entity.Account;
import entity.Booking;
import entity.Booking.TicketType;
import entity.Cinema;
import entity.Settings;
import moblima.control.HolidayDatasource;
import utils.Helper;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

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
  private Settings currentSettings;
  private Account currentAccount;

  /**
   * Constructor for SettingsHandler
   * Calls loadData() to load saved settings into settings variable
   */
  public SettingsHandler() {
    this.currentSettings = this.getCurrentSystemSettings();
  }

  //+ checkIfIsAuthenticated():boolean
  public boolean checkIfIsAuthenticated() {
    return (this.currentAccount != null);
  }

  //+ getCurrentAccount():Account
  public Account getCurrentAccount() {
    return this.currentAccount;
  }

  //+ setIsAuthenticated(account:Account):void
  public void setIsAuthenticated(Account account) {
    this.currentAccount = account;
  }

  public boolean changeAdultPrice(Settings settings, double newPrice) {
    if (settings.getAdultTicket() == newPrice) return false;
    settings.setAdultTicket(newPrice);
    return true;
  }

  public boolean changeBlockbusterSurcharge(Settings settings, double newSurcharge) {
    if (settings.getBlockbusterSurcharge() == newSurcharge) return false;
    settings.setBlockbusterSurcharge(newSurcharge);
    return true;
  }

  public void changeTicketSurcharges(Settings settings, EnumMap<Booking.TicketType, Double> newTicketSurcharges) {
    settings.setTicketSurcharges(newTicketSurcharges);
  }

  public void changeCinemaSurcharges(Settings settings, EnumMap<Cinema.ClassType, Double> newCinemaSurcharges) {
    settings.setCinemaSurcharges(newCinemaSurcharges);
  }

  /**
   * Checks whether public holiday date entered is a valid date, and adds the date to the list of public holidays if valid
   *
   * @param clone:SystemSettings - clone of SystemSettings being used in Menu
   * @param date:String          - date to be added
   * @return 1 - date is valid
   * @return 0 - date is invalid
   * @since 1.0
   */
  public int addPublicHoliday(Settings clone, String date) {
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
  public boolean removePublicHoliday(Settings clone, String date) {

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
  public void updateSettings(Settings settings) {
    // Replace current price
    this.currentSettings = settings;
    Helper.logger("SettingsHandler.updateSystemSettings", "Settings: \n" + this.currentSettings);

    // Serialize data
    this.saveSettings();
  }

  /**
   * Returns current settings (CLONE)
   *
   * @return systemSettings:SystemSettings | null
   */
  // + getCurrentPrice():Price
  public Settings getCurrentSystemSettings() {
    if (this.currentSettings == null) this.getSettings();
    return new Settings(this.currentSettings);
  }

  /**
   * Generates default pricing scheme
   *
   * @return defaultPrice:Price
   */
  //- getDefaultPricingScheme():Price
  private Settings getDefaultSettings() {
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

    // Public Holidays
    HolidayDatasource dsHoliday = new HolidayDatasource();
    List<LocalDate> publicHolidays = dsHoliday.fetchHolidays();

    return new Settings(adultTicketPrice, blockbusterSurcharge, ticketSurcharges, cinemaSurcharges, publicHolidays);
  }

  public TicketType verifyTicketType(LocalDateTime showDateTime, TicketType ticketType) {
    // Check if PEAK
    DayOfWeek day = showDateTime.getDayOfWeek();
    boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
    boolean isHoliday = this.currentSettings.getHolidays().stream()
        .anyMatch(h -> h.isEqual(showDateTime.toLocalDate()));
    if (isHoliday || isWeekend) ticketType = TicketType.PEAK;

    return ticketType;
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
    Helper.logger("SettingsHandler.computeTicketPrice", "currentPrice:\n" + this.currentSettings.toString());
    double price = this.currentSettings.getAdultTicket();

    // Blockbuster Surcharges
    if (isBlockbuster) price += this.currentSettings.getBlockbusterSurcharge();

    // Cinema Surcharges
    EnumMap<Cinema.ClassType, Double> cinemaSurcharge = this.currentSettings.getCinemaSurcharges();
    if (cinemaSurcharge.containsKey(classType)) price += cinemaSurcharge.get(classType);

    // Check if PEAK
    ticketType = verifyTicketType(showDateTime, ticketType);

    // Ticket Surcharges
    EnumMap<Booking.TicketType, Double> ticketSurcharges = this.currentSettings.getTicketSurcharges();
    if (ticketSurcharges.containsKey(ticketType)) price += ticketSurcharges.get(ticketType);

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
  public Settings getSettings() {
    List<Settings> settings = new ArrayList<Settings>();

    // Intialize with default pricing
    this.currentSettings = this.getDefaultSettings();

    //Source from serialized datasource
    String fileName = "settings.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("SettingsHandler.getSystemSettingss", "Null and void filename provided, no data retrieved.");
      return this.currentSettings;
    }

    JsonArray settingsList = Datasource.readArrayFromCsv(fileName);
    if (settingsList == null) {
      Helper.logger("SettingsHandler.getSystemSettingss", "No serialized data available");
      return this.currentSettings;
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
      Type typePublicHolidays = new TypeToken<ArrayList<LocalDate>>() {
      }.getType();
      ArrayList<LocalDate> publicHolidays = Datasource.getGson().fromJson(strPublicHolidays, typePublicHolidays);

      this.currentSettings = new Settings(
          adultTicket,
          blockbusterSurcharge,
          ticketSurcharges,
          cinemaSurcharges,
          publicHolidays
      );
    }

    if (settings.size() < 1) return this.currentSettings;


    // Update current price
    this.updateSettings(settings.get(settings.size()));
    Helper.logger("SettingsHandler.getSystemSettings", "Settings: \n" + this.currentSettings);

    return this.currentSettings;
  }

  /**
   * Serialize price data to CSV
   */
  //# saveSettings():boolean
  protected boolean saveSettings() {
    List<Settings> settings = new ArrayList<Settings>();
    settings.add(this.currentSettings);
    return Datasource.serializeData(settings, "settings.csv");
  }

}
