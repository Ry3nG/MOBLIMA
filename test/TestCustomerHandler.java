import moblima.control.handlers.CustomerHandler;
import moblima.entities.Customer;
import moblima.utils.datasource.Datasource;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.function.Try.success;

@Tag("TestCustomerHandler")
public class TestCustomerHandler {
  private static CustomerHandler handler;
  private static List<Customer> customers;
  private static Customer testCustomer;

  /**
   * Init.
   */
  @BeforeAll
  public static void init() {
    handler = new CustomerHandler();
    customers = handler.getCustomers();
    assertNotNull(handler, "Handler instance was null");

    // Store existing customer list
    int customerCount = handler.getCustomers().size();
    int storedCustomerCount = customers.size();
    assertEquals(storedCustomerCount, customerCount, "Customers: " + customers.size());
  }


  @BeforeEach
  public void reset() {
    // Wipe all existing customers
    boolean isResetted = Datasource.deleteFile("customers.csv");
    assertTrue(isResetted, "Resetted customer list by deletion");
    int customerCount = handler.getCustomers().size();
    assertTrue((customerCount == 0), "Customer list is empty");

    // Initialize test object
    testCustomer = new Customer("tttesttt", "12345678", "tttesttt@mail.com");
  }


  @AfterEach
  public void restore() {
    // Wipe all
    reset();

    int customerCount = handler.getCustomers().size();
    int storedCustomerCount = customers.size();

    if (customers.size() > 0) {
      // Restore customers
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
    testCustomer = null;
    customers = null;
    assertNull(handler, "Handler instance is null");
    assertNull(testCustomer, "Test object is null");
    assertNull(customers, "Test object is null");
    exit(0);
  }

  @Test
  @Order(0)
  public void testAddCustomer() {
    int customerCount = handler.getCustomers().size();
    String name = testCustomer.getName();
    String contactNumber = testCustomer.getContactNumber();
    String emailAddress = testCustomer.getEmailAddress();

    // CASE: Added
    boolean isAdded = true;
    int expectedIdx = customerCount + 1;
    int actualIdx = handler.addCustomer(name, contactNumber, emailAddress);
    boolean output = (actualIdx == expectedIdx);
    customerCount = handler.getCustomers().size();
    assertEquals(isAdded, output);
    assertEquals(expectedIdx, handler.getCustomerIdx(handler.getCurrentCustomer().getId()));
    success("[SUCCESS/TestCustomerHandler.testAddCustomer] Case: Added = PASSED");


    // Case: Invalids
    isAdded = false;
    LinkedHashMap<String, List<String>> invalidInputs = new LinkedHashMap<String, List<String>>() {{
      // Duplicate contact and email
      put("Duplicate contact and email", Arrays.asList(contactNumber, emailAddress));
      // Duplicate contact
      put("Duplicate contact", Arrays.asList(contactNumber, "ttestt@mail.com"));
      // Duplicate email
      put("Duplicate email", Arrays.asList("12345679", emailAddress));

      // Invalid contact(s)
      put("Invalid contact(s) - Less than 8 digits", Arrays.asList("1234567", emailAddress));
      put("Invalid contact(s) - alphabets", Arrays.asList("abcdefg", emailAddress));
      put("Invalid contact(s) - alphanumeric", Arrays.asList("abc123", emailAddress));
      put("Invalid contact(s) - w/ special chars", Arrays.asList(";dfdfwe.,", emailAddress));
    }};

    for (Map.Entry<String, List<String>> invalid : invalidInputs.entrySet()) {
      String invalidContactNumber = invalid.getValue().get(0);
      String invalidEmailAddress = invalid.getValue().get(1);
      actualIdx = handler.addCustomer(name, invalidContactNumber, invalidEmailAddress);
      assertEquals(isAdded, (actualIdx > -1));
      success("[SUCCESS/TestCustomerHandler.testAddCustomer] Case: " + invalid.getKey() + "  = PASSED");
    }
  }
}
