package moblima.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static moblima.utils.Helper.formatAsTable;

/**
 * The type Customer.
 */
public class Customer extends Account {
  private String contactNumber;
  private String emailAddress;

  /**
   * Instantiates a new Customer.
   *
   * @param name          the name
   * @param contactNumber the contact number
   * @param emailAddress  the email address
   */
  public Customer(String name, String contactNumber, String emailAddress) {
    super(name);
    this.contactNumber = contactNumber;
    this.emailAddress = emailAddress;
  }

  /**
   * Gets contact number.
   *
   * @return the contact number
   */
  public String getContactNumber() {
    return contactNumber;
  }

  /**
   * Sets contact number.
   *
   * @param contactNumber the contact number
   */
  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  /**
   * Gets email address.
   *
   * @return the email address
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * Sets email address.
   *
   * @param emailAddress the email address
   */
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String toString() {
    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Account:", this.id));
    rows.add(Arrays.asList("Name:", this.name));
    rows.add(Arrays.asList("Contact No.:", this.contactNumber));
    rows.add(Arrays.asList("Email Addr.:", this.emailAddress));

    return formatAsTable(rows);
  }

}
