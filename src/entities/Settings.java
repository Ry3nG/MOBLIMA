package entities;

import utils.deserializers.LocalDateDeserializer;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static utils.Helper.formatAsTable;

public class Settings {
  private double adultTicket;
  private double blockbusterSurcharge;
  private EnumMap<Showtime.ShowType, Double> showSurcharges;
  private EnumMap<Booking.TicketType, Double> ticketSurcharges;
  private EnumMap<Cinema.ClassType, Double> cinemaSurcharges;
  private List<LocalDate> publicHolidays;

  public Settings(double adultTicket, double blockbusterSurcharge, EnumMap<Showtime.ShowType, Double> showSurcharges, EnumMap<Booking.TicketType, Double> ticketSurcharges, EnumMap<Cinema.ClassType, Double> cinemaSurcharges, List<LocalDate> publicHolidays) {
    this.adultTicket = adultTicket;
    this.blockbusterSurcharge = blockbusterSurcharge;
    this.showSurcharges = showSurcharges;
    this.ticketSurcharges = ticketSurcharges;
    this.cinemaSurcharges = cinemaSurcharges;
    this.publicHolidays = publicHolidays;
  }

  /**
   * Clone constructor
   *
   * @param settings:SystemSettings
   */
  public Settings(Settings settings) {
    this(
        settings.adultTicket,
        settings.blockbusterSurcharge,
        settings.showSurcharges,
        settings.ticketSurcharges,
        settings.cinemaSurcharges,
        settings.publicHolidays
    );
  }

  public double getAdultTicket() {
    return adultTicket;
  }

  public void setAdultTicket(double adultTicket) {
    this.adultTicket = adultTicket;
  }

  public double getBlockbusterSurcharge() {
    return blockbusterSurcharge;
  }

  public void setBlockbusterSurcharge(double blockbusterSurcharge) {
    this.blockbusterSurcharge = blockbusterSurcharge;
  }

  public EnumMap<Showtime.ShowType, Double> getShowSurcharges() {
    return showSurcharges;
  }

  public void setShowSurcharges(EnumMap<Showtime.ShowType, Double> showSurcharges) {
    this.showSurcharges = showSurcharges;
  }

  public List<LocalDate> getPublicHolidays() {
    return publicHolidays;
  }

  public void setPublicHolidays(List<LocalDate> publicHolidays) {
    this.publicHolidays = publicHolidays;
  }

  public EnumMap<Booking.TicketType, Double> getTicketSurcharges() {
    return ticketSurcharges;
  }

  public void setTicketSurcharges(EnumMap<Booking.TicketType, Double> ticketSurcharges) {
    this.ticketSurcharges = ticketSurcharges;
  }

  public EnumMap<Cinema.ClassType, Double> getCinemaSurcharges() {
    return cinemaSurcharges;
  }

  public void setCinemaSurcharges(EnumMap<Cinema.ClassType, Double> cinemaSurcharges) {
    this.cinemaSurcharges = cinemaSurcharges;
  }

  public List<LocalDate> getHolidays() {
    return this.publicHolidays;
  }

  public void setHolidays(List<LocalDate> publicHolidays) {
    this.publicHolidays = publicHolidays;
  }

  public void addHoliday(LocalDate holiday) {
    this.publicHolidays.add(holiday);
  }

  public boolean removeHoliday(LocalDate holiday) {
    return this.publicHolidays.remove(holiday);
  }

  public String formatPrice(double price) {
    DecimalFormat df = new DecimalFormat("0.00");
    return "SGD " + df.format(price);
  }

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
    this.showSurcharges.entrySet()
        .stream()
        .forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formatPrice(entry.getValue()))));

    // Ticket surcharges
    rows.add(Arrays.asList("\nTicket Surcharges:", ""));
    this.ticketSurcharges.entrySet()
        .stream()
        .forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formatPrice(entry.getValue()))));

    // Cinema surcharges
    rows.add(Arrays.asList("\nCinema Surcharges:", ""));
    this.cinemaSurcharges.entrySet()
        .stream()
        .forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formatPrice(entry.getValue()))));

    rows.addAll(this.printHolidayTable());

    return formatAsTable(rows);
  }
}
