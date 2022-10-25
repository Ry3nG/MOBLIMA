package boundary;

import utils.Helper;

import java.util.*;

import static java.lang.System.exit;

public abstract class Menu {
  protected static Scanner scanner = new Scanner(System.in);
  protected LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();

  /**
   * Pretty-printer for menu list
   */
  protected void displayMenuList() {
    this.displayMenuList(this.menuMap.keySet().stream().toList());
  }

  /**
   * Display menu list options
   *
   * @param menuList:List<String>
   */
  //# displayMenuList(menuList:List<String>):void
  protected void displayMenuList(List<String> menuList) {
    if (menuList.size() < 1) exit(0);
    Helper.logger("Menu.displayMenuList", "MENU LIST: " + Arrays.deepToString(menuList.toArray()));

    int menuIdx = 1;
    System.out.println("---------------------------------------------------------------------------");
    for (String key : menuList) {
      System.out.println("(" + menuIdx + ") " + key);
      menuIdx++;
    }
    System.out.println("---------------------------------------------------------------------------");
  }

  /**
   * Display menu and executes the mapped action
   */
  //# displayMenu(): void
  protected void displayMenu() {
    int menuChoice = 0;
    scanner = new Scanner(System.in);
    while ((menuChoice != menuMap.size())) {
      try {
        displayMenuList();
        System.out.print("SELECTED: ");

        if (scanner.hasNextInt()) { // if the next in buffer is int
          menuChoice = scanner.nextInt();
          if ((menuChoice - 1) < -1)
            throw new IllegalArgumentException("[ERROR] Negative value - input must be a positive integer");
          else if (menuChoice > menuMap.size() || menuChoice == 0)
            throw new IllegalArgumentException("[ERROR] Invalid menu selection - input must be between 1 and " + menuMap.size());
        } else { // next in buffer is not int
          scanner.next(); // clear buffer
          throw new InputMismatchException("[ERROR] Invalid non-numerical value - input must be an integer");
        }

        if (menuChoice != menuMap.size())
          System.out.print("\t>>> " + menuMap.keySet().toArray()[menuChoice - 1] + "\n");
        menuMap.get(menuMap.keySet().toArray()[menuChoice - 1]).run();
        if (menuChoice != menuMap.size()) {
          System.out.println("Press any key to continue . . .");
          System.in.read();
        }
      } catch (Exception e) {
        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      }
    }
  }

  /**
   * Obtain user selection input of list option and display menu list
   *
   * @param list:List
   * @return menuChoice:int
   */
  //# getListSelectionIdx(list:List) : int
  protected int getListSelectionIdx(List list) {
    return getListSelectionIdx(list, true);
  }

  /**
   * Obtain user selection input of list options
   *
   * @param list:List
   * @param showMenuList:boolean
   * @return menuChoice:int
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
      } catch (NoSuchElementException e) {
        System.out.println("[NIL]\nApplication is terminated via CTRL + C");
        System.exit(1);
      } catch (Exception e) {
        System.out.println("[ERROR] Invalid menu input - input must be of " + Arrays.toString(list.toArray()));
        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      }
    }
    return menuChoice;
  }

  /**
   * Remap menu to new map
   *
   * @param menuMap:LinkedHashMap<String, Runnable>
   */
  //+ refreshMenu(menuMap: LinkedHashMap<String, Runnable>) :void
  public void refreshMenu(LinkedHashMap<String, Runnable> menuMap) {
    Helper.logger("Menu.refreshMenu", menuMap.keySet().toString());
    this.menuMap = menuMap;
  }

  /**
   * Abstract method for showing menu
   */
  public abstract void showMenu();
}
