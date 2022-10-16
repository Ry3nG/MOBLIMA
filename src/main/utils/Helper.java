package main.utils;

public class Helper {

  /**
   * Strictly debug logger, hides when DEBUG_MODE != 1
   *
   * @param methodName:String, msg:String
   * @return void
   */
  public static void logger(
      String methodName,
      String msg
  ) {
    if (!Constants.DEBUG_MODE) return;
    System.out.println("[LOG/" + methodName + "] " + msg);
  }

  /**
   * @return -1 if failure
   * @description Parse string to integer values.
   * @params inputStr:String
   */
  public static int parseStrToInt(
      String inputStr
  ) {
    int result = -1;
    boolean isNumericString = inputStr != null && inputStr.matches("[0-9.]+");

    try {
      if (!isNumericString) throw new NumberFormatException("Unable to parse non-numeric input string");
      Integer number = Integer.valueOf(inputStr);
      result = number;
    } catch (NumberFormatException ex) {
      ex.printStackTrace();
    }
    return result;
  }
}
