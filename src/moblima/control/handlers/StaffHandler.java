package moblima.control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import moblima.entities.Staff;
import moblima.utils.Helper;
import moblima.utils.datasource.Datasource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Staff handler.
 */
public class StaffHandler {
  /**
   * The Staffs.
   */
  protected List<Staff> staffs;
  /**
   * The Current staff.
   */
  protected Staff currentStaff = null;

  /**
   * Instantiates a new Staff handler.
   */
  public StaffHandler() {
    this.staffs = this.getStaffs();
  }

  /**
   * Gets current staff.
   *
   * @return the current staff
   */
// + getCurrentStaff() : Staff
  public Staff getCurrentStaff() {
    return this.currentStaff;
  }

  /**
   * Sets current staff.
   *
   * @param staffIdx the staff idx
   */
// + setCurrentStaff(staffIdx:int) : void
  public void setCurrentStaff(int staffIdx) {
    this.currentStaff = this.getStaff(staffIdx);
  }

  /**
   * Validate username availability boolean.
   *
   * @param username the username
   * @return the boolean
   */
// + validateUsernameAvailability(String username) : boolean
  public boolean validateUsernameAvailability(String username) {
    boolean status = true;
    if (this.staffs.isEmpty() || username.isEmpty()) return status;

    for (Staff staff : staffs) {
      if (staff.getUsername().equals(username)) {
        status = false;
        break;
      }
    }

    return status;
  }

  /**
   * Gets staffs.
   *
   * @return the staffs
   */
// + getStaffs() : List<Staff>
  public List<Staff> getStaffs() {
    List<Staff> staffs = new ArrayList<Staff>();
    this.staffs = staffs;

    //Source from serialized datasource
    String fileName = "staffs.csv";
    if (fileName == null || fileName.isEmpty()) {
      Helper.logger("StaffHandler.getStaffs", "Null and void filename provided, no data retrieved.");
      return staffs;
    }

    JsonArray staffList = Datasource.readArrayFromCsv(fileName);
    if (staffList == null) {
      Helper.logger("StaffHandler.getStaffs", "No serialized data available");
      this.addStaff("Staff", "staff", "staff");
      staffs = this.staffs;
      return staffs;
    }

    String strStaffList = Datasource.getGson().toJson(staffList);
    Type typeStaffList = new TypeToken<List<Staff>>() {
    }.getType();
    staffs = Datasource.getGson().fromJson(strStaffList, typeStaffList);

    return staffs;
  }

  /**
   * Gets staff.
   *
   * @param staffIdx the staff idx
   * @return the staff
   */
// + getStaff(staffIdx:int) : Staff
  public Staff getStaff(int staffIdx) {
    Staff staff = null;
    if (staffIdx < 0 || this.staffs.size() < 1) return staff;

    staff = this.staffs.get(staffIdx);
    return staff;
  }

  /**
   * Gets staff.
   *
   * @param username the username
   * @return the staff
   */
// + getStaff(username: String) : int
  public Staff getStaff(String username) {
    Staff staff = null;
    if (this.validateUsernameAvailability(username) || this.staffs.size() < 1) return staff;

    for (Staff s : staffs) {
      if (s.getUsername().equals(username)) {
        staff = s;
        break;
      }
    }

    return staff;
  }

  /**
   * Gets staff idx.
   *
   * @param staffId the staff id
   * @return the staff idx
   */
// + getStaffIdx(staffId: String) : int
  public int getStaffIdx(String staffId) {
    int staffIdx = -1;
    if (staffId.isEmpty() || this.staffs.size() < 1) return staffIdx;

    for (int i = 0; i < this.staffs.size(); i++) {
      Staff staff = this.staffs.get(i);
      if (staff.getId().equals(staffId)) {
        staffIdx = i;
        break;
      }
    }
    return staffIdx;
  }

  /**
   * Add staff int.
   *
   * @param name     the name
   * @param username the username
   * @param password the password
   * @return the int
   */
// +addStaff(username:String, password:String) : int
  public int addStaff(String name, String username, String password) {

    // Initialize new Staff
    Staff staff = new Staff(UUID.randomUUID().toString(), name, username, password);
    this.staffs.add(staff);
    this.currentStaff = staff;

    // Serialize data
    this.saveStaffs();

    return this.staffs.size() - 1;
  }

  /**
   * Save staffs boolean.
   *
   * @return the boolean
   */
//# saveStaffs():boolean
  public boolean saveStaffs() {
    return Datasource.serializeData(this.staffs, "staffs.csv");
  }
}
