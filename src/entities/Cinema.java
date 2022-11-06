package entities;

import java.util.List;

/**
 * Encapsulates cinema details
 *
 * @author Crystal Cheong
 * @version 1.0
 */
public class Cinema {

  private int id;

  private ClassType classType;
  private List<Showtime> showtimes;
  private String cineplexCode;

  /**
   * Default constructor
   * @param id:int
   * @param classType:ClassType
   * @param showtimes:List<Showtime>
   * @param cineplexCode:String
   */
  public Cinema(int id, ClassType classType, List<Showtime> showtimes, String cineplexCode) {
    this.id = id;
    this.classType = classType;
    this.showtimes = showtimes;
    this.cineplexCode = cineplexCode;
  }


  /**
   * Clone constructor
   *
   * @param cloneCinema:Cinema
   */
  public Cinema(Cinema cloneCinema) {
    this(cloneCinema.id, cloneCinema.classType, cloneCinema.showtimes, cloneCinema.cineplexCode);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public ClassType getClassType() {
    return classType;
  }

  public void setClassType(ClassType classType) {
    this.classType = classType;
  }

  public List<Showtime> getShowtimes() {
    return showtimes;
  }

  public void setShowtimes(List<Showtime> showtimes) {
    this.showtimes = showtimes;
  }

  public String getCineplexCode() {
    return cineplexCode;
  }

  public void setCineplexCode(String cineplexCode) {
    this.cineplexCode = cineplexCode;
  }

  /**
   * Pretty print cinema object
   *
   * @return strCinema:String
   */
  @Override
  public String toString() {
    String printed = "ID: " + this.id + "\n";
    printed += "Cineplex Code: " + this.cineplexCode + "\n";
    printed += "Class Type: " + this.classType + "\n";
    printed += "Total Showtimes: " + this.showtimes.size() + "\n";

    return printed;
  }

  /**
   * Constant values of all possible Cinema class types
   *
   * @author Crystal Cheong
   * @version 1.0
   */
  public enum ClassType {
    Normal, Premium
  }
}
