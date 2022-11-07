package moblima.control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import moblima.entities.*;
import moblima.entities.Booking.TicketType;
import moblima.utils.Helper;
import moblima.utils.datasource.Datasource;
import moblima.utils.datasource.HolidayDatasource;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static moblima.utils.deserializers.LocalDateDeserializer.dateFormatter;

/**
 * The type Settings handler.
 */
public class SettingsHandler {

  /**
   * The Current settings.
   */
  protected Settings currentSettings;
  /**
   * The Current account.
   */
  protected Account currentAccount;

  /**
   * Instantiates a new Settings handler.
   */
  public SettingsHandler() {
    this.currentSettings = this.getCurrentSystemSettings();
  }

  /**
   * Check if is authenticated boolean.
   *
   * @return the boolean
   */
//+ checkIfIsAuthenticated():boolean
  public boolean checkIfIsAuthenticated() {
    return (this.currentAccount != null);
  }

  /**
   * Gets current account.
   *
   * @return the current account
   */
//+ getCurrentAccount():Account
  public Account getCurrentAccount() {
    return this.currentAccount;
  }

  /**
   * Sets is authenticated.
   *
   * @param account the account
   */
//+ setIsAuthenticated(account:Account):void
  public void setIsAuthenticated(Account account) {
    this.currentAccount = account;
  }

  /**
   * Add public holiday int.
   *
   * @param settings       the settings
   * @param strHolidayDate the str holiday date
   * @return the int
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
   * Remove public holiday boolean.
   *
   * @param settings       the settings
   * @param strHolidayDate the str holiday date
   * @return the boolean
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
   * Update settings.
   *
   * @param settings the settings
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
   * Gets current system settings.
   *
   * @return the current system settings
   */
// + getCurrentPrice():Price
  public Settings getCurrentSystemSettings() {
    if (this.currentSettings == null) this.currentSettings = this.getSettings();
    Helper.logger("SettingsHandler.getCurrentSystemSettings", "Settings: \n" + this.currentSettings);

    return new Settings(this.currentSettings);
  }

  //- getDefaultPricingScheme():Price
  private Settings getDefaultSettings() {
    double adultTicketPrice = 10;
    double blockbusterSurcharge = 8;

    EnumMap<Showtime.ShowType, Double> showSurcharges = new EnumMap<Showtime.ShowType, Double>(Showtime.ShowType.class) {{
      put(Showtime.ShowType.Digital, 0.0);
      put(Showtime.ShowType.ThreeDimensional, 5.0);
    }};

    EnumMap<Booking.TicketType, Double> ticketSurcharges = new EnumMap<Booking.TicketType, Double>(Booking.TicketType.class) {{
      put(TicketType.STUDENT, -5.0);
      put(TicketType.SENIOR, -5.0);
      put(TicketType.PEAK, 5.0);
      put(TicketType.SUPER_PEAK, 8.0);
      put(TicketType.NON_PEAK, 0.0);
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

  /**
   * Verify ticket type ticket type.
   *
   * @param showDateTime the show date time
   * @param ticketType   the ticket type
   * @return the ticket type
   */
  public TicketType verifyTicketType(LocalDateTime showDateTime, TicketType ticketType) {
    // Check if PEAK
    DayOfWeek day = showDateTime.getDayOfWeek();
    int hour = showDateTime.getHour();

    List<DayOfWeek> peakDays = Arrays.asList(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    int endPeakHour = 18;
    boolean isPeakDay = peakDays.contains(day);
    boolean isBeforePeakEnd = hour >= endPeakHour;

    if(isPeakDay && isBeforePeakEnd) return ticketType = TicketType.PEAK;

    List<DayOfWeek> weekendDays = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    boolean isWeekend = weekendDays.contains(day);
    boolean isHoliday = this.currentSettings.getHolidays().stream().anyMatch(h -> h.isEqual(showDateTime.toLocalDate()));
    if (isHoliday || isWeekend) ticketType = TicketType.SUPER_PEAK;

    return ticketType;
  }

  /**
   * Compute ticket price double.
   *
   * @param isBlockbuster the is blockbuster
   * @param showType      the show type
   * @param classType     the class type
   * @param ticketType    the ticket type
   * @param showDateTime  the show date time
   * @return the double
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
   * Compute total cost double.
   *
   * @param isBlockbuster the is blockbuster
   * @param showType      the show type
   * @param classType     the class type
   * @param ticketType    the ticket type
   * @param showDateTime  the show date time
   * @param seatCount     the seat count
   * @return the double
   */
//+ computeTotalCost(
  public double computeTotalCost(boolean isBlockbuster, Showtime.ShowType showType, Cinema.ClassType classType, Booking.TicketType ticketType, LocalDateTime showDateTime, int seatCount) {
    // final double GST_PERCENT = 0.07;

    if (seatCount <= 0) return 0;
    double totalCost = this.computeTicketPrice(isBlockbuster, showType, classType, ticketType, showDateTime) * seatCount;
    // totalCost += totalCost * GST_PERCENT;

    return totalCost;
  }

  /**
   * Gets settings.
   *
   * @return the settings
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
   * Save settings boolean.
   *
   * @return the boolean
   */
//# saveSettings():boolean
  protected boolean saveSettings() {
    List<Settings> settings = new ArrayList<Settings>();
    settings.add(this.currentSettings);
    HolidayDatasource.saveHolidays(this.currentSettings.getHolidays());
    return Datasource.serializeData(settings, "settings.csv");
  }

}
