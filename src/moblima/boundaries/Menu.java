package moblima.boundaries;

import moblima.utils.Helper;
import moblima.utils.Helper.Preset;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.System.exit;
import static moblima.utils.Helper.colorPrint;
import static moblima.utils.deserializers.LocalDateDeserializer.dateFormatter;
import static moblima.utils.deserializers.LocalDateTimeDeserializer.dateTimeFormatter;

/**
 * The type Menu.
 */
public abstract class Menu {
  /**
   * The constant scanner.
   */
  protected static Scanner scanner = new Scanner(System.in);
  /**
   * The Menu map.
   */
  protected LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();

  /**
   * Display menu list.
   */
// # displayMenuList(): void
  protected void displayMenuList() {
    Helper.logger("Menu.displayMenuList", "Menu: \n" + this.menuMap.keySet());
    this.displayMenuList(this.menuMap.keySet().stream().toList());
  }

  /**
   * Display menu list.
   *
   * @param menuList the menu list
   */
// # displayMenuList(menuList:List<String>):void
  protected void displayMenuList(List<String> menuList) {
    if (menuList.size() < 1)
      exit(0);
//    Helper.logger("Menu.displayMenuList", "MENU LIST: " + menuMap.keySet());

    int menuIdx = 1;
    System.out.println("---------------------------------------------------------------------------");
    for (String key : menuList) {
      System.out.println("(" + menuIdx + ") " + key);
      menuIdx++;
    }
    System.out.println("---------------------------------------------------------------------------");
  }

  /**
   * Display menu.
   */
// # displayMenu(): void
  protected void displayMenu() {
    Helper.logger("Menu.displayMenu", "Displaying menu . . ." + this.menuMap);

    int menuChoice = 0;
    while (menuChoice > -1) {
      scanner = new Scanner(System.in);

      List<String> menuList = this.menuMap.keySet().stream().toList();
      menuChoice = this.getListSelectionIdx(menuList);

      // Display selection choice
      System.out.print("\t>>> " + menuList.get(menuChoice) + "\n");
//      this.menuMap.get(menuList.get(menuChoice)).run();

      int lastChoice = this.menuMap.size() - 1;
      if (menuChoice != lastChoice)
        System.out.print("\t>>> " + menuList.get(menuChoice) + "\n");
      this.menuMap.get(menuList.get(menuChoice)).run();

      // Await continue
      if (menuChoice != lastChoice)
        this.awaitContinue();
      else menuChoice = -1;
    }
  }

  /**
   * Await continue.
   */
  public void awaitContinue() {
    try {
      scanner = new Scanner(System.in);
      colorPrint("Press any key to continue . . .", Preset.LOG);
      System.in.read();
    } catch (Exception e) {

    }
  }

  /**
   * Sets date.
   *
   * @param promptMsg  the prompt msg
   * @param onlyFuture the only future
   * @return the date
   */
  public LocalDate setDate(String promptMsg, boolean onlyFuture) {
    LocalDate date = null;

    while (date == null) {
      System.out.print(promptMsg);
      scanner = new Scanner(System.in).useDelimiter("\n");
      try {
        String strDate = scanner.next().trim();
        if (strDate.matches("^\\d{2}-\\d{2}-\\d{4}")) {
          date = LocalDate.parse(strDate, dateFormatter);

          // VALIDATION: Present or future dates
          if (onlyFuture && date.isBefore(LocalDate.now())) {
            throw new Exception("Given date must not be in the past");
          }
        } else
          throw new Exception("Invalid input, expected format (dd-MM-yyyy)");
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);
        date = null;
      }
    }

