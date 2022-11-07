package moblima.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static moblima.utils.Helper.formatAsTable;

/**
 * The type Staff.
 */
public class Staff extends Account {
  private String username;
  private String password;

  /**
   * Instantiates a new Staff.
   *
   * @param name     the name
   * @param username the username
   * @param password the password
   */
  public Staff(String name, String username, String password) {
    super(name);
    this.username = username;
    this.password = password;
  }

  /**
   * Gets username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets username.
   *
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Account:", this.id));
    rows.add(Arrays.asList("Name:", this.name));

    return formatAsTable(rows);
  }
}
