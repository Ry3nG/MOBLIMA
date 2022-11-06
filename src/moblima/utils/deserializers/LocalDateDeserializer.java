package moblima.utils.deserializers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The type Local date deserializer.
 */
public class LocalDateDeserializer implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
  /**
   * The constant dateFormatter.
   */
  public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withLocale(Locale.ENGLISH);

  @Override
  public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return LocalDate.parse(json.getAsString(), dateFormatter);
  }

  @Override
  public JsonElement serialize(LocalDate localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(localDateTime.format(dateFormatter));
  }
}

