package control.handlers;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import entity.Staff;
import moblima.control.Datasource;
import utils.Helper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StaffHandler {
  private List<Staff> staffs;
  private Staff currentStaff = null;

  public StaffHandler() {
    this.staffs = this.getStaffs();
  }

  /**
   * Get currently selected / active staff
   *
   * @return staff:Staff | null
   */
  // + getCurrentStaff() : Staff
  public Staff getCurrentStaff() {
    return this.currentStaff;
  }

  /**
   * Save currently selected / active staff by idx
   *
   * @param staffIdx:int
   */
  // + setCurrentStaff(staffIdx:int) : void
  public void setCurrentStaff(int staffIdx) {
    this.currentStaff = this.getStaff(staffIdx);
  }

  /**
   * Validate username availability in account login/registration
   *
   * @param username:String
   * @return status:boolean
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
   * Deserializes and return staff list
   *
   * @return staffs:List<Staff>
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
      this.addStaff("staff", "staff");
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
   * Get staff by specified idx
   *
   * @param staffIdx:int
   * @return staff:Staff | null
   */
  // + getStaff(staffIdx:int) : Staff
  public Staff getStaff(int staffIdx) {
    Staff staff = null;
    if (staffIdx < 0 || this.staffs.size() < 1)
      return staff;

    staff = this.staffs.get(staffIdx);
    return staff;
  }

  /**
   * Get staff by specified username
   *
   * @param username:String
   * @return staff:Staff | null
   */
  // + getStaff(username: String) : int
  public Staff getStaff(String username) {
    Staff staff = null;
    if (this.validateUsernameAvailability(username) || this.staffs.size() < 1)
      return staff;

    for (Staff s : staffs) {
      if (s.getUsername().equals(username)) {
        staff = s;
        break;
      }
    }

    return staff;
  }

  /**
   * Retrieve idx of specified staff id
   *
   * @param staffId:String
   * @return staffIdx:int
   */
  // + getStaffIdx(staffId: String) : int
  public int getStaffIdx(String staffId) {
    int staffIdx = -1;
    if (staffId.isEmpty() || this.staffs.size() < 1)
      return staffIdx;

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
   * Append new staff to staff list
   *
   * @param username:String
   * @param password:String
   * @return staffIdx:int
   */
  // +addStaff(username:String, password:String) : int
  public int addStaff(String username, String password) {

    // Initialize new Staff
    Staff staff = new Staff(
        UUID.randomUUID().toString(),
        username,
        password
    );
    this.staffs.add(staff);
    this.currentStaff = staff;

    // Serialize data
    this.saveStaffs();

    return this.staffs.size() - 1;
  }

  /**
   * Serialize staff data to CSV
   */
  //# saveCustomers():boolean
  public boolean saveStaffs() {
    return Datasource.serializeData(this.staffs, "staffs.csv");
  }
}
