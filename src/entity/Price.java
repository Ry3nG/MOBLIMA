package entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static utils.Helper.formatAsTable;

public class Price {
  private double baseTicket;
  private double blockbusterSurcharge;
  private EnumMap<Booking.TicketType, Double> ticketSurcharges;
  private EnumMap<Cinema.ClassType, Double> cinemaSurcharges;

  public Price(double baseTicket,double blockbusterSurcharge, EnumMap<Booking.TicketType, Double> ticketSurcharges, EnumMap<Cinema.ClassType, Double> cinemaSurcharges) {
    this.baseTicket = baseTicket;
    this.blockbusterSurcharge = blockbusterSurcharge;
    this.ticketSurcharges = ticketSurcharges;
    this.cinemaSurcharges = cinemaSurcharges;
  }

  /**
   * Clone constructor
   *
   * @param price:Price
   */
  public Price(Price price) {
    this(
        price.baseTicket,
        price.blockbusterSurcharge,
        price.ticketSurcharges,
        price.cinemaSurcharges
    );
  }

  public double getBaseTicket() {
    return baseTicket;
  }

  public void setBaseTicket(double baseTicket) {
    this.baseTicket = baseTicket;
  }

  public double getBlockbusterSurcharge() {
    return blockbusterSurcharge;
  }

  public void setBlockbusterSurcharge(double blockbusterSurcharge) {
    this.blockbusterSurcharge = blockbusterSurcharge;
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

  public String formattedPrice(double price){
    DecimalFormat df = new DecimalFormat("0.00");
    return df.format(price);
  }

  @Override
  public String toString() {
    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Base Ticket:", formattedPrice(this.baseTicket)));
    rows.add(Arrays.asList("Blockbuster Surcharge:", formattedPrice(this.blockbusterSurcharge)));

    // Ticket surcharges
    rows.add(Arrays.asList("\nTicket Surcharges:" ,""));
    this.ticketSurcharges.entrySet()
        .stream()
        .forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formattedPrice(entry.getValue()))));

    // Cinema surcharges
    rows.add(Arrays.asList("\nCinema Surcharges:", ""));
    this.cinemaSurcharges.entrySet()
        .stream()
        .forEachOrdered(entry -> rows.add(Arrays.asList(entry.getKey().toString(), formattedPrice(entry.getValue()))));

    return formatAsTable(rows);
  }
}