    return date;
  }

  /**
   * Sets date time.
   *
   * @param promptMsg  the prompt msg
   * @param onlyFuture the only future
   * @return the date time
   */
  public LocalDateTime setDateTime(String promptMsg, boolean onlyFuture) {
    LocalDateTime datetime = null;

    while (datetime == null) {
      System.out.print(promptMsg);
      scanner = new Scanner(System.in).useDelimiter("\n");
      try {
        String strDateTime = scanner.next().trim();
        if (strDateTime.matches("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}[AP]M$")) {
          datetime = LocalDateTime.parse(strDateTime, dateTimeFormatter);

          // VALIDATION: Present or future dates
          if (onlyFuture && datetime.isBefore(LocalDateTime.now())) {
            throw new Exception("Given datetime must not be in the past");
          }
        } else
          throw new Exception("Invalid input, expected format (dd-MM-yyyy hh:mma)");
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);
        datetime = null;
      }
    }

    return datetime;
  }

  /**
   * Sets int.
   *
   * @param promptMsg the prompt msg
   * @return the int
   */
  public int setInt(String promptMsg) {
    int val = -1;
    while (val < 0) {
      System.out.print(promptMsg);
      scanner = new Scanner(System.in).useDelimiter("\n");
      try {
        if (scanner.hasNextInt()) { // if the next in buffer is int
          val = scanner.nextInt();
          if (val <= 0)
            throw new IllegalArgumentException("Negative value - input must be a positive integer");
        } else { // next in buffer is not int
          scanner.next(); // clear buffer
          throw new InputMismatchException("Invalid non-numerical value - input must be an integer");
        }
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);
        val = -1;
      }
    }
    return val;
  }

  /**
   * Sets double.
   *
   * @param promptMsg the prompt msg
   * @return the double
   */
  public Double setDouble(String promptMsg) {
    Double val = null;
    while (val == null) {
      System.out.print(promptMsg);
      scanner = new Scanner(System.in).useDelimiter("\n");

      try {
        String input = scanner.next().trim();
        if (input.isEmpty() || input.isBlank()) throw new InputMismatchException("Input cannot be blank");

        val = Double.parseDouble(input);
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);
        val = null;
      }
    }
    return val;
  }

  /**
   * Sets string.
   *
   * @param varname   the varname
   * @param promptMsg the prompt msg
   * @return the string
   */
  public String setString(String varname, String promptMsg) {
    String text = null;
    while (text == null) {
      System.out.print(varname);
      text = scanner.next().trim();

      if (text.isEmpty() || text.isBlank()) {
        text = null;
        colorPrint(promptMsg, Preset.ERROR);
      }
    }
    return text;
  }

  /**
   * Print changes.
   *
   * @param label      the label
   * @param isSame     the is same
   * @param prevStatus the prev status
   * @param curStatus  the cur status
   */
  public void printChanges(String label, boolean isSame, String prevStatus, String curStatus) {
    if (isSame) {
      colorPrint("/ NO CHANGE - " + label + ": " + prevStatus, Preset.WARNING);
    } else {
      colorPrint(label + ": " + prevStatus + " -> " + curStatus, Preset.SUCCESS);
    }
  }

  /**
   * Gets list selection idx.
   *
   * @param list the list
   * @return the list selection idx
   */
// # getListSelectionIdx(list:List) : int
  protected int getListSelectionIdx(List list) {
    return getListSelectionIdx(list, true);
  }

  /**
   * Gets list selection idx.
   *
   * @param list         the list
   * @param showMenuList the show menu list
   * @return the list selection idx
   */
// # getListSelectionIdx(list:List, showMenuList:boolean) : int
  protected int getListSelectionIdx(List list, boolean showMenuList) {
    int menuChoice = 0;
    int lastChoice = list.size() - 1;
    scanner = new Scanner(System.in);
    while (menuChoice != lastChoice) {
      try {
        if (showMenuList)
          displayMenuList();
        System.out.print("SELECTED: ");

        if (scanner.hasNextInt()) { // if the next in buffer is int
          menuChoice = scanner.nextInt();
          menuChoice -= 1;
          if (menuChoice < 0)
            throw new IllegalArgumentException("Negative value - input must be a positive integer");
          else if (menuChoice > lastChoice)
            throw new IllegalArgumentException(
                "Invalid menu selection - input must be between 1 and " + list.size());
        } else {
          // clear buffer
          scanner.next();
          throw new InputMismatchException("Invalid non-numerical value - input must be an integer");
        }

        Helper.logger("Menu.getListSelectionIdx", "MENU CHOICE: " + menuChoice + " / " + lastChoice);
        return menuChoice;
      } catch (Exception e) {
        String errMsg = !e.getMessage().isEmpty() ? e.getMessage()
            : "Invalid menu input - input must be of " + Arrays.toString(list.toArray());
        colorPrint(errMsg, Preset.ERROR);

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      }
    }
    return menuChoice;
  }

  /**
   * Refresh menu.
   *
   * @param menuMap the menu map
   */
// # refreshMenu(menuMap: LinkedHashMap<String, Runnable>) :void
  protected void refreshMenu(LinkedHashMap<String, Runnable> menuMap) {
//    Helper.logger("Menu.refreshMenu", menuMap.keySet().toString());
    this.menuMap = menuMap;
  }

  /**
   * Show menu.
   */
// + showMenu():void
  public abstract void showMenu();
}
