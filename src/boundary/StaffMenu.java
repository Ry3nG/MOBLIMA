package boundary;

import control.controllers.StaffController;
import control.handlers.StaffHandler;
import entity.Staff;
import utils.Helper;
import utils.Helper.Preset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;

public class StaffMenu extends Menu {
  private static StaffMenu instance;
  private static StaffHandler handler;
  private static StaffController controller;

  private StaffMenu() {
    super();

    handler = new StaffHandler();
    controller = StaffController.getInstance();

    // Require account authentication first
    this.isAuthenticated();
  }

  public static StaffMenu getInstance() {
    if (instance == null)
      instance = new StaffMenu();
    return instance;
  }

  @Override
  public void showMenu() {
    boolean isAuthenticated = isAuthenticated();
    if (isAuthenticated) {
      Staff currentStaff = handler.getCurrentStaff();
      controller.settingsHandler().setIsAuthenticated(currentStaff);
      System.out.println(colorize("STAFF: " + currentStaff.getUsername(), Preset.SUCCESS.color));
    }
    this.displayMenu();
  }

  /**
   * Retrieve currently selected / active staff via login/registration
   *
   * @return staffIdx:int
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
   * Facilitates staff account login
   *
   * @return staffIdx:int
   */
  //+ login():int
  public int login() {
    int staffIdx = -1;

    String username = null;
    System.out.println("Account Login");
    while (staffIdx == -1) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

        if (username == null) {
          System.out.print("Username: ");
          username = scanner.next().trim();

          // VALIDATION: Check if username already exists
          if (handler.validateUsernameAvailability(username)) {
            System.out.println(colorize("Invalid username, no staff account associated.", Preset.ERROR.color));
            username = null;
            continue;
          }
        }

        System.out.print("Password: ");
        String password = scanner.next().trim();

        Staff staff = handler.getStaff(username);
        if (!staff.getPassword().equals(password))
          throw new Exception("Invalid login credentials, unable to authenticate");

        System.out.println(colorize("Successful account login", Preset.SUCCESS.color));
        staffIdx = handler.getStaffIdx(staff.getId());
        Helper.logger("StaffMenu.login", "staffIdx: " + staffIdx);

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(colorize(e.getMessage(), Preset.ERROR.color));
        username = null;

        List<String> proceedOptions = new ArrayList<String>() {{
          add("Proceed with login");
          add("Return to previous menu");
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
   * Facilitates staff account registration
   *
   * @return status:boolean
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
            System.out.println(colorize("Username is taken, try another", Preset.ERROR.color));
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
        System.out.println(colorize("Successful account registration", Preset.SUCCESS.color));

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(colorize(e.getMessage(), Preset.ERROR.color));
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
        if (proceedSelection == proceedOptions.size() - 1)
          return status;
      }
    }

    return status;
  }

  /**
   * Check if staff was authenticated
   *
   * @return status:boolean
   */
  //+ isAuthenticated():boolean
  public boolean isAuthenticated() {
    boolean status = false;

    int staffIdx = this.getCurrentStaff();
    Helper.logger("StaffMenu.isAuthenticated", "staffIdx: " + staffIdx);

    if (staffIdx < 0) {
      System.out.println("\t>>> Access Denied, Quitting application...");
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
   * Gets full staff menu list
   *
   * @return menuMap:LinkedHashMap<String, Runnable>
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
