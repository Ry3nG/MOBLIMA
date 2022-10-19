package main.boundary;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import main.control.StaffHandler;

/**
 * System Settings Menu
 * 
 * @author SS11 Group 1
 * @version 1.0
 * @since 18 October 2022
 */
public class SettingsMenu extends Menu{
  
  /**
   * Obtain an instance of StaffMenu to return to that menu
   */
  private static final StaffMenu staffMenu = new StaffMenu();
  private static StaffHandler staffHandler = StaffHandler.getInstance();
  private HashMap<String,Object> settings;

  public SettingsMenu() {
    super();
    this.loadCurrentSettings();
    this.menuMap = new LinkedHashMap<String,Runnable>() {{
    put("View Current Settings", () -> displayCurrentSettings());
    put("Change Ticket Price", () -> {});
    put("Add Public Holiday", () -> {});
    put("Remove Public Holiday", () -> {});
    put("Return to Main Menu", () -> {
      System.out.println("\t>>> Returning to Main Menu...");
      staffMenu.showMenu();
    });
    }};
  }

  @Override
  public void showMenu() {
    this.displayMenu();
  }

  private void loadCurrentSettings() {
    this.settings = staffHandler.retrieveSettings();
  }

  private void displayCurrentSettings() {
    System.out.println("---------------------------------------------------------------------------");
    System.out.println("CURRENT SETTINGS:");
    
    System.out.println("- Standard Ticket: SGD " + this.settings.get("standardTix"));
    System.out.println("- Student Ticket: SGD " + this.settings.get("studentTix"));
    System.out.println("- Senior Ticket: SGD " + this.settings.get("seniorTix"));

    System.out.println("- Public Holidays (see below)");
    Object[] hols = (Object[])this.settings.get("holidays");
    for (int i = 0; i < hols.length; i++) {
      System.out.println("  - " + hols[i]);
    }
  }
}
