package moblima.boundaries;

import moblima.utils.Helper;
import moblima.utils.Helper.Preset;

import java.util.*;

import static java.lang.System.exit;
import static moblima.utils.Helper.colorizer;

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
//# displayMenuList(): void
  protected void displayMenuList() {
    Helper.logger("Menu.displayMenuList", "Menu: \n" + this.menuMap.keySet());
    this.displayMenuList(this.menuMap.keySet().stream().toList());
  }

  /**
   * Display menu list.
   *
   * @param menuList the menu list
   */
//# displayMenuList(menuList:List<String>):void
  protected void displayMenuList(List<String> menuList) {
    if (menuList.size() < 1) exit(0);
    Helper.logger("Menu.displayMenuList", "MENU LIST: " + menuMap.keySet());

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
//# displayMenu(): void
  protected void displayMenu() {
    Helper.logger("Menu.displayMenu", "Displaying menu . . .");

    int menuChoice = -1;
    int lastChoice = menuMap.size() - 1;
    scanner = new Scanner(System.in);
    while (menuChoice != lastChoice) {
      List<String> menuList = this.menuMap.keySet().stream().toList();
      menuChoice = this.getListSelectionIdx(menuList, true);

      // Display selection choice
      if (menuChoice != menuMap.size()) System.out.print("\t>>> " + menuList.get(menuChoice) + "\n");
      menuMap.get(menuList.get(menuChoice)).run();

      // Await continue
      if (menuChoice != lastChoice) {
        this.awaitContinue();
      }
    }
  }

  /**
   * Await continue.
   */
  public void awaitContinue() {
    try {
      System.out.println(colorizer("Press any key to continue . . .", Preset.LOG));
      System.in.read();
    } catch (Exception e) {

    }
  }

  /**
   * Gets list selection idx.
   *
   * @param list the list
   * @return the list selection idx
   */
//# getListSelectionIdx(list:List) : int
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
//# getListSelectionIdx(list:List, showMenuList:boolean) : int
  protected int getListSelectionIdx(List list, boolean showMenuList) {
    int menuChoice = 0;
    scanner = new Scanner(System.in);
    while (menuChoice != (list.size() - 1)) {
      try {
        if (showMenuList) displayMenuList();
        System.out.print("SELECTED: ");

        if (scanner.hasNextInt()) { // if the next in buffer is int
          menuChoice = scanner.nextInt();
          menuChoice -= 1;
          if (menuChoice < 0)
            throw new IllegalArgumentException("[ERROR] Negative value - input must be a positive integer");
          else if (menuChoice > list.size())
            throw new IllegalArgumentException("[ERROR] Invalid menu selection - input must be between 1 and " + list.size());
        } else { // next in buffer is not int
          scanner.next(); // clear buffer
          throw new InputMismatchException("[ERROR] Invalid non-numerical value - input must be an integer");
        }

        Helper.logger("Menu.getListSelectionIdx", "MENU CHOICE: " + menuChoice);
        return menuChoice;
      } catch (Exception e) {
        String errMsg = !e.getMessage().isEmpty() ? e.getMessage() : "[ERROR] Invalid menu input - input must be of " + Arrays.toString(list.toArray());
        System.out.println(colorizer(errMsg, Preset.ERROR));

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
//# refreshMenu(menuMap: LinkedHashMap<String, Runnable>) :void
  protected void refreshMenu(LinkedHashMap<String, Runnable> menuMap) {
    Helper.logger("Menu.refreshMenu", menuMap.keySet().toString());
    this.menuMap = menuMap;
  }

  /**
   * Show menu.
   */
//+ showMenu():void
  public abstract void showMenu();
}
