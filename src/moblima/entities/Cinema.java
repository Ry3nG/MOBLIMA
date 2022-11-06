package moblima.entities;

import java.util.List;

/**
 * The type Cinema.
 */
public class Cinema {

  private int id;

  private ClassType classType;
  private List<Showtime> showtimes;
  private String cineplexCode;

  /**
   * Instantiates a new Cinema.
   *
   * @param id           the id
   * @param classType    the class type
   * @param showtimes    the showtimes
   * @param cineplexCode the cineplex code
   */
  public Cinema(int id, ClassType classType, List<Showtime> showtimes, String cineplexCode) {
    this.id = id;
    this.classType = classType;
    this.showtimes = showtimes;
    this.cineplexCode = cineplexCode;
  }


  /**
   * Instantiates a new Cinema.
   *
   * @param cloneCinema the clone cinema
   */
  public Cinema(Cinema cloneCinema) {
    this(cloneCinema.id, cloneCinema.classType, cloneCinema.showtimes, cloneCinema.cineplexCode);
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets class type.
   *
   * @return the class type
   */
  public ClassType getClassType() {
    return classType;
  }

  /**
   * Sets class type.
   *
   * @param classType the class type
   */
  public void setClassType(ClassType classType) {
    this.classType = classType;
  }

  /**
   * Gets showtimes.
   *
   * @return the showtimes
   */
  public List<Showtime> getShowtimes() {
    return showtimes;
  }

  /**
   * Sets showtimes.
   *
   * @param showtimes the showtimes
   */
  public void setShowtimes(List<Showtime> showtimes) {
    this.showtimes = showtimes;
  }

  /**
   * Gets cineplex code.
   *
   * @return the cineplex code
   */
  public String getCineplexCode() {
    return cineplexCode;
  }

  /**
   * Sets cineplex code.
   *
   * @param cineplexCode the cineplex code
   */
  public void setCineplexCode(String cineplexCode) {
    this.cineplexCode = cineplexCode;
  }

  @Override
  public String toString() {
    String printed = "ID: " + this.id + "\n";
    printed += "Cineplex Code: " + this.cineplexCode + "\n";
    printed += "Class Type: " + this.classType + "\n";
    printed += "Total Showtimes: " + this.showtimes.size() + "\n";

    return printed;
  }

  /**
   * The enum Class type.
   */
  public enum ClassType {
    /**
     * Normal class type.
     */
    Normal,
    /**
     * Premium class type.
     */
    Premium
  }
}
