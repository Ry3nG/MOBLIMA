package entity;

import java.util.ArrayList;
import java.util.List;

public class Cinema {
  private int id;
  private ClassType classType;
  private List<Showtime> showtimes;

  public Cinema(int id, ClassType classType, List<Showtime> showtimes) {
    this.id = id;
    this.classType = classType;
    this.showtimes = new ArrayList<Showtime>();
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

  public enum ClassType {
    Normal, Premium
  }
}
