package control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import entity.Booking;
import entity.Cinema;
import entity.Price;
import moblima.control.Datasource;
import utils.Helper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class PriceHandler {
  private static PriceHandler instance;
  private Price currentPrice;

  private PriceHandler() {
    this.currentPrice = this.getCurrentPrice();
  }

  public static PriceHandler getInstance() {
    if (instance == null) instance = new PriceHandler();
    return instance;
  }

  /**
   * Updates current price
   *
   * @param price:Price
   */
  //+updatePrice(price : Price) : void
  public void updatePrice(Price price) {
    // Replace current price
    this.currentPrice = price;
    Helper.logger("PriceHandler.updatePrice", "Price: \n" + this.currentPrice);

    // Serialize data
    this.savePrices();
  }

  /**
   * Returns current price
   *
   * @return price:Price | null
   */
  // + getCurrentPrice():Price
  public Price getCurrentPrice() {
    Price defaultPrice = this.getPrices();
    return new Price(this.currentPrice);
  }

  /**
   * Generates default pricing scheme
   *
   * @return defaultPrice:Price
   */
  //- getDefaultPricingScheme():Price
  private Price getDefaultPricingScheme() {
    double baseTicketPrice = 10;
    double blockbusterSurcharge = 8;

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

    return new Price(baseTicketPrice, blockbusterSurcharge, ticketSurcharges, cinemaSurcharges);
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
  public double computeTicketPrice(boolean isBlockbuster, Cinema.ClassType classType, Booking.TicketType ticketType) {
    Helper.logger("PriceHandler.computeTicketPrice", "currentPrice:\n" + this.currentPrice.toString());
    double price = this.currentPrice.getBaseTicket();

    // Blockbuster Surcharges
    if (isBlockbuster) price += this.currentPrice.getBlockbusterSurcharge();

    // Cinema Surcharges
    EnumMap<Cinema.ClassType, Double> cinemaSurcharge = this.currentPrice.getCinemaSurcharges();
    if (cinemaSurcharge.containsKey(classType)) price += cinemaSurcharge.get(classType);

    // Ticket Surcharges
    EnumMap<Booking.TicketType, Double> ticketSurcharges = this.currentPrice.getTicketSurcharges();
    if (ticketSurcharges.containsKey(ticketType)) price += ticketSurcharges.get(ticketType);

    return price;
  }

  //+ computeTotalCost(
  public double computeTotalCost(boolean isBlockbuster, Cinema.ClassType classType, Booking.TicketType ticketType, int seatCount) {
    final double GST_PERCENT = 0.07;

    if (seatCount <= 0) return 0;
    double totalCost = this.computeTicketPrice(isBlockbuster, classType, ticketType) * seatCount;
    totalCost += totalCost * GST_PERCENT;

    return totalCost;
  }

  //+ getPrices():Price
  public Price getPrices() {
    List<Price> prices = new ArrayList<Price>();

    // Intialize with default pricing
    this.currentPrice = this.getDefaultPricingScheme();

    //Source from serialized datasource
    String fileName = "prices.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("PriceHandler.getPrices", "Null and void filename provided, no data retrieved.");
      return this.currentPrice;
    }

    JsonArray priceList = Datasource.readArrayFromCsv(fileName);
    if (priceList == null) {
      Helper.logger("PriceHandler.getPrices", "No serialized data available");
      return this.currentPrice;
    }

    for (JsonElement price : priceList) {
      JsonObject p = price.getAsJsonObject();

      double baseTicket = p.get("baseTicket").getAsDouble();
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

      prices.add(new Price(
          baseTicket,
          blockbusterSurcharge,
          ticketSurcharges,
          cinemaSurcharges
      ));
    }

    if (prices.size() < 1) return this.currentPrice;

    // Update current price
    this.updatePrice(prices.get(0));

    return this.currentPrice;
  }

  /**
   * Serialize price data to CSV
   */
  //# savePrices():boolean
  protected boolean savePrices() {
    List<Price> prices = new ArrayList<Price>();
    prices.add(this.currentPrice);
    return Datasource.serializeData(prices, "prices.csv");
  }
}
