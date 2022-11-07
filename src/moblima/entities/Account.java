package moblima.entities;

import java.util.UUID;

/**
 * The type Account.
 */
public abstract class Account {
  /**
   * The Id.
   */
  protected String id;
  /**
   * The Name.
   */
  protected String name;

  /**
   * Instantiates a new Account.
   *
   * @param id   the id
   * @param name the name
   */
  public Account(String id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Instantiates a new Account.
   *
   * @param name the name
   */
  public Account(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }
}
