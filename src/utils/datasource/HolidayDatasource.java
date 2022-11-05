package utils.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import utils.Helper;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Real-world datasource to fetch holiday data
 *
 * @author Crystal Cheong
 */
public class HolidayDatasource extends Datasource {
  public HolidayDatasource() {
    ENDPOINT = "https://notes.rjchow.com/singapore_public_holidays/api/";
    API_KEY = null;
  }

  /**
   * Serializes holiday data
   *
   * @param holidays:List<LocalDate>
   * @return status:boolean
   */
  public static boolean saveHolidays(List<LocalDate> holidays) {
    return serializeData(holidays, "holidays.csv");
  }

  /**
   * Retrieves holidays from serialized data
   *
   * @return holidays:List<LocalDate>
   */
  public List<LocalDate> getHolidays() {
    List<LocalDate> holidays = new ArrayList<LocalDate>();

    String fileName = "holidays.csv";
    JsonArray holidayList = Datasource.readArrayFromCsv(fileName);
    Helper.logger("HolidayDatasource.getHolidays", "holidayList: " + holidayList);

    if (holidayList == null) {
      holidays = this.fetchHolidays();
      return holidays;
    }

    String strHolidayList = Datasource.getGson().toJson(holidayList);
    Type typeHolidayList = new TypeToken<List<LocalDate>>() {
    }.getType();
    holidays = Datasource.getGson().fromJson(strHolidayList, typeHolidayList);

    Helper.logger("HolidayDatasource.getHolidays", "Total holidays: " + holidays.size());
    return holidays;
  }

  /**
   * Requests the current year's holiday data from API
   *
   * @return holidays:List<LocalDate>
   * <a href="https://notes.rjchow.com/singapore_public_holidays/api/2022/data.json">Holidays Endpoint</a>
   */
  public List<LocalDate> fetchHolidays() {
    List<LocalDate> holidays = new ArrayList<LocalDate>();

    // Get current year
    int currentYear = LocalDate.now().getYear();

    // API Request to get the list of holidays
    String queryHolidayList = currentYear + "/data.json";
    JsonElement response = request(queryHolidayList);
    if (response == null) {
      Helper.logger("HolidayDatasource.fetchHolidays", "Unable to fetch response, possibly the lack of internet connectivity");
      return holidays;
    }
    JsonArray holidaylist = response.getAsJsonArray();
    Helper.logger("HolidayDatasource.fetchHolidays", "Results: " + holidaylist);
    if (holidaylist == null) return holidays;

    // Iterate through API payload to map as LocalDate object
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    for (JsonElement holiday : holidaylist) {
      JsonObject h = holiday.getAsJsonObject();

      /// Raw values
      String holidayDate = h.get("Date").getAsString();

      /// Derived
      //// Holiday Date
      LocalDate dateHoliday = LocalDate.parse(holidayDate, formatter);

      // Initialise and append to list
      holidays.add(dateHoliday);
    }

    Helper.logger("HolidayDatasource.fetchHolidays", "Total holidays: " + holidays.size());

    // Serialize data to CSV
    saveHolidays(holidays);

    return holidays;
  }
}
