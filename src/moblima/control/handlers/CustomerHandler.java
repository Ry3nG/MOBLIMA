package moblima.control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import moblima.entities.Customer;
import moblima.utils.Helper;
import moblima.utils.datasource.Datasource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Customer handler.
 */
public class CustomerHandler {
  /**
   * The Customers.
   */
  protected final List<Customer> customers;
  /**
   * The Current customer.
   */
  protected Customer currentCustomer = null;

  /**
   * Instantiates a new Customer handler.
   */
  public CustomerHandler() {
    this.customers = this.getCustomers();
  }

  /**
   * Validate phone number boolean.
   *
   * @param phoneNumber the phone number
   * @return the boolean
   */
// + validatePhoneNumber(String phoneNumber) : boolean
  public boolean validatePhoneNumber(String phoneNumber) {
    return phoneNumber.matches("^[0-9]{8}$");
  }

  /**
   * Gets current customer.
   *
   * @return the current customer
   */
// + getCurrentCustomer() : Customer
  public Customer getCurrentCustomer() {
    return this.currentCustomer;
  }

  /**
   * Sets current customer.
   *
   * @param customerIdx the customer idx
   */
// + setCurrentCustomer(customerIdx:int) : void
  public void setCurrentCustomer(int customerIdx) {
    this.currentCustomer = this.getCustomer(customerIdx);
  }

  /**
   * Gets customers.
   *
   * @return the customers
   */
// + getCustomers() : List<Customer>
  public List<Customer> getCustomers() {
    List<Customer> customers = new ArrayList<Customer>();
    //Source from serialized datasource
    String fileName = "customers.csv";
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
   * Gets customer.
   *
   * @param customerIdx the customer idx
   * @return the customer
   */
// + getCustomer(customerIdx:int) : Customer
  public Customer getCustomer(int customerIdx) {
    Customer customer = null;
    if (customerIdx < 0 || this.customers.size() < 1) return customer;

    customer = this.customers.get(customerIdx);
    return customer;
  }

  /**
   * Gets customer idx.
   *
   * @param customerId the customer id
   * @return the customer idx
   */
// + getCustomerIdx(customerId: String) : int
  public int getCustomerIdx(String customerId) {
    int customerIdx = -1;
    if (customerId.isEmpty() || this.customers.size() < 1) return customerIdx;

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
   * Add customer int.
   *
   * @param name          the name
   * @param contactNumber the contact number
   * @param emailAddress  the email address
   * @return the int
   */
// +addCustomer(name:String, contactNumber:String) : int
  public int addCustomer(String name, String contactNumber, String emailAddress) {
    if (checkIfAccountExists(contactNumber) >= 0) return -1;
    if(!validatePhoneNumber(contactNumber)) return -1;

    // Initialize new Customer
    Customer customer = new Customer(UUID.randomUUID().toString(), name, contactNumber, emailAddress);
    this.customers.add(customer);
    this.currentCustomer = customer;

    // Serialize data
    this.saveCustomers();

    return this.customers.size() - 1;
  }

  /**
   * List customers.
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
   * Check if account exists int.
   *
   * @param contactNumber the contact number
   * @return the int
   */
// + checkIfAccountExists(contactNumber:String) : int
  public int checkIfAccountExists(String contactNumber) {
    int customerIdx = -1;
    if (this.customers.size() < 1) return customerIdx;

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
   * Save customers boolean.
   *
   * @return the boolean
   */
//# saveCustomers():boolean
  public boolean saveCustomers() {
    return Datasource.serializeData(this.customers, "customers.csv");
  }
}
