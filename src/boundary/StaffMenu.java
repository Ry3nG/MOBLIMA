package boundary;

import control.StaffHandler;
import entity.Menu;
import entity.Staff;
import utils.Helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class StaffMenu extends Menu {
  private static StaffMenu instance;
  private static StaffHandler handler;
  private final MovieMenu movieMenu;

  private StaffMenu() {
    super();
    this.movieMenu = MovieMenu.getInstance();

    handler = new StaffHandler();

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
    this.displayMenu();
  }

  /**
   * Get the updated staff list to be displayed
   *
   * @return menuMap:LinkedHashMap<String, Runnable>
   */
  //+ getShowtimeMenu():LinkedHashMap<String, Runnable>
  public LinkedHashMap<String, Runnable> getStaffMenu() {
    LinkedHashMap<String, Runnable> menuMap = new LinkedHashMap<String, Runnable>();
    menuMap = new LinkedHashMap<String, Runnable>() {
      {
        put("View and update movie details", () -> {

          // Select movie
          System.out.println("Select movie: ");
          int movieIdx = movieMenu.selectMovieIdx();
          if (movieIdx < 0) return;

//          Movie selectedMovie = movieMenu.getHandler().getMovie(movieIdx);
          MovieMenu.getHandler().printMovieDetails(movieIdx);

          movieMenu.selectEditableAction(movieIdx);

        });
//        // put("Check seat availability and selection of seat/s.", () -> {
//        //
//        // });
//        put("Book and purchase ticket", () -> {
//          makeBooking();
//        });
//        put("View booking history", () -> {
//          viewBookings();
//
//        });
//        // put("List the Top 5 ranking by ticket sales OR by overall reviewers’
//        // ratings", () -> {
//        // });
        put("Exit", () -> {
          System.out.println("\t>>> Quitting application...");
          System.out.println("---------------------------------------------------------------------------");
          System.out.println("Thank you for using MOBLIMA. We hope to see you again soon!");
          scanner.close();
          System.exit(0);
        });
      }
    };
    return menuMap;
  }

  /**
   * Get staff handler
   *
   * @return staffHandler:StaffHandler
   */
  //+ getHandler():StaffHandler
  public StaffHandler getHandler() {
    return handler;
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
            System.out.println("Invalid username, no staff account associated.");
            username = null;
            continue;
          }
        }

        System.out.print("Password: ");
        String password = scanner.next().trim();

        Staff staff = handler.getStaff(username);
        if (!staff.getPassword().equals(password))
          throw new Exception("Invalid login credentials, unable to authenticate");

        System.out.println("Successful account login");
        staffIdx = handler.getStaffIdx(staff.getId());
        Helper.logger("StaffMenu.login", "staffIdx: " + staffIdx);

        // Flush excess scanner buffer
        scanner = new Scanner(System.in);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        username = null;

        List<String> proceedOptions = new ArrayList<String>() {
          {
            add("Proceed with login");
            add("Return to previous menu");
          }
        };

        System.out.println("Next steps:");
        this.displayMenuList(proceedOptions);
        int proceedSelection = getListSelectionIdx(proceedOptions, false);

        Helper.logger("StaffMenu.login", "Max: " + (proceedOptions.size() - 1));
        Helper.logger("StaffMenu.login", "Selected: " + proceedSelection);
        Helper.logger("StaffMenu.login", "Selected: " + proceedOptions.get(proceedSelection));

        // Return to previous menu
        if (proceedSelection == proceedOptions.size() - 1) return -2;
        else continue;
      }
    }

    return staffIdx;
  }
}


