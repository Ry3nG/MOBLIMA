package main.boundary;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.lang.System.exit;

public abstract class Menu {
  protected static Scanner scanner = new Scanner(System.in);
  protected LinkedHashMap<String, Runnable> menuMap;
  protected boolean killSession = false;

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
   */
  protected void displayMenu() {
    int menuChoice = 0;
    scanner = new Scanner(System.in);
    while ((menuChoice != menuMap.size()) && !killSession) {
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
        // if (menuChoice != menuMap.size()) System.in.read(); // this line causes an extra 'Enter' to be required to display menu again, commented out
      } catch (java.util.InputMismatchException e) {
        System.out.println("[ERROR] Invalid non-numerical value - input must be an integer");
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
      } catch (NoSuchElementException e) {
        System.out.println("[NIL]\nApplication is terminated via CTRL + C");
        System.exit(1);
      } catch (Exception e) {
        System.out.println("[ERROR] Invalid menu input - input must be of " + Arrays.toString(menuMap.keySet().toArray()));
        // scanner = new Scanner(System.in); // necessity?
        // scanner.nextLine();
      }
    }
  }

  /**
   * Abstract method for showing menu
   */
  public abstract void showMenu();

}
