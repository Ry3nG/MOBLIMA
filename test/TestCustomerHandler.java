import moblima.control.handlers.CustomerHandler;
import moblima.entities.Customer;
import moblima.utils.datasource.Datasource;
import org.junit.jupiter.api.*;

import java.util.List;

import static java.lang.System.exit;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.function.Try.success;

@Tag("TestCustomerHandler")
public class TestCustomerHandler {
  private static CustomerHandler handler;
  private static List<Customer> customers;

  /**
   * Init.
   */
  @BeforeAll
  public static void init() {
    handler = new CustomerHandler();
    customers = handler.getCustomers();
    assertNotNull(handler, "Handler instance was null");

    int customerCount = handler.getCustomers().size();
    int storedCustomerCount = customers.size();
    assertEquals(storedCustomerCount, customerCount, "Customers: " + customers.size());
  }


  @BeforeEach
  public void reset() {
    boolean isResetted = Datasource.deleteFile("customers.csv");
    assertEquals(true, isResetted, "Resetted customer list by deletion");
  }


  @AfterEach
  public void restore(){
    reset();
    // Restore customers
    int customerCount = handler.getCustomers().size();
    int storedCustomerCount = customers.size();

    if(customers.size() > 0){
      Datasource.serializeData(customers, "customers.csv");

      customerCount = handler.getCustomers().size();
      storedCustomerCount = customers.size();
      assertEquals(storedCustomerCount, customerCount, "Restored customer list");
    }
  }

  /**
   * Teardown.
   */
  @AfterAll
  public static void teardown() {
    handler = null;
    assertNull(handler, "Handler instance is null");
    exit(0);
  }

  @Test
  public void testAddCustomer() {
    int customerCount = handler.getCustomers().size();
    String name = "Test Testy";
    String contactNumber = "12345678";
    String emailAddress = "tttesttt@mail.com";

    // CASE: Added
    boolean isAdded = true;
    int actualIdx = handler.addCustomer(name, contactNumber, emailAddress);
    boolean output = (actualIdx > -1);
    customerCount = handler.getCustomers().size();
    assertEquals(isAdded, output);
    success("[SUCCESS/TestCustomerHandler.testAddCustomer] Case: Added = PASSED");

    // CASE: Duplicate contactNumber
    isAdded = false;
    actualIdx = handler.addCustomer(name, contactNumber, emailAddress);
    customerCount = handler.getCustomers().size();
    assertEquals(isAdded, (actualIdx > -1));
    success("[SUCCESS/TestCustomerHandler.testAddCustomer] Case: Duplicate contactNumber = PASSED");
  }
}
