package main.boundary;

import java.util.LinkedHashMap;
import java.util.Scanner;

import static java.lang.System.exit;

public abstract class Menu {
  protected static Scanner scanner = new Scanner(System.in);
  protected LinkedHashMap<String, Runnable> menuMap;

  /**
   * Initializes the Menu object
   * - menuMap: contains the menu label and actions
   *
   * @return void
   */
  public Menu() {
    this.menuMap = new LinkedHashMap<String, Runnable>();
  }

  /**
   * Pretty-printer for menu list
   *
   * @return void
   */
  protected void displayMenuList() {
    if (this.menuMap.size() < 1) exit(0);

    int menuIdx = 1;
    System.out.println("---------------------------------------------------------------------------");
    for (String key : this.menuMap.keySet()) {
      System.out.println("(" + menuIdx + ") " + key);
      menuIdx++;
    }
    System.out.println("---------------------------------------------------------------------------");
  }

  /**
   * Display menu and executes the mapped action
   *
   * @return void
   */
  protected void displayMenu() {
    int menuChoice = 0;
    while (menuChoice != menuMap.size()) {
      try {
        displayMenuList();
        System.out.print("SELECTED: ");

        if (scanner.hasNextInt()) {
          menuChoice = scanner.nextInt();
        } else {
          System.out.println("Please enter value between 1 to " + menuMap.size());
          scanner.next();
          continue;
        }

        if (menuChoice < 0)
          throw new IllegalArgumentException("[ERROR] Negative value - input must be a positive integer");
        else if (menuChoice > menuMap.size())
          throw new IllegalArgumentException("[ERROR] Invalid menu selection - input must be a valid menu selection integer");

        if (menuChoice != menuMap.size())
          System.out.print("\t>>> " + menuMap.keySet().toArray()[menuChoice - 1] + "\n");
        menuMap.get(menuMap.keySet().toArray()[menuChoice - 1]).run();
      } catch (java.util.InputMismatchException e) {
        System.out.println("[ERROR] Invalid non-numerical value - input must be an integer");
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
      } catch (Exception e) {
        System.out.println("[ERROR] Invalid menu input - input must be of " + menuMap.keySet().toArray());
        scanner.next();
      }
    }
  }

  /**
   * Abstract method for showing menu
   */
  public abstract void showMenu();

}
