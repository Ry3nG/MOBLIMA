import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.function.Try.success;

@Tag("AppTest")
public class AppTest {
  private static App app;
  private static final int MAX_TIMEOUT = 3;

  @BeforeAll
  public static void testInit() {
    app = App.getInstance();
    assertNotNull(app, "App instance was null");
  }

  @Test
  public void testDefault() {
    String[] args = {};
    Runnable runApp = () -> assertDoesNotThrow(() -> this.app.main(args));

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

  @Test
  public void testDebug() {
    String[] args = {"--debug"};
    Runnable runApp = () -> assertDoesNotThrow(() -> this.app.main(args));

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

  @Test
  public void testStaff() {
    String[] args = {"--staff"};
    Runnable runApp = () -> assertDoesNotThrow(() -> this.app.main(args));

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
