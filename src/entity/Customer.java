package entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.Helper.formatAsTable;

public class Customer {
  private String id;
  private String name;
  private String contactNumber;

  public Customer(String id, String name, String contactNumber) {
    this.id = id;
    this.name = name;
    this.contactNumber = contactNumber;
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

  @Override
  public String toString() {
    List<List<String>> rows = new ArrayList<List<String>>();
    rows.add(Arrays.asList("Account:", this.id));
    rows.add(Arrays.asList("Name:", this.name));
    rows.add(Arrays.asList("Contact No.:", this.contactNumber));

    return formatAsTable(rows);
  }

}
