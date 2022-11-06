package entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.Helper.formatAsTable;

/**
 * Encapsulates staff account details
 *
 * @author Crystal Cheong
 * @version 1.0
 */
public class Staff extends Account {
  private String username;
  private String password;

  /**
   * Default constructor
   *
   * @param id:String
   * @param name:String
   * @param username:String
   * @param password:String
   */
  public Staff(String id, String name, String username, String password) {
    super(id, name);
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

  /**
   * Pretty print Staff object
   *
   * @return strStaff:String
   */
  @Override
  public String toString() {
    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Account:", this.id));
    rows.add(Arrays.asList("Name:", this.name));

    return formatAsTable(rows);
  }
}
