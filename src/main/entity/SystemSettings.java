package main.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class SystemSettings {

  private float standardTix;
  private float studentTix;
  private float seniorTix;

  private ArrayList<String> holidays = new ArrayList<String>();

  private static SystemSettings _settings = null;

  private SystemSettings() {
    File csv = new File("data/settings.csv");
    String absolutePath = csv.getAbsolutePath();
    try {
      BufferedReader br = new BufferedReader(new FileReader(absolutePath));
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        switch (values[0]) {
          case "standardTix":
            this.standardTix = Float.parseFloat(values[1].trim());
            break;
          case "studentTix":
            this.studentTix = Float.parseFloat(values[1].trim());
            break;
          case "seniorTix":
            this.seniorTix = Float.parseFloat(values[1].trim());
            break;
          case "holidays":
            for (int i = 1; i < values.length; i++) {
              this.holidays.add(values[i].trim());
            }
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public static SystemSettings getInstance() {
    if (_settings == null) _settings = new SystemSettings();
    return _settings;
  }

  public float getStandardTix() {
    return standardTix;
  }

  public void setStandardTix(float standardTix) {
    this.standardTix = standardTix;
  }

  public float getStudentTix() {
    return studentTix;
  }

  public void setStudentTix(float studentTix) {
    this.studentTix = studentTix;
  }

  public float getSeniorTix() {
    return seniorTix;
  }

  public void setSeniorTix(float seniorTix) {
    this.seniorTix = seniorTix;
  }

  public ArrayList<String> getHolidays() {
    return this.holidays;
  }

  public boolean isHoliday(String yymmdd) {
    if (this.holidays.contains(yymmdd)) return true;
    return false;
  }

  public boolean addHoliday(String yymmdd) {
    if (isHoliday(yymmdd)) return false; // already added
    holidays.add(yymmdd);
    return true;
  }

  public boolean delHoliday(String yymmdd) {
    if (!isHoliday(yymmdd)) return false;
    this.holidays.remove(yymmdd);
    return true;
  }

}
