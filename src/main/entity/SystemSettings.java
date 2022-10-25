package main.entity;

import java.io.Serializable;
import java.util.ArrayList;
public class SystemSettings implements Serializable {

  /**
   * Price of an Adult ticket
   */
  private double adultTicket;
  /**
   * Price of a Child ticket
   */
  private double childTicket;
  /**
   * Price of a Senior ticket
   */
  private double seniorTicket;
  /**
   * Surcharge for Premium cinema class
   */
  private double cinemaSurchage;
  /**
   * Surcharge for special categories of movies, e.g., Blockbusters, Preview
   */
  private double movieSurcharge;
  /**
   * Surcharge for weekends and public holidays
   */
  private double weekendSurcharge;
  /**
   * List of public holidays
   */
  private ArrayList<String> pHolidays = new ArrayList<String>();

  public SystemSettings(double adultTicket, double childTicket, double seniorTicket,
    double cinemaSurchage, double movieSurcharge, double weekendSurcharge) {
    this.adultTicket = adultTicket;
    this.childTicket = childTicket;
    this.seniorTicket = seniorTicket;
    this.cinemaSurchage = cinemaSurchage;
    this.movieSurcharge = movieSurcharge;
    this.weekendSurcharge = weekendSurcharge;
  }

  public double getAdultTicket() {
    return adultTicket;
  }

  public void setAdultTicket(double adultTicket) {
    this.adultTicket = adultTicket;
  }

  public double getChildTicket() {
    return childTicket;
  }

  public void setChildTicket(double childTicket) {
    this.childTicket = childTicket;
  }

  public double getSeniorTicket() {
    return seniorTicket;
  }

  public void setSeniorTicket(double seniorTicket) {
    this.seniorTicket = seniorTicket;
  }

  public double getCinemaSurchage() {
    return cinemaSurchage;
  }

  public void setCinemaSurchage(double cinemaSurchage) {
    this.cinemaSurchage = cinemaSurchage;
  }

  public double getMovieSurcharge() {
    return movieSurcharge;
  }

  public void setMovieSurcharge(double movieSurcharge) {
    this.movieSurcharge = movieSurcharge;
  }

  public double getWeekendSurcharge() {
    return weekendSurcharge;
  }

  public void setWeekendSurcharge(double weekendSurcharge) {
    this.weekendSurcharge = weekendSurcharge;
  }

  public ArrayList<String> getPublicHolidays() {
    return this.pHolidays;
  }

  public void addPublicHoliday(String yyyymmdd) {
    this.pHolidays.add(yyyymmdd);
  }

  public boolean removePublicHoliday(String yyyymmdd) {
    return this.pHolidays.remove(yyyymmdd);
  }
}
