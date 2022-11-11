import moblima.control.handlers.CustomerHandler;
import moblima.entities.Customer;
import moblima.utils.datasource.Datasource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Test customer handler.
 */
@Tag("TestCustomerHandler")
public class TestCustomerHandler {
  private static SecureRandom random = new SecureRandom();
  private static CustomerHandler handler;
  private static List<Customer> storedCustomers;

  private static boolean reset() {
    return Datasource.deleteFile("customers.csv");
  }

  private static boolean restore() {
    return Datasource.serializeData(storedCustomers, "customers.csv");
  }

  private static String generateName() {
    return RandomStringUtils.random(5, true, false);
  }

  private static String generateContactNumber() {
    return RandomStringUtils.random(8, false, true);
  }

  private static String generateEmailAddress() {
    return RandomStringUtils.random(5, true, false) + "@mail.com";
  }

  /**
   * Init.
   */
  @BeforeAll
  public static void init() {
    // Instantiate handler
    handler = new CustomerHandler();
    assertNotNull(handler, "Handler instance was null");

    // Retrieve and store existing customers (if any)
    storedCustomers = handler.getCustomers();
    int expected = handler.getCustomers().size();
    int actual = storedCustomers.size();
    assertEquals(expected, actual, "Verifying customer count");

    // Wipe all existing customers
    if (actual > 0) {
      boolean isResetted = reset();
      expected = 0;
      actual = handler.getCustomers().size();
      assertTrue((isResetted == (expected == actual)), "Resetted customer list by deletion");
    }
  }

  @AfterAll
  public static void teardown() {
    // Wipe all existing customers
    int customerCount = handler.getCustomers().size();
    if (customerCount > 0) {
      boolean isResetted = reset();
      assertTrue(isResetted, "Resetted customer list by deletion");
    }

    // Restored existing customers
    int expected = storedCustomers.size();
    if (expected > 0) {
      boolean isRestored = restore();
      assertTrue(isRestored, "Restored customer list");
    }

    // Strip down
    handler = null;
    storedCustomers = null;
    exit(0);
  }

  @Test
  public void testAddCustomer() {
    String name = generateName();
    String contactNumber = generateContactNumber();
    String emailAddress = generateEmailAddress();

    // SUCCESS
    int customerIdx = handler.addCustomer(name, contactNumber, emailAddress);
    assertTrue(customerIdx >= 0, "Check if customer was successfully added");

    // INVALID
    LinkedHashMap<String, List<String>> invalidInputs = new LinkedHashMap<String, List<String>>() {{
      // Duplicate contact and email
      put("Duplicate contact and email", Arrays.asList(contactNumber, emailAddress));
      // Duplicate contact
      put("Duplicate contact", Arrays.asList(contactNumber, generateEmailAddress()));
      // Duplicate email
      put("Duplicate email", Arrays.asList(generateContactNumber(), emailAddress));

      // Invalid contact(s)
      put("Invalid contact(s) - Less than 8 digits", Arrays.asList("1234567", emailAddress));
      put("Invalid contact(s) - alphabets", Arrays.asList("abcdefg", emailAddress));
      put("Invalid contact(s) - alphanumeric", Arrays.asList("abc123", emailAddress));
      put("Invalid contact(s) - w/ special chars", Arrays.asList(";dfdfwe.,", emailAddress));
    }};
    for (Map.Entry<String, List<String>> invalid : invalidInputs.entrySet()) {
      String invalidContactNumber = invalid.getValue().get(0);
      String invalidEmailAddress = invalid.getValue().get(1);
      customerIdx = handler.addCustomer(name, invalidContactNumber, invalidEmailAddress);
      assertFalse(customerIdx >= 0, "Check if customer was not successfully added");
    }
  }

  @Test
  public void testCheckIfAccountExists() {
    String name = generateName();
    String contactNumber = generateContactNumber();
    String emailAddress = generateEmailAddress();

    // Add Customer
    int customerIdx = handler.addCustomer(name, contactNumber, emailAddress);
    assertTrue(customerIdx >= 0, "Check if customer was successfully added");

    // NOT EXISTS
    int expected = -1;
    int actual = handler.checkIfAccountExists(generateContactNumber(), generateEmailAddress());
    assertEquals(expected, actual, "Check if account does not exists");

    // EXISTS
    expected = customerIdx;
    LinkedHashMap<String, List<String>> invalidInputs = new LinkedHashMap<String, List<String>>() {{
      // Correct contact, wrong email
      put("Correct contact, correct email", Arrays.asList(contactNumber, emailAddress));
      // Correct contact, wrong email
      put("Correct contact, wrong email", Arrays.asList(contactNumber, generateEmailAddress()));
      // Wrong contact, correct email
      put("Wrong contact, correct email", Arrays.asList(generateContactNumber(), emailAddress));
    }};
    for (Map.Entry<String, List<String>> invalid : invalidInputs.entrySet()) {
      String invalidContactNumber = invalid.getValue().get(0);
      String invalidEmailAddress = invalid.getValue().get(1);
      actual = handler.checkIfAccountExists(contactNumber, emailAddress);
      assertEquals(expected, actual, "Check if account exists");
    }
  }

  @Test
  public void testGetCustomer() {
    int customerCount = handler.getCustomers().size();
    if (customerCount <= 0) {
      String name = generateName();
      String contactNumber = generateContactNumber();
      String emailAddress = generateEmailAddress();

      // Add Customer
      int customerIdx = handler.addCustomer(name, contactNumber, emailAddress);
      assertTrue(customerIdx >= 0, "Check if customer was successfully added");
    }

    int expected = customerCount - 1;
    Customer expectedCustomer = handler.getCustomers().get(expected);
    Customer actualCustomer = handler.getCustomer(expected);
    int actual = handler.getCustomerIdx(actualCustomer.getId());
    assertEquals(expected, actual, "Check if customers idx is the same");
    assertEquals(expectedCustomer.getId(), actualCustomer.getId(), "Check if customers are the same");
  }

  @Test
  public void testRemoveCustomer() {
    int customerCount = handler.getCustomers().size();
    if (customerCount <= 0) {
      String name = generateName();
      String contactNumber = generateContactNumber();
      String emailAddress = generateEmailAddress();

      // Add Customer
      int customerIdx = handler.addCustomer(name, contactNumber, emailAddress);
      assertTrue(customerIdx >= 0, "Check if customer was successfully added");
    }

    int customerIdx = customerCount - 1;
    Customer customer = handler.getCustomer(customerIdx);

    // SUCCESS
    boolean isRemoved = handler.removeCustomer(customer.getId());
    assertTrue(isRemoved, "Check if customer was successfully removed");

    // INVALID
    customerIdx = Integer.valueOf(generateContactNumber());
    isRemoved = handler.removeCustomer(customerIdx);
    assertFalse(isRemoved, "Check if customer was not successfully removed");
  }
}

