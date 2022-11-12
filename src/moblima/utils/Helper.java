package moblima.utils;

import com.diogonunes.jcolor.Attribute;
import com.github.lalyos.jfiglet.FigletFont;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * The type Helper.
 */
public class Helper {


  /**
   * Format price string.
   *
   * @param price the price
   * @return the string
   */
  public static String formatPrice(double price) {
    DecimalFormat df = new DecimalFormat("0.00");
    return "SGD " + df.format(price);
  }

  /**
   * Logger.
   *
   * @param methodName the method name
   * @param msg        the msg
   */
  public static void logger(
      String methodName,
      String msg
  ) {
    if (!Constants.DEBUG_MODE) return;
    String outputStr = "[LOG/" + methodName + "] " + msg;
    System.out.println(colorize(outputStr, Preset.LOG.color));
  }

  /**
   * Colorizer string.
   *
   * @param text   the text
   * @param preset the preset
   * @return the string
   */
  public static String colorizer(String text, Preset preset) {
    Attribute color = preset.color;
    return colorize(text, color);
  }

  /**
   * Parse str to int int.
   *
   * @param inputStr the input str
   * @return the int
   */
  public static int parseStrToInt(
      String inputStr
  ) {
    int result = -1;
    boolean isNumericString = inputStr != null && inputStr.matches("[0-9.]+");

    try {
      if (!isNumericString) throw new NumberFormatException("Unable to parse non-numeric input string");
      result = Integer.parseInt(inputStr);
    } catch (NumberFormatException ex) {
      logger("Helper.parseStrToInt", ex.getMessage());
      return result;
    }
    return result;
  }

  /**
   * Format as table string.
   *
   * @param rows the rows
   * @return the string
   */
  public static String formatAsTable(List<List<String>> rows) {
    int[] maxLengths = new int[rows.get(0).size()];
    for (List<String> row : rows) {
      for (int i = 0; i < row.size(); i++)
        maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());

    }

    StringBuilder formatBuilder = new StringBuilder();
    for (int maxLength : maxLengths)
      formatBuilder.append("%-").append(maxLength + 2).append("s");

    String format = formatBuilder.toString();

    StringBuilder result = new StringBuilder();
    for (List<String> row : rows)
      result.append(String.format(format, (String[]) row.toArray(new String[0]))).append("\n");

    return result.toString();
  }

  /**
   * Fig print.
   *
   * @param lineText the line text
   */
  public static void figPrint(String lineText) {
    try {
      String asciiArt = FigletFont.convertOneLine(lineText);
      System.out.println(asciiArt);
    } catch (Exception e) {
    }
  }

  /**
   * Generate digits int.
   *
   * @param digitCount the digit count
   * @return the int
   */
  public static int generateDigits(int digitCount) {
    if (digitCount < 1) return 0;

    SecureRandom rnd = new SecureRandom();
    int number = rnd.nextInt(999999);

    return Integer.valueOf(String.format("%0" + digitCount + "d", number));
  }


  /**
   * Check price input double.
   *
   * @param input the input
   * @return the double
   */
  public static double checkPriceInput(String input) {
    if (input.equals("-")) return -1; // Staff does not want to change
    Double price = null;
    while (price == null) {
      try {
        price = Double.parseDouble(input);
        break;
      } catch (NumberFormatException e) { // characters other than -
        System.out.println("[ERROR] Please enter integers and decimal point (if needed) only, or - to keep the current price.");
        price = null;
      }
    }

    return price;
  }


  /**
   * The enum Preset.
   */
  public enum Preset {
    /**
     * Log preset.
     */
    LOG(Attribute.TEXT_COLOR(240)),
    /**
     * Warning preset.
     */
    WARNING(Attribute.TEXT_COLOR(208)),
    /**
     * Error preset.
     */
    ERROR(Attribute.TEXT_COLOR(160)),
    /**
     * Success preset.
     */
    SUCCESS(Attribute.TEXT_COLOR(118)),
    /**
     * Default preset.
     */
    DEFAULT(Attribute.TEXT_COLOR(7)),
    /**
     * Highlight preset.
     */
    HIGHLIGHT(Attribute.TEXT_COLOR(200));

    /**
     * The Color.
     */
    public final Attribute color;

    Preset(Attribute attr) {
      this.color = attr;
    }
  }
}
