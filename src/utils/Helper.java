package utils;

import com.diogonunes.jcolor.Attribute;
import com.github.lalyos.jfiglet.FigletFont;

import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;

/**
 * Reusable functions and presets
 *
 * @author Crystal Cheong
 */
public class Helper {

  /**
   * Strictly debug logger, hides when DEBUG_MODE != 1
   *
   * @param methodName:String, msg:String
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
   * Clone helper of colorize
   *
   * @param text:String
   * @param preset:Preset
   * @return colorizedText:String
   */
  public static String colorizer(String text, Preset preset) {
    Attribute color = preset.color;
    return colorize(text, color);
  }

  /**
   * Parse string to integer values.
   *
   * @param inputStr:String
   * @return -1 if failure
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
   * Pretty-print table
   *
   * @param rows:List<List<String>> rows
   * @return tableStr:String
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
   * Figlet font printer for single-line text
   *
   * @param lineText:String
   */
  public static void figPrint(String lineText) {
    try {
      String asciiArt = FigletFont.convertOneLine(lineText);
      System.out.println(asciiArt);
    } catch (Exception e) {
    }
  }


  /**
   * Helper method to check validity of price input
   *
   * @param input:String      - input obained from Staff
   * @param checkZero:boolean - whether it is required to check for zero
   * @return price:double
   * @since 1.1
   */
  public static double checkPriceInput(String input, boolean checkZero) {
    if (input.equals("-")) return -1; // Staff does not want to change
    try {
      double price = Double.parseDouble(input);
      if (price <= 0 && checkZero) { // 0 or less
        System.out.println("[ERROR] Please enter a price that is more than SGD 0, or - to keep the current price.");
        return -2;
      } else if (price < 0) { // less than 0
        System.out.println("[ERROR] Please enter a price that is more than or equal to SGD 0, or - to keep the current price.");
        return -2;
      } else return price; // valid
    } catch (NumberFormatException e) { // characters other than -
      System.out.println("[ERROR] Please enter integers and decimal point (if needed) only, or - to keep the current price.");
      return -2;
    }
  }

  /**
   * Helper method to check validity of price input
   *
   * @param input:String - input obtained from Staff
   * @return 0 - if the price entered is valid
   * @return -2 - if the price entered is invalid (incl. non-digits)
   */
  public static int checkNoCharacters(String input) {
    if (input.equals("-")) return -1; // Staff does not want to change
    try {
      Double.parseDouble(input);
      return 0;
    } catch (NumberFormatException e) { // characters other than -
      System.out.println("Please enter integers and decimal point (if needed) only, or - to keep the current price.");
      return -2;
    }
  }

  public enum Preset {
    LOG(Attribute.TEXT_COLOR(240)),
    WARNING(Attribute.TEXT_COLOR(208)),
    ERROR(Attribute.TEXT_COLOR(160)),
    SUCCESS(Attribute.TEXT_COLOR(118)),
    DEFAULT(Attribute.TEXT_COLOR(7)),
    HIGHLIGHT(Attribute.TEXT_COLOR(200));

    public final Attribute color;

    Preset(Attribute attr) {
      this.color = attr;
    }
  }
}
