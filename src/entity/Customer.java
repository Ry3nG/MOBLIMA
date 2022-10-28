package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.Helper.formatAsTable;

public class Customer {
  private String id;
  private String name;
  private String contactNumber;
  private String emailAddress;

  public Customer(String id, String name, String contactNumber, String emailAddress) {
    this.id = id;
    this.name = name;
    this.contactNumber = contactNumber;
    this.emailAddress = emailAddress;
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

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

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
