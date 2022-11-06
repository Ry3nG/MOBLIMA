package moblima.utils;

/**
 * The type Catcher.
 */
public class Catcher extends Exception {
  /**
   * Instantiates a new Catcher.
   *
   * @param methodName the method name
   * @param msg        the msg
   */
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
