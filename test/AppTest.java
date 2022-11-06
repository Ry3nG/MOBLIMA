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
@Tag("AppTest")
public class AppTest {
  private static final int MAX_TIMEOUT = 3;
  private static App app;

  /**
   * Init.
   */
  @BeforeAll
  public static void init() {
    app = App.getInstance();
    assertNotNull(app, "moblima.App instance was null");
  }

  /**
   * Teardown.
   */
  @AfterAll
  public static void teardown() {
    app = null;
    assertNull(app, "moblima.App instance is null");
    exit(0);
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
      success("[SUCCESS/AppTest.testDefault] Executor timed out as expected");
    } catch (Exception ex) {
      fail("[FAIL/AppTest.testDefault] Executor did not timeout as expected");
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
      success("[SUCCESS/AppTest.testDebug] Executor timed out as expected");
    } catch (Exception ex) {
      fail("[FAIL/AppTest.testDebug] Executor did not timeout as expected");
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
      success("[SUCCESS/AppTest.testStaff] Executor timed out as expected");
    } catch (Exception ex) {
      fail("[FAIL/AppTest.testStaff] Executor did not timeout as expected");
    }
  }
}
