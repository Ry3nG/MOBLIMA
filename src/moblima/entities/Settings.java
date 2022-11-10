package moblima.entities;

import moblima.entities.Booking.TicketType;
import moblima.entities.Cinema.ClassType;
import moblima.entities.Showtime.ShowType;
import moblima.utils.deserializers.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static moblima.utils.Helper.formatAsTable;
import static moblima.utils.Helper.formatPrice;

/**
 * The type Settings.
 */
public class Settings {

  private double adultTicket;
  private double blockbusterSurcharge;
  private EnumMap<ShowType, Double> showSurcharges;
  private EnumMap<TicketType, Double> ticketSurcharges;
  private EnumMap<ClassType, Double> cinemaSurcharges;
  private EnumMap<RankedType, Boolean> rankedTypes;
  private List<LocalDate> publicHolidays;

  /**
   * Instantiates a new Settings.
   *
   * @param adultTicket          the adult ticket
   * @param blockbusterSurcharge the blockbuster surcharge
   * @param showSurcharges       the show surcharges
   * @param ticketSurcharges     the ticket surcharges
   * @param cinemaSurcharges     the cinema surcharges
   * @param rankedTypes          the ranked types
   * @param publicHolidays       the public holidays
   */
  public Settings(double adultTicket, double blockbusterSurcharge, EnumMap<ShowType, Double> showSurcharges, EnumMap<TicketType, Double> ticketSurcharges, EnumMap<ClassType, Double> cinemaSurcharges, EnumMap<RankedType, Boolean> rankedTypes, List<LocalDate> publicHolidays) {
    this.adultTicket = adultTicket;
    this.blockbusterSurcharge = blockbusterSurcharge;
    this.showSurcharges = showSurcharges;
    this.ticketSurcharges = ticketSurcharges;
    this.cinemaSurcharges = cinemaSurcharges;
    this.rankedTypes = rankedTypes;
    this.publicHolidays = publicHolidays;
  }

  /**
   * Instantiates a new Settings.
   *
   * @param settings the settings
   */
  public Settings(Settings settings) {
    this(settings.adultTicket, settings.blockbusterSurcharge, settings.showSurcharges, settings.ticketSurcharges, settings.cinemaSurcharges, settings.rankedTypes, settings.publicHolidays);
  }

  /**
   * Gets adult ticket.
   *
   * @return the adult ticket
   */
  public double getAdultTicket() {
    return adultTicket;
  }

  /**
   * Sets adult ticket.
   *
   * @param adultTicket the adult ticket
   */
  public void setAdultTicket(double adultTicket) {
    this.adultTicket = adultTicket;
  }

  /**
   * Gets blockbuster surcharge.
   *
   * @return the blockbuster surcharge
   */
  public double getBlockbusterSurcharge() {
    return blockbusterSurcharge;
  }

  /**
   * Sets blockbuster surcharge.
   *
   * @param blockbusterSurcharge the blockbuster surcharge
   */
  public void setBlockbusterSurcharge(double blockbusterSurcharge) {
    this.blockbusterSurcharge = blockbusterSurcharge;
  }

  /**
   * Gets show surcharges.
   *
   * @return the show surcharges
   */
  public EnumMap<Showtime.ShowType, Double> getShowSurcharges() {
    return showSurcharges;
  }

  /**
   * Sets show surcharges.
   *
   * @param showSurcharges the show surcharges
   */
  public void setShowSurcharges(EnumMap<Showtime.ShowType, Double> showSurcharges) {
    this.showSurcharges = showSurcharges;
  }

  /**
   * Gets public holidays.
   *
   * @return the public holidays
   */
  public List<LocalDate> getPublicHolidays() {
    return publicHolidays;
  }

  /**
   * Sets public holidays.
   *
   * @param publicHolidays the public holidays
   */
  public void setPublicHolidays(List<LocalDate> publicHolidays) {
    this.publicHolidays = publicHolidays;
  }

  /**
   * Gets ticket surcharges.
   *
   * @return the ticket surcharges
   */
  public EnumMap<Booking.TicketType, Double> getTicketSurcharges() {
    return ticketSurcharges;
  }

