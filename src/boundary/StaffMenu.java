package boundary;

import control.handlers.StaffHandler;
import control.menu.StaffController;
import entity.Booking;
import entity.Cinema;
import entity.Price;
import entity.Staff;
import utils.Helper;
import utils.Helper.Preset;

import java.util.*;

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
    if (isAuthenticated()) {
      System.out.println(colorize("STAFF: " + handler.getCurrentStaff().getUsername(), Preset.SUCCESS.color));
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

    String username = null, password = null;
    System.out.println("Staff Account Registration");
    while (!status && scanner.hasNextLine()) {
      try {
        scanner = new Scanner(System.in).useDelimiter("\n");

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

        int staffIdx = handler.addStaff(username, password);
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
      put("View and update prices", () -> editPrices());
      put("Register new staff account", () -> {
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

  public boolean editPrices() {
    boolean status = false;

    // Retrieve prices
    Price price = controller.priceHandler().getCurrentPrice();
    System.out.println(price.toString());
    List<String> proceedOptions = new ArrayList<String>() {
      {
        add("Set base ticket price");
        add("Set blockbuster surcharge");
        add("Set ticket type surcharges");
        add("Set cinema class surcharges");
        add("Discard changes");
        add("Save changes & return");
        add("Return to previous menu");
      }
    };

    while (!status) {
      try {
        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        // Save changes & return OR Return to previous menu
        if (proceedSelection >= proceedOptions.size() - 2) {
          // Save changes
          if (proceedSelection == proceedOptions.size() - 2) {
            controller.priceHandler().updatePrice(price);
            status = true;
          }

          System.out.println("\t>>> " + "Returning to previous menu...");
          return status;
        }

        // Discard changes
        else if (proceedSelection == proceedOptions.size() - 3) {
          System.out.println(colorize("[REVERTED] Changes discarded", Preset.SUCCESS.color));
          price = controller.priceHandler().getCurrentPrice();
          System.out.println(price);
        }

        // Set base ticket price
        else if (proceedSelection == 0) {
          double prevStatus = price.getBaseTicket();
          System.out.println("[CURRENT] Base Ticket: " + price.formattedPrice(prevStatus));

          //TODO: Extract as separate function
          double baseTicket = -1;
          while (baseTicket == -1) {
            scanner = new Scanner(System.in).useDelimiter("\n");

            System.out.print("Set to:");
            if (!scanner.hasNextDouble()) {
              System.out.println(colorize("Invalid input, try again", Preset.ERROR.color));
              continue;
            }

            baseTicket = scanner.nextDouble();
            price.setBaseTicket(baseTicket);

            if (prevStatus == baseTicket) {
              System.out.println("[NO CHANGE] Datetime: " + price.formattedPrice(prevStatus));
            } else {
              System.out.println("[UPDATED] Datetime: " + price.formattedPrice(prevStatus) + " -> " + price.formattedPrice(baseTicket));
            }
          }

        }

        // Set blockbuster surcharge
        else if (proceedSelection == 1) {
          double prevStatus = price.getBlockbusterSurcharge();
          System.out.println("[CURRENT] Blockbuster Surcharge: " + price.formattedPrice(prevStatus));

          //TODO: Extract as separate function
          double blockbusterSurcharge = -1;

          while (blockbusterSurcharge == -1) {
            scanner = new Scanner(System.in).useDelimiter("\n");
            System.out.print("Set to:");
            if (!scanner.hasNextDouble()) {
              System.out.println(colorize("Invalid input, try again", Preset.ERROR.color));
              continue;
            }
            blockbusterSurcharge = scanner.nextDouble();

            price.setBlockbusterSurcharge(blockbusterSurcharge);
            if (prevStatus == blockbusterSurcharge) {
              System.out.println("[NO CHANGE] Blockbuster Surcharge: " + price.formattedPrice(prevStatus));
            } else {
              System.out.println("[UPDATED] Blockbuster Surcharge: " + price.formattedPrice(prevStatus) + " -> " + price.formattedPrice(blockbusterSurcharge));
            }
          }

        }

        // Set ticket surcharge
        else if (proceedSelection == 2) {
          EnumMap<Booking.TicketType, Double> ticketSurcharges = price.getTicketSurcharges();

          //TODO: Extract as separate function
          for (var entry : ticketSurcharges.entrySet()) {
            double prevStatus = entry.getValue();
            System.out.println("[CURRENT] " + entry.getKey().toString() + ": " + price.formattedPrice(prevStatus));

            double curStatus = -1;
            while (curStatus == -1) {
              scanner = new Scanner(System.in).useDelimiter("\n");
              System.out.print("Set to:");
              if (!scanner.hasNextDouble()) {
                System.out.println(colorize("Invalid input, try again", Preset.ERROR.color));
                continue;
              }
              curStatus = scanner.nextDouble();

              entry.setValue(curStatus);
              if (prevStatus == curStatus) {
                System.out.println("[NO CHANGE] " + entry.getKey().toString() + ": " + price.formattedPrice(curStatus));
              } else {
                System.out.println("[UPDATED] " + entry.getKey().toString() + ": " + price.formattedPrice(prevStatus) + " -> " + price.formattedPrice(curStatus));
              }
            }

          }
        }

        // Set cinema class surcharge
        else if (proceedSelection == 2) {
          EnumMap<Cinema.ClassType, Double> cinemaSurcharges = price.getCinemaSurcharges();

          //TODO: Extract as separate function
          for (var entry : cinemaSurcharges.entrySet()) {
            double prevStatus = entry.getValue();
            System.out.println("[CURRENT] " + entry.getKey().toString() + ": " + price.formattedPrice(prevStatus));

            double curStatus = -1;
            while (curStatus == -1) {
              scanner = new Scanner(System.in).useDelimiter("\n");
              System.out.print("Set to:");
              if (!scanner.hasNextDouble()) {
                System.out.println(colorize("Invalid input, try again", Preset.ERROR.color));
                continue;
              }
              curStatus = scanner.nextDouble();

              entry.setValue(curStatus);
              if (prevStatus == curStatus) {
                System.out.println("[NO CHANGE] " + entry.getKey().toString() + ": " + price.formattedPrice(curStatus));
              } else {
                System.out.println("[UPDATED] " + entry.getKey().toString() + ": " + price.formattedPrice(prevStatus) + " -> " + price.formattedPrice(curStatus));
              }
            }

          }
        }

      } catch (Exception e) {
        System.out.println(colorize(e.getMessage(), Preset.ERROR.color));
      }

    }
    return status;
  }

}


