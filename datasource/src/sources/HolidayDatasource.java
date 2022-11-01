package sources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import utils.Helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HolidayDatasource extends Datasource {
  protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public HolidayDatasource() {
    ENDPOINT = "https://notes.rjchow.com/singapore_public_holidays/api/";
    API_KEY = null;
  }

  public List<LocalDate> fetchHolidays() {
    List<LocalDate> holidays = new ArrayList<LocalDate>();

    // Get current year
    int currentYear = LocalDate.now().getYear();

    // API Request to get the list of holidays
    String queryHolidayList = currentYear + "/data.json";
    JsonArray holidaylist = request(queryHolidayList).getAsJsonArray();
    Helper.logger("HolidayDatasource.fetchHolidays", "Results: " + holidaylist);
    if (holidaylist == null) return holidays;

    // Iterate through API payload to map as LocalDate object
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
    serializeData(holidays, "holidays.csv");

    return holidays;
  }

}