  /**
   * Sets ticket surcharges.
   *
   * @param ticketSurcharges the ticket surcharges
   */
  public void setTicketSurcharges(EnumMap<Booking.TicketType, Double> ticketSurcharges) {
    this.ticketSurcharges = ticketSurcharges;
  }

  /**
   * Gets cinema surcharges.
   *
   * @return the cinema surcharges
   */
  public EnumMap<Cinema.ClassType, Double> getCinemaSurcharges() {
    return cinemaSurcharges;
  }

  /**
   * Sets cinema surcharges.
   *
   * @param cinemaSurcharges the cinema surcharges
   */
  public void setCinemaSurcharges(EnumMap<Cinema.ClassType, Double> cinemaSurcharges) {
    this.cinemaSurcharges = cinemaSurcharges;
  }

  /**
   * Gets ranked lists.
   *
   * @return the ranked lists
   */
  public EnumMap<RankedType, Boolean> getRankedTypes() {
    return rankedTypes;
  }

  /**
   * Sets ranked lists.
   *
   * @param rankedTypes the ranked lists
   */
  public void setRankedTypes(EnumMap<RankedType, Boolean> rankedTypes) {
    this.rankedTypes = rankedTypes;
  }

  /**
   * Gets holidays.
   *
   * @return the holidays
   */
  public List<LocalDate> getHolidays() {
    return this.publicHolidays;
  }

  /**
   * Sets holidays.
   *
   * @param publicHolidays the public holidays
   */
  public void setHolidays(List<LocalDate> publicHolidays) {
    this.publicHolidays = publicHolidays;
  }

  /**
   * Add holiday.
   *
   * @param holiday the holiday
   */
  public void addHoliday(LocalDate holiday) {
    this.publicHolidays.add(holiday);
  }

  /**
   * Remove holiday boolean.
   *
   * @param holiday the holiday
   * @return the boolean
   */
  public boolean removeHoliday(LocalDate holiday) {
    return this.publicHolidays.remove(holiday);
  }

  /**
   * Print holiday table list.
   *
   * @return the list
   */
  public List<List<String>> printHolidayTable() {
    List<List<String>> rows = new ArrayList<List<String>>();
    // Public holidays
    rows.add(Arrays.asList("\nPublic Holidays:", ""));
    if (this.publicHolidays.size() == 0) rows.add(Arrays.asList("No public holidays.", ""));
    else {
      for (LocalDate holiday : this.publicHolidays) {
        rows.add(Arrays.asList(holiday.format(LocalDateDeserializer.dateFormatter), holiday.getDayOfWeek().toString()));
      }
    }

    return rows;
  }

  @Override
  public String toString() {
    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Adult Ticket:", formatPrice(this.adultTicket)));
    rows.add(Arrays.asList("Blockbuster Surcharge:", formatPrice(this.blockbusterSurcharge)));

    // Show surcharges
    rows.add(Arrays.asList("\nShow Surcharges:", ""));
    this.showSurcharges.entrySet().stream().forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formatPrice(entry.getValue()))));

    // Ticket surcharges
    rows.add(Arrays.asList("\nTicket Surcharges:", ""));
    this.ticketSurcharges.entrySet().stream().forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formatPrice(entry.getValue()))));

    // Cinema surcharges
    rows.add(Arrays.asList("\nCinema Surcharges:", ""));
    this.cinemaSurcharges.entrySet().stream().forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formatPrice(entry.getValue()))));

    // Ranked lists
    rows.add(Arrays.asList("\nRanked Types:", ""));
    this.rankedTypes.entrySet().stream().forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), entry.getValue().toString())));

    rows.addAll(this.printHolidayTable());

    return formatAsTable(rows);
  }

  /**
   * The enum Ranked type.
   */
  public enum RankedType {
    /**
     * Movies by tickets ranked type.
     */
    MOVIES_BY_TICKETS("Top 5 movies by ticket sales"),
    /**
     * Movies by ratings ranked type.
     */
    MOVIES_BY_RATINGS("Top 5 movies by overall rating");

    private final String displayName;

    RankedType(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
