package main.entity;

public class Staff extends Account {
  private String name;
  private String cinemaCode;

  public Staff(
      String id,
      String username,
      String password,
      String name,
      Type type,
      String cinemaCode
  ) {
    super(id, username, password, type);
    this.name = name;
    this.cinemaCode = cinemaCode;
  }

  public String getCinemaCode() {
    return cinemaCode;
  }

  public void setCinemaCode(String cinemaCode) {
    this.cinemaCode = cinemaCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
