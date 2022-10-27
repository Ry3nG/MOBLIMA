package entity;

import java.util.List;

public class Cinema {
  private int id;
  private ClassType classType;
  private List<Showtime> showtimes;

  public Cinema(int id, ClassType classType, List<Showtime> showtimes) {
    this.id = id;
    this.classType = classType;
    this.showtimes = showtimes;
  }

  /**
   * Clone constructor
   *
   * @param cloneCinema:Cinema
   */
  public Cinema(Cinema cloneCinema) {
    this(
        cloneCinema.id,
        cloneCinema.classType,
        cloneCinema.showtimes
    );
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

  @Override
  public String toString() {
    String printed = "ID: " + this.id + "\n";
    printed += "Class Type: " + this.classType + "\n";
    printed += "Total Showtimes: " + this.showtimes.size() + "\n";

    return printed;
  }

  public enum ClassType {
    Normal, Premium
  }
}
