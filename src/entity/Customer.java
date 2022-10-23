package entity;

public class Customer {
  private String id;
  private String name;
  private String contactNumber;
//  private String email;

  public Customer(String id, String name, String contactNumber) {
    this.id = id;
    this.name = name;
    this.contactNumber = contactNumber;
//    this.email = email;
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

//  public String getEmail() {
//    return email;
//  }
//
//  public void setEmail(String email) {
//    this.email = email;
//  }
}
