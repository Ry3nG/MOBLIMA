package control;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import entity.Customer;
import tmdb.control.Datasource;
import utils.Helper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerHandler {
  private List<Customer> customers;
  private Customer currentCustomer = null;

  public CustomerHandler() {
    customers = this.getCustomers();
  }

  //+ setCurrentCustomer(customerIdx:int) : void
  public void setCurrentCustomer(int customerIdx) {
    this.currentCustomer = this.getCustomer(customerIdx);
  }

  //+ getCurrentCustomer() : Customer
  public Customer getCurrentCustomer(){
    return this.currentCustomer;
  }

  //+ getCustomers() : List<Customer>
  public List<Customer> getCustomers() {
    List<Customer> customers = new ArrayList<Customer>();
    //TODO: Source from serialized datasource
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

  //+ getCustomer(customerIdx:int) : Customer
  public Customer getCustomer(
      int customerIdx
  ){
    Customer customer = null;
    if(customerIdx < 0 || this.customers.size() < 1) return customer;

    customer = this.customers.get(customerIdx);
    return customer;
  }

  //+ getCustomer(customerId: String) : Customer
  public Customer getCustomer(
      String customerId
  ){
    Customer customer = null;
    if(this.customers.size() < 1) return customer;

    for(Customer c:this.customers){
      if(c.getId().equals(customerId)){
        customer = c;
        break;
      }
    }
    return customer;
  }

  //+ getCustomerIdx(customerId: String) : int
  public int getCustomerIdx(String customerId){
    int customerIdx = -1;
    if(customerId.isEmpty() || this.customers.size() < 1) return customerIdx;

    for(int i = 0; i < this.customers.size(); i++){
      Customer customer = this.customers.get(i);
      if(customer.getId().equals(customerId)){
        customerIdx = i;
        break;
      }
    }
    return customerIdx;
  }

  //+addCustomer(customer : Customer) : int
  public int addCustomer(String name, String contactNumber) {
    if(checkIfAccountExists(contactNumber) != -1) return -1;

    Customer customer = new Customer(
        UUID.randomUUID().toString(),
        name,
        contactNumber
//        email
    );

    this.customers.add(customer);
    this.currentCustomer = customer;

    // Serialize data
    this.saveCustomers();

    return this.customers.size() - 1;
  }

//+ updateCustomer(customer Customer) : boolean

//+deleteCustomer(customer : Customer) : boolean

  //+ listCustomers() : void
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

  //+ checkIfAccountExists(phoneNumber:String) : int
  public int checkIfAccountExists(
      String contactNumber
  ){
    int customerIdx = -1;
    if(this.customers.size() < 1) return customerIdx;

    for(int i = 0; i < this.customers.size(); i++){
      Customer customer = this.customers.get(i);
      if(customer.getContactNumber().equals(contactNumber)){
        customerIdx = i;
        break;
      }
    }

    return customerIdx;
  }

  public boolean saveCustomers() {
    return Datasource.serializeData(this.customers, "customers.csv");
  }

}
