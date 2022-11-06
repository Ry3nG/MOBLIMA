package moblima.utils;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Global environment variables and debug mode toggle
 *
 * @author SC2002 /SS11 Group 1
 * @version 1.0
 */
public class Constants {
  /**
   * Debug flag
   */
  public static boolean DEBUG_MODE = false;
  /**
   * Ledger of environment variables / secrets
   */
  private static Map<String, String> entries;
  /**
   * Singleton instance of Constants
   */
  private static Constants _instance = null;

  /**
   * Default constructor
   */
  private Constants() {
    this.loadEnv();
  }

  /**
   * Retrieves instance of Constants
   *
   * @return instance :current Constants instance
   */
  public static Constants getInstance() {
    if (_instance == null) _instance = new Constants();
    return _instance;
  }

  /**
   * Configure debug mode
   *
   * @param isDebug :boolean
   */
  public static void setDebugMode(boolean isDebug) {
    DEBUG_MODE = isDebug;
  }

  /**
   * Retrieves specific env key from loaded entries
   *
   * @param envKey :String
   * @return String | null
   */
  public static String getEnv(String envKey) {
    _instance = getInstance();
    return entries.get(envKey);
  }

  /**
   * Load enviroment variables from project .env file
   *
   * @return boolean
   */
  private boolean loadEnv() {
    boolean isLoaded = false;
    try {
      Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().filename(".env").load();

      entries = new HashMap<String, String>();
      for (DotenvEntry e : dotenv.entries()) {
        entries.put(e.getKey(), e.getValue());
      }

      isLoaded = true;
    } catch (Exception e) {
      Helper.logger("Constants.loadEnv", "No env found");
    }
    return isLoaded;
  }
}
