package entity;

import utils.Helper;

import java.util.*;

import static java.lang.System.exit;

public abstract class Menu {
  protected static Scanner scanner = new Scanner(System.in);
  protected LinkedHashMap<String, Runnable> menuMap;
  protected static final char QUIT_CODE = 'q';

  /**
   * Initializes the Menu object
   * - menuMap: contains the menu label and actions
   */
  public Menu() {
    this.menuMap = new LinkedHashMap<String, Runnable>();
  }

  /**
   * Pretty-printer for menu list
   */
  protected void displayMenuList() {
    this.displayMenuList(this.menuMap.keySet().stream().toList());
  }

  protected void displayMenuList(List<String> menuList) {
    if (menuList.size() < 1) exit(0);

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
      }
//      catch (NoSuchElementException e) {
//        System.out.println("[NIL]\nApplication is terminated via CTRL + C");
//        System.exit(1);
//      }
      catch (Exception e) {
//        System.out.println("[ERROR] Invalid menu input - input must be of " + Arrays.toString(menuMap.keySet().toArray()));
        scanner.nextLine();
        scanner = new Scanner(System.in);
//        scanner.nextLine();
      }
    }
  }

  protected int getListSelectionIdx(List list) {
    return getListSelectionIdx(list, true);
  }

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
            throw new IllegalArgumentException("[ERROR] Invalid menu selection - input must be between 1 and " + (list.size() + 1));
        } else { // next in buffer is not int
          scanner.next(); // clear buffer
          throw new InputMismatchException("[ERROR] Invalid non-numerical value - input must be an integer");
        }

        return menuChoice;
      } catch (java.util.InputMismatchException e) {
        System.out.println("[ERROR] Invalid non-numerical value - input must be an integer");
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
      } catch (NoSuchElementException e) {
        System.out.println("[NIL]\nApplication is terminated via CTRL + C");
        System.exit(1);
      } catch (Exception e) {
        System.out.println("[ERROR] Invalid menu input - input must be of " + Arrays.toString(list.toArray()));
        // scanner = new Scanner(System.in); // necessity?
        // scanner.nextLine();
      }
    }
    return menuChoice;
  }

  /**
   * Remap menu to new map
   * @param menuMap:LinkedHashMap<String, Runnable>
   */
  public void refreshMenu(LinkedHashMap<String, Runnable> menuMap) {
    Helper.logger("Menu.refreshMenu", menuMap.keySet().toString());
    this.menuMap = menuMap;
  }

  /**
   * Abstract method for showing menu
   */
  public abstract void showMenu();


}
