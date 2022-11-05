package utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * App-global environment variables and debug mode toggle
 *
 * @author Crystal Cheong
 */
public class Constants {
  public static boolean DEBUG_MODE = false;
  private static Map<String, String> entries;
  private static Constants _instance = null;

  public Constants() {
    this.loadEnv();
  }

  public static Constants getInstance() {
    if (_instance == null) _instance = new Constants();
    return _instance;
  }

  /**
   * Configure debug mode
   *
   * @param isDebug:boolean
   */
  public static void setDebugMode(
      boolean isDebug
  ) {
    DEBUG_MODE = isDebug;
  }

  /**
   * Retrieves specific env key from loaded entries
   *
   * @param envKey:String
   * @return String | null
   * @author Crystal Cheong
   */
  public static String getEnv(
      String envKey
  ) {
    _instance = getInstance();
    return entries.get(envKey);
  }

  /**
   * Load enviroment variables from project .env file
   *
   * @return boolean
   * @author Crystal Cheong
   */
  private boolean loadEnv() {
    boolean isLoaded = false;
    try {
      Dotenv dotenv = Dotenv.configure()
          .ignoreIfMalformed()
          .ignoreIfMissing()
          .filename(".env")
          .load();

      entries = new HashMap<String, String>();
      for (DotenvEntry e : dotenv.entries()) {
        entries.put(e.getKey(), e.getValue());
      }

      isLoaded = true;
    } catch (Exception e) {
      e.getStackTrace();
    }
    return isLoaded;
  }
}
