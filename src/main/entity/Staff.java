package main.entity;

public class Staff extends Account {
  private String cinemaId;

  public Staff(
      String id,
      String username,
      String password
  ) {
    super(id, username, password);
  }
}
