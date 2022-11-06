package control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entities.*;
import entities.Booking.TicketType;
import utils.Helper;
import utils.datasource.Datasource;
import utils.datasource.HolidayDatasource;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static utils.deserializers.LocalDateDeserializer.dateFormatter;

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
  protected Settings currentSettings;
  protected Account currentAccount;

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

  /**
   * Checks whether public holiday date entered is a valid date, and adds the date to the list of public holidays if valid
   *
   * @param settings:Settings
   * @param strHolidayDate:String
   * @return holidayIdx:int
   * @since 1.0
   */
  public int addPublicHoliday(Settings settings, String strHolidayDate) {
    int holidayIdx = -1;
    try {
      LocalDate dateAdd = LocalDate.parse(strHolidayDate, dateFormatter);

      // VALIDATION: Check if holidayDate is in the past
      if (dateAdd.isBefore(LocalDate.now())) return holidayIdx;

      // Add holiday to settings
      settings.addHoliday(dateAdd);
      holidayIdx = settings.getHolidays().size() - 1;

      return holidayIdx;
    } catch (DateTimeParseException e) { // Invalid date
      return holidayIdx;
    }
  }

  /**
   * Checks whether public holiday date entered is in the list of public holidays, and removes if found.
   *
   * @param settings:Settings
   * @param strHolidayDate:String
   * @return isRemoved:boolean
   * @since 1.0
   */
  public boolean removePublicHoliday(Settings settings, String strHolidayDate) {
    try {
      LocalDate dateRemove = LocalDate.parse(strHolidayDate, dateFormatter);

      return settings.removeHoliday(dateRemove);
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  /**
   * Updates current system settings
   *
   * @param settings:Settings
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
    if (this.currentSettings == null) this.currentSettings = this.getSettings();
    Helper.logger("SettingsHandler.getCurrentSystemSettings", "Settings: \n" + this.currentSettings);

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

    EnumMap<Showtime.ShowType, Double> showSurcharges = new EnumMap<Showtime.ShowType, Double>(Showtime.ShowType.class) {{
      put(Showtime.ShowType.Digital, 0.0);
      put(Showtime.ShowType.ThreeDimensional, 5.0);
    }};

    EnumMap<Booking.TicketType, Double> ticketSurcharges = new EnumMap<Booking.TicketType, Double>(Booking.TicketType.class) {{
      put(Booking.TicketType.STUDENT, -5.0);
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
    List<LocalDate> publicHolidays = dsHoliday.getHolidays();

    return new Settings(adultTicketPrice, blockbusterSurcharge, showSurcharges, ticketSurcharges, cinemaSurcharges, publicHolidays);
  }

  public TicketType verifyTicketType(LocalDateTime showDateTime, TicketType ticketType) {
    // Check if PEAK
    DayOfWeek day = showDateTime.getDayOfWeek();
    boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
    boolean isHoliday = this.currentSettings.getHolidays().stream().anyMatch(h -> h.isEqual(showDateTime.toLocalDate()));
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
  public double computeTicketPrice(boolean isBlockbuster, Showtime.ShowType showType, Cinema.ClassType classType, Booking.TicketType ticketType, LocalDateTime showDateTime) {
    Helper.logger("SettingsHandler.computeTicketPrice", "currentPrice:\n" + this.currentSettings.toString());
    double price = this.currentSettings.getAdultTicket();

    // Blockbuster Surcharges
    if (isBlockbuster) price += this.currentSettings.getBlockbusterSurcharge();

    // ShowType Surcharges
    EnumMap<Showtime.ShowType, Double> showSurcharge = this.currentSettings.getShowSurcharges();
    if (showSurcharge.containsKey(showType)) price += showSurcharge.get(showType);

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

  /**
   * Computes the cost of a single ticket
   *
   * @param isBlockbuster:boolean
   * @param showType:ShowType
   * @param classType:ClassType
   * @param ticketType:TicketType
   * @param showDateTime:LocalDateTime
   * @param seatCount:int
   * @return totalCost:double
   */
  //+ computeTotalCost(
  public double computeTotalCost(boolean isBlockbuster, Showtime.ShowType showType, Cinema.ClassType classType, Booking.TicketType ticketType, LocalDateTime showDateTime, int seatCount) {
    // NOTE; this current implementation of computeTotalCost assumes that all tickets booked are of the same type, which is NOT TRUE
    // final double GST_PERCENT = 0.07;

    if (seatCount <= 0) return 0;
    double totalCost = this.computeTicketPrice(isBlockbuster, showType, classType, ticketType, showDateTime) * seatCount;
    // totalCost += totalCost * GST_PERCENT;

    return totalCost;
  }

  /**
   * Retrieves settings from serialized data
   *
   * @return settings:Settings;
   */
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

      // Show Surcharge
      String strShowSurcharges = p.get("showSurcharges").getAsString();
      Type typeShowSurcharges = new TypeToken<EnumMap<Showtime.ShowType, Double>>() {
      }.getType();
      EnumMap<Showtime.ShowType, Double> showSurcharges = Datasource.getGson().fromJson(strShowSurcharges, typeShowSurcharges);

      // Ticket Surcharge
      String strTicketSurcharges = p.get("ticketSurcharges").getAsString();
      Type typeTicketSurcharges = new TypeToken<EnumMap<TicketType, Double>>() {
      }.getType();
      EnumMap<Booking.TicketType, Double> ticketSurcharges = Datasource.getGson().fromJson(strTicketSurcharges, typeTicketSurcharges);

      // Cinema Surcharge
      String strCinemaSurcharges = p.get("cinemaSurcharges").getAsString();
      Type typeCinemaSurcharges = new TypeToken<EnumMap<Cinema.ClassType, Double>>() {
      }.getType();
      EnumMap<Cinema.ClassType, Double> cinemaSurcharges = Datasource.getGson().fromJson(strCinemaSurcharges, typeCinemaSurcharges);

      // Public Holidays
      String strPublicHolidays = p.get("publicHolidays").getAsString();
      Type typePublicHolidays = new TypeToken<ArrayList<LocalDate>>() {
      }.getType();
      ArrayList<LocalDate> publicHolidays = Datasource.getGson().fromJson(strPublicHolidays, typePublicHolidays);

      this.currentSettings = new Settings(adultTicket, blockbusterSurcharge, showSurcharges, ticketSurcharges, cinemaSurcharges, publicHolidays);
    }

    if (settings.size() < 1) return this.currentSettings;


    // Update current price
    this.updateSettings(settings.get(settings.size()));
    Helper.logger("SettingsHandler.getSystemSettings", "Settings: \n" + this.currentSettings);

    return this.currentSettings;
  }

  /**
   * Serialize price data to CSV
   *
   * @return isSaved:boolean
   */
  //# saveSettings():boolean
  protected boolean saveSettings() {
    List<Settings> settings = new ArrayList<Settings>();
    settings.add(this.currentSettings);
    HolidayDatasource.saveHolidays(this.currentSettings.getHolidays());
    return Datasource.serializeData(settings, "settings.csv");
  }

}
