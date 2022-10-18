package main.entity;

public class Account {
  private String id, username, password;

  public Account(
      String id,
      String username,
      String password
  ) {
    this.id = id;
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String[] toPrint() {
    return new String[]{this.id, this.username, this.password};
  }

}
