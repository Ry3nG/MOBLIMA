package entities;

/**
 * Encapsulates account details
 *
 * @author Crystal Cheong
 * @version 1.0
 */
public class Account {
  protected String id;
  protected String name;

  public Account(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
