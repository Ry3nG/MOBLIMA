import moblima.App;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.System.exit;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.function.Try.success;

/**
 * The type App test.
 */
@Tag("TestApp")
public class TestApp {
  private static final int MAX_TIMEOUT = 3;
  private static App app;

  /**
   * Init.
   */
  @BeforeAll
  public static void init() {
    app = App.getInstance();
    assertNotNull(app, "App instance was null");
  }

  /**
   * Teardown.
   */
  @AfterAll
  public static void teardown() {
    app = null;
    assertNull(app, "App instance is null");
  }

  /**
   * Test default.
   */
  @Test
  public void testDefault() {
    String[] args = {};
    Runnable runApp = () -> assertDoesNotThrow(() -> App.main(args));

    Future<String> executor
        = (Future<String>) Executors.newSingleThreadExecutor().submit(runApp);
    try {
      executor.get(MAX_TIMEOUT, TimeUnit.SECONDS);
    } catch (TimeoutException ex) {
      executor.cancel(true);
      success("[SUCCESS/TestApp.testDefault] Executor timed out as expected");
    } catch (Exception ex) {
      fail("[FAIL/TestApp.testDefault] Executor did not timeout as expected");
    }
  }

  /**
   * Test debug.
   */
  @Test
  public void testDebug() {
    String[] args = {"--debug"};
    Runnable runApp = () -> assertDoesNotThrow(() -> App.main(args));

    Future<String> executor
        = (Future<String>) Executors.newSingleThreadExecutor().submit(runApp);
    try {
      executor.get(MAX_TIMEOUT, TimeUnit.SECONDS);
    } catch (TimeoutException ex) {
      executor.cancel(true);
      success("[SUCCESS/TestApp.testDebug] Executor timed out as expected");
    } catch (Exception ex) {
      fail("[FAIL/TestApp.testDebug] Executor did not timeout as expected");
    }
  }

  /**
   * Test staff.
   */
  @Test
  public void testStaff() {
    String[] args = {"--staff"};
    Runnable runApp = () -> assertDoesNotThrow(() -> App.main(args));

    Future<String> executor
        = (Future<String>) Executors.newSingleThreadExecutor().submit(runApp);
    try {
      executor.get(MAX_TIMEOUT, TimeUnit.SECONDS);
    } catch (TimeoutException ex) {
      executor.cancel(true);
      success("[SUCCESS/TestApp.testStaff] Executor timed out as expected");
    } catch (Exception ex) {
      fail("[FAIL/TestApp.testStaff] Executor did not timeout as expected");
    }
  }
}
