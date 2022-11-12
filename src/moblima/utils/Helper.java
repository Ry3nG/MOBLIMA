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
  public static void logger(String methodName, String msg) {
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
  private static String colorizer(String text, Preset preset) {
    Attribute color = preset.color;
    return colorize(text, color);
  }

  /**
   * Color print.
   *
   * @param text   the text
   * @param preset the preset
   */
  public static void colorPrint(String text, Preset preset){
    Attribute color = preset.color;
    String tag = preset.tag;

    String content = tag + text;
    System.out.println(colorize(content, color));
  }

  /**
   * Parse str to int.
   *
   * @param inputStr the input str
   * @return the int
   */
  public static int parseStrToInt(String inputStr) {
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
   * The enum Preset.
   */
  public enum Preset {
    /**
     * Log preset.
     */
    LOG(Attribute.TEXT_COLOR(240), ""),
    /**
     * Warning preset.
     */
    WARNING(Attribute.TEXT_COLOR(208), "[⚠️WARNING] "),
    /**
     * Error preset.
     */
    ERROR(Attribute.TEXT_COLOR(160), "[❌ERROR] "),
    /**
     * Success preset.
     */
    SUCCESS(Attribute.TEXT_COLOR(118), "[✅SUCCESS] "),
    /**
     * Default preset.
     */
    DEFAULT(Attribute.TEXT_COLOR(7), ""),
    /**
     * Current preset.
     */
    CURRENT(Attribute.TEXT_COLOR(190), "[CURRENT] "),
    /**
     * Highlight preset.
     */
    HIGHLIGHT(Attribute.TEXT_COLOR(200), "");

    /**
     * The Color.
     */
    public final Attribute color;
    /**
     * The Tag.
     */
    public final String tag;

    Preset(Attribute attr, String tagDisplay) {

      this.color = attr;
      this.tag = tagDisplay;
    }
  }
}
