package utils;

/**
 * Custom exception block for early non-error try/catch exits
 *
 * @author Crystal Cheong
 */
public class Catcher extends Exception {
  public Catcher(
      String methodName,
      String msg
  ) {
    super(msg);
    if (Constants.DEBUG_MODE) {
      Helper.logger(methodName + "/Catcher", msg);
    }
  }
}