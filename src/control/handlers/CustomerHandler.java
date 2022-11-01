package control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import entity.Customer;
import utils.Helper;
import utils.datasource.Datasource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerHandler {
  private final List<Customer> customers;
  private Customer currentCustomer = null;

  public CustomerHandler() {
    this.customers = this.getCustomers();
  }

  /**
   * Validate phone number in account login/registration
   * SG Phone Numbers requires exactly 8 digits
   *
   * @param phoneNumber:String
   * @return status:boolean
   */
  // + validatePhoneNumber(String phoneNumber) : boolean
  public boolean validatePhoneNumber(String phoneNumber) {
    return phoneNumber.matches("^[0-9]{8}$");
  }

  /**
   * Get currently selected / active customer
   *
   * @return customer:Customer | null
   */
  // + getCurrentCustomer() : Customer
  public Customer getCurrentCustomer() {
    return this.currentCustomer;
  }

  /**
   * Save currently selected / active customer by idx
   *
   * @param customerIdx:int
   */
  // + setCurrentCustomer(customerIdx:int) : void
  public void setCurrentCustomer(int customerIdx) {
    this.currentCustomer = this.getCustomer(customerIdx);
  }

  /**
   * Deserializes and return customer list
   *
   * @return customers:List<Customer>
   */
  // + getCustomers() : List<Customer>
  public List<Customer> getCustomers() {
    List<Customer> customers = new ArrayList<Customer>();
    //Source from serialized datasource
    String fileName = "customers.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("CustomerHandler.getCustomers", "Null and void filename provided, no data retrieved.");
      return customers;
    }

    JsonArray customerList = Datasource.readArrayFromCsv(fileName);
    if (customerList == null) {
      Helper.logger("CustomerHandler.getCustomers", "No serialized data available");
      return customers;
    }

    String strCustomerList = Datasource.getGson().toJson(customerList);
    Type typeCustomerList = new TypeToken<List<Customer>>() {
    }.getType();
    customers = Datasource.getGson().fromJson(strCustomerList, typeCustomerList);

    return customers;
  }

  /**
   * Get customer by specified idx
   *
   * @param customerIdx:int
   * @return customer:Customer | null
   */
  // + getCustomer(customerIdx:int) : Customer
  public Customer getCustomer(int customerIdx) {
    Customer customer = null;
    if (customerIdx < 0 || this.customers.size() < 1)
      return customer;

    customer = this.customers.get(customerIdx);
    return customer;
  }

  /**
   * Retrieve idx of specified customer id
   *
   * @param customerId:String
   * @return customerIdx:int
   */
  // + getCustomerIdx(customerId: String) : int
  public int getCustomerIdx(String customerId) {
    int customerIdx = -1;
    if (customerId.isEmpty() || this.customers.size() < 1)
      return customerIdx;

    for (int i = 0; i < this.customers.size(); i++) {
      Customer customer = this.customers.get(i);
      if (customer.getId().equals(customerId)) {
        customerIdx = i;
        break;
      }
    }
    return customerIdx;
  }

  /**
   * Append new customer to customer list
   *
   * @param name:String
   * @param contactNumber:String
   * @return customerIdx:int
   */
  // +addCustomer(name:String, contactNumber:String) : int
  public int addCustomer(String name, String contactNumber, String emailAddress) {
    if (checkIfAccountExists(contactNumber) != -1)
      return -1;

    // Initialize new Customer
    Customer customer = new Customer(
        UUID.randomUUID().toString(),
        name,
        contactNumber,
        emailAddress
    );
    this.customers.add(customer);
    this.currentCustomer = customer;

    // Serialize data
    this.saveCustomers();

    return this.customers.size() - 1;
  }

  /**
   * Get customer list
   */
  // + listCustomers() : void
  public void listCustomers() {
    if (this.customers.isEmpty()) {
      System.out.println("No customers registered");
      return;
    }

    for (int i = 0; i < this.customers.size(); i++) {
      Customer customer = this.customers.get(i);
      System.out.println("> " + i + " " + customer.getName());
    }
  }

  /**
   * Check if account with phone number already exists
   *
   * @param contactNumber:String
   * @return customerIdx:int
   */
  // + checkIfAccountExists(contactNumber:String) : int
  public int checkIfAccountExists(
      String contactNumber
  ) {
    int customerIdx = -1;
    if (this.customers.size() < 1)
      return customerIdx;

    for (int i = 0; i < this.customers.size(); i++) {
      Customer customer = this.customers.get(i);
      if (customer.getContactNumber().equals(contactNumber)) {
        customerIdx = i;
        break;
      }
    }

    return customerIdx;
  }

  /**
   * Serialize customer data to CSV
   */
  //# saveCustomers():boolean
  public boolean saveCustomers() {
    return Datasource.serializeData(this.customers, "customers.csv");
  }

  //// + updateCustomer(customer Customer) : boolean

  //// +deleteCustomer(customer : Customer) : boolean

  //  // + getCustomer(customerId: String) : Customer
  //  public Customer getCustomer(String customerId) {
  //    Customer customer = null;
  //    if (this.customers.size() < 1)
  //      return customer;
  //
  //    for (Customer c : this.customers) {
  //      if (c.getId().equals(customerId)) {
  //        customer = c;
  //        break;
  //      }
  //    }
  //    return customer;
  //  }

}
