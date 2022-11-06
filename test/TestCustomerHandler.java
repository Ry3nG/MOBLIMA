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
    assertEquals(customers.size(), handler.getCustomers().size(), "Customers: " + customers.size());
  }

  @BeforeEach
  @AfterEach
  public void reset() {
    boolean isResetted = Datasource.deleteFile("customers.csv");
    assertEquals(true, isResetted, "Resetted customer list by deletion");
  }

  /**
   * Teardown.
   */
  @AfterAll
  public static void teardown() {
    // Restore customers
    if(customers.size() > 0){
      for (Customer c : customers) handler.addCustomer(c.getName(), c.getContactNumber(), c.getEmailAddress());
      assertEquals(customers.size(), handler.getCustomers().size(), "Restored customer list");
    }

    handler = null;
    assertNull(handler, "Handler instance is null");
    exit(0);
  }

  @Test
  public void testAddCustomer() {
    String name = "Test Testy";
    String contactNumber = "12345678";
    String emailAddress = "tttesttt@mail.com";

    // CASE: Added
    boolean isAdded = true;
    int actualIdx = handler.addCustomer(name, contactNumber, emailAddress);
    boolean output = (actualIdx > -1);
    assertEquals(isAdded, output);
    success("[SUCCESS/TestCustomerHandler.testAddCustomer] Case: Added = PASSED");

    // CASE: Duplicate contactNumber
    isAdded = false;
    actualIdx = handler.addCustomer(name, contactNumber, emailAddress);
    assertEquals(isAdded, (actualIdx > -1));
    success("[SUCCESS/TestCustomerHandler.testAddCustomer] Case: Duplicate contactNumber = PASSED");
  }
}
