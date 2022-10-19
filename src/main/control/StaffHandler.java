package main.control;

import main.entity.SystemSettings;

import java.util.HashMap;
public class StaffHandler {

  private static StaffHandler instance = null;

  // private constructor
  private StaffHandler() {
  }

  // singleton pattern design
  public static StaffHandler getInstance() {
      if (instance == null) {
          instance = new StaffHandler();
      }
      return instance;
  }

  public boolean login(String username, String password) {
    // return false;
    return true;
  }

  public HashMap<String,Object> retrieveSettings() {
    SystemSettings settings = SystemSettings.getInstance();
    HashMap<String,Object> mapping = new HashMap<String,Object>();
    mapping.put("standardTix", settings.getStandardTix());
    mapping.put("studentTix", settings.getStudentTix());
    mapping.put("seniorTix", settings.getSeniorTix());
    mapping.put("holidays", settings.getHolidays().toArray());

    return mapping;
  }
}
