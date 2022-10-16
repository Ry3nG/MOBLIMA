package main.utils;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Datasource {
  protected static final String DATA_DIR = "./data/";
  protected static final String ENDPOINT = "https://api.themoviedb.org/3";
  protected static final String API_KEY = Constants.getEnv("TMDB_API_KEY");
  protected static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

  public static String getApiKey() {
    return API_KEY;
  }

  protected static JsonArray convertToJsonArray(
      List list
  ) {
    return gson.toJsonTree(list).getAsJsonArray();
  }

  /// REQUESTERS
  public static JsonArray requestPagination(
      String query,
      int startIdx,
      int endIdx
  ) {
    JsonArray allResults = new JsonArray();

    for (int i = startIdx; i < endIdx; i++) {
      String pageRequest = query + "&page=" + i;

      JsonArray results = request(pageRequest).get("results").getAsJsonArray();
      allResults.addAll(results);
    }

    Helper.logger("Datasource.requestPagination", "Output: " + allResults);

    return allResults;
  }

  public static JsonObject request(
      String query
  ) {
    JsonObject responseJson = null;

    if (API_KEY == null) {
      Helper.logger("ERROR/Datasource.request", "API_KEY does not exist");
      return responseJson;
    }

    // Prep URI with API Key
    query += "&api_key=" + API_KEY;
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ENDPOINT + query)).method("GET", HttpRequest.BodyPublishers.noBody()).build();
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        throw new Exception("[LOG/ERROR/Datasource.request] Status " + response.statusCode() + "\nhttps://www.themoviedb.org/documentation/api/status-codes");
      }
      String data = response.body().trim();

      // Convert JSON to JsonObject
      responseJson = gson.fromJson(data, JsonObject.class);

      Helper.logger("Datasource.request", "Endpoint requested: " + request.uri().toString());
      Helper.logger("Datasource.request", "Output: " + data);
    } catch (Exception e) {
      e.getStackTrace();
    }

    return responseJson;
  }

  /// SERIALIZERS
  /// JsonArray to CSV
  public static boolean serializeDataToCSV(
      JsonArray responseObj,
      String outputFileName,
      boolean overwrite
  ) {
    boolean isSuccessful = false;

    if (overwrite && responseObj == null) {
      Helper.logger("ERROR/Datasource.serializeCSV", "Unable to serialize null object");
      return isSuccessful;
    }

    // Configure output path
    String path = DATA_DIR + outputFileName;
    File file = new File(path);

    try {
      // Fetch data from API (only if file doesn't exists)
      if (file.exists() && !overwrite) throw new Catcher("Datasource.serializeCSV", "File already exists at " + path);

      // Save to file (if overwrite)
      String jsonStringified = gson.toJson(responseObj);
      JSONArray jsonArray = new JSONArray(jsonStringified);
      String csvString = CDL.toString(jsonArray);
      isSuccessful = saveCsv(
          file,
          csvString,
          overwrite
      );
    } catch (Catcher e) {
      isSuccessful = true;
    }

    return isSuccessful;
  }

  //// JSON
  public static boolean serializeRequest(
      Object responseObj,
      String outputFileName,
      boolean overwrite
  ) {
    boolean isSuccessful = false;

    if (overwrite && responseObj == null) {
      Helper.logger("ERROR/Datasource.serializeRequest", "Unable to serialize null object");
      return isSuccessful;
    }

    // Configure output path
    String path = DATA_DIR + outputFileName;
    File file = new File(path);

    try {
      // Fetch data from API (only if file doesn't exists)
      if (file.exists() && !overwrite) throw new Catcher("Datasource.request", "File already exists at " + path);

      // Save to file (if overwrite)
      String jsonStringified = gson.toJson(responseObj);
      isSuccessful = saveJson(
          file,
          jsonStringified,
          overwrite
      );
    } catch (Catcher e) {
      isSuccessful = true;
    }

    return isSuccessful;
  }


  /// SAVERS
  public static boolean saveJson(
      File outputFile,
      String jsonObject,
      boolean overwrite
  ) {
    boolean isSaved = false;
    FileWriter writer = null;
    try {
      // Fetch data from API (only if file doesn't exists)
      if (outputFile.exists() && !overwrite)
        throw new Catcher("Datasource.saveJson", "File already exists at " + outputFile.getAbsolutePath());

      // Create parent directories if not exists
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();

      // Save to file
      writer = new FileWriter(outputFile.getAbsolutePath());
      writer.write(jsonObject);
      writer.close();

      Helper.logger("Datasource.saveJson", "Successfully fetched from API and output JSON to " + outputFile.getAbsolutePath());
      Helper.logger("Datasource.saveJson", "Output: " + jsonObject);

      isSaved = true;
    } catch (Catcher e) {
      isSaved = true;
    } catch (Exception e) {
      e.getStackTrace();
    }

    return isSaved;
  }

  public static boolean saveCsv(
      File outputFile,
      String csvObject,
      boolean overwrite
  ) {
    boolean isSaved = true;

    try {
      // Fetch data from API (only if file doesn't exists)
      if (outputFile.exists() && !overwrite)
        throw new Catcher("Datasource.saveCsv", "File already exists at " + outputFile.getAbsolutePath());

      // Create parent directories if not exists
      outputFile.getParentFile().mkdirs();
      outputFile.createNewFile();

      FileUtils.writeStringToFile(outputFile, csvObject, Charset.defaultCharset());

      Helper.logger("Datasource.saveCsv", "Output CSV to " + outputFile.getAbsolutePath());
      Helper.logger("Datasource.saveCsv", "Output: " + csvObject);

      isSaved = true;
    } catch (Catcher e) {
      isSaved = true;
    } catch (Exception e) {
      e.getStackTrace();
    }

    return isSaved;
  }


  /// READERS
  public static JsonArray readArray(
      String fileName
  ) {
    JsonArray result = null;
    Reader reader = null;

    // Configure target path
    String path = DATA_DIR + fileName;
    File file = new File(path);

    try {
      if (!file.exists()) throw new FileNotFoundException("File" + path + " does not exist");
      Helper.logger("Datasource.readArray", "Reading from " + file.getAbsolutePath());

      reader = new FileReader(file.getAbsolutePath());
      result = JsonParser.parseReader(reader).getAsJsonArray();

      reader.close();
    } catch (Exception e) {
      e.getStackTrace();
    }

    return result;
  }

  public static JsonObject readObject(
      String fileName
  ) {
    JsonObject result = null;
    Reader reader = null;

    // Configure target path
    String path = DATA_DIR + fileName;
    File file = new File(path);

    try {
      if (!file.exists()) throw new FileNotFoundException("File" + path + " does not exist");
      Helper.logger("Datasource.readObject", "Reading from " + file.getAbsolutePath());

      reader = new FileReader(file.getAbsolutePath());
      result = JsonParser.parseReader(reader).getAsJsonObject();

      reader.close();
    } catch (Exception e) {
      e.getStackTrace();
    }

    return result;
  }

  public static JsonArray readArrayFromCsv(
      String fileName
  ) {
    JsonArray result = null;

    // Configure target path
    String path = DATA_DIR + fileName;
    File file = new File(path);

    try {
      if (!file.exists()) throw new FileNotFoundException("File" + path + " does not exist");
      Helper.logger("Datasource.readArrayFromCsv", "Reading from " + file.getAbsolutePath());

      String content = Files.readString(Paths.get(path));
      JSONArray jsonArray = CDL.toJSONArray(content);
      String jsonStringified = jsonArray.toString();
      result = gson.fromJson(jsonStringified, JsonArray.class);

      Helper.logger("Datasource.readArrayFromCsv", "Stringified " + jsonStringified);
      Helper.logger("Datasource.readArrayFromCsv", "Result " + result);

    } catch (Exception e) {
      e.getStackTrace();
    }

    return result;
  }

}
