package moblima.boundaries;

import moblima.control.controllers.StaffController;
import moblima.control.handlers.StaffHandler;
import moblima.entities.Staff;
import moblima.utils.Helper;
import moblima.utils.Helper.Preset;

import java.io.Console;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static moblima.utils.Helper.colorPrint;

/**
 * The type Staff menu.
 */
public class StaffMenu extends Menu {
  private static StaffMenu instance;
  private static StaffHandler handler;
  private static StaffController controller;

  private StaffMenu() {
    super();

    handler = new StaffHandler();
    controller = StaffController.getInstance();

    Helper.logger("StaffMenu", "Init: \n" + controller.settingsHandler().getSettings());

    // Require account authentication first
    this.isAuthenticated();
  }

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static StaffMenu getInstance() {
    if (instance == null) instance = new StaffMenu();
    return instance;
  }

  @Override
  public void showMenu() {
    boolean isAuthenticated = isAuthenticated();
    if (isAuthenticated) {
      Staff currentStaff = handler.getCurrentStaff();
      controller.settingsHandler().setIsAuthenticated(currentStaff);
      colorPrint("STAFF: " + currentStaff.getUsername(), Preset.HIGHLIGHT);
    }
    this.displayMenu();
  }

  /**
   * Gets current staff.
   *
   * @return the current staff
   */
//+ getCurrentStaff():int
  public int getCurrentStaff() {
    int staffIdx = -1;

    Staff staff = handler.getCurrentStaff();
    if (staff != null) {
      staffIdx = handler.getStaffIdx(staff.getId());
      handler.setCurrentStaff(staffIdx);
      return staffIdx;
    }

    while (staffIdx == -1) {
      staffIdx = this.login();
      if (staffIdx == -2) return staffIdx;
    }

    handler.setCurrentStaff(staffIdx);
    return staffIdx;
  }

  /**
   * Login int.
   *
   * @return the int
   */
//+ login():int
  public int login() {
    int staffIdx = -1;

    String username = null;
    colorPrint("/// STAFF LOGIN ///", Preset.DEFAULT);
    while (staffIdx == -1) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        if (username == null) {
          System.out.print("Username: ");
          username = scanner.next().trim();

          // VALIDATION: Check if username already exists
          if (handler.validateUsernameAvailability(username)) {
            throw new Exception("Invalid username, no staff account associated.");
          }
        }

        String password = null;
        Console console = System.console();
        if (console != null) {
          char[] passwordArray = console.readPassword("Password: ");
          password = new String(passwordArray);
          Helper.logger("StaffMenu.login", "Password entered: " + password);
        } else {
          System.out.print("Password: ");
          password = scanner.next().trim();
        }

        Staff staff = handler.getStaff(username);
        if (!staff.getPassword().equals(password))
          throw new Exception("Invalid login credentials, unable to authenticate");

        colorPrint("Successful account login", Preset.SUCCESS);
        staffIdx = handler.getStaffIdx(staff.getId());
        Helper.logger("StaffMenu.login", "staffIdx: " + staffIdx);

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);

        username = null;

        List<String> proceedOptions = new ArrayList<String>() {{
          add("Proceed with login");
          add("Exit application");
        }};

        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        Helper.logger("StaffMenu.login", "Max: " + (proceedOptions.size() - 1));
        Helper.logger("StaffMenu.login", "Selected: " + proceedSelection);
        Helper.logger("StaffMenu.login", "Selected: " + proceedOptions.get(proceedSelection));

        // Return to previous menu
        if (proceedSelection == proceedOptions.size() - 1) return -2;
      }
    }

    return staffIdx;
  }

  /**
   * Register boolean.
   *
   * @return the boolean
   */
//+ register():boolean
  public boolean register() {
    boolean status = false;

    String name = null, username = null, password = null;
    System.out.println("Staff Account Registration");
    while (!status && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        if (name == null) {
          System.out.print("Name: ");
          name = scanner.next().trim();
        }

        if (username == null) {
          System.out.print("Username: ");
          username = scanner.next().trim();

          if (!handler.validateUsernameAvailability(username)) {
            colorPrint("Username is taken, try another", Preset.ERROR);
            username = null;
            continue;
          }
        }

        if (password == null) {
          System.out.print("Password: ");
          password = scanner.next().trim();
        }

        int staffIdx = handler.addStaff(name, username, password);
        if (staffIdx < 0) throw new Exception("Unable to register, account with username already exists");

        status = true;
        colorPrint("Successful account registration", Preset.SUCCESS);

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        colorPrint(e.getMessage(), Preset.ERROR);
        username = password = null;

        List<String> proceedOptions = new ArrayList<String>() {
          {
            add("Proceed with registration");
            add("Return to previous menu");
          }
        };

        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        // Return to previous menu
        if (proceedSelection == proceedOptions.size() - 1) return status;
      }
    }

    return status;
  }

  /**
   * Is authenticated boolean.
   *
   * @return the boolean
   */
//+ isAuthenticated():boolean
  public boolean isAuthenticated() {
    boolean status = false;

    int staffIdx = this.getCurrentStaff();
    Helper.logger("StaffMenu.isAuthenticated", "staffIdx: " + staffIdx);

    if (staffIdx < 0) {
      System.out.println("\t>>> Quitting application...");
      System.out.println("---------------------------------------------------------------------------");
      scanner.close();
      System.exit(0);
    } else {
      status = true;
      this.refreshMenu(this.getStaffMenu());
    }

    return status;
  }

  /**
   * Gets staff menu.
   *
   * @return the staff menu
   */
//+ getStaffMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getStaffMenu() {
    LinkedHashMap<String, Runnable> menuMap = controller.getStaffMenu();
    LinkedHashMap<String, Runnable> addMenuMap = new LinkedHashMap<String, Runnable>() {{
      put("Register new staff account", () -> {  // NOTE: this should be something that vendor can do, not staff
        // Maintain current account
        int staffIdx = getCurrentStaff();

        register();

        // Revert to account
        handler.setCurrentStaff(staffIdx);
      });
      // put("List the Top 5 ranking by ticket sales OR by overall reviewers’
      // ratings", () -> {
      // });
      put("Exit", () -> {
        System.out.println("\t>>> Quitting application...");
        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Thank you for using MOBLIMA. We hope to see you again soon!");
        scanner.close();
        System.exit(0);
      });
    }};

    menuMap.putAll(addMenuMap);
    return menuMap;
  }
}
