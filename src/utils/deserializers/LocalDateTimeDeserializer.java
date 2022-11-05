package utils.deserializers;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeDeserializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

  public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mma").withLocale(Locale.ENGLISH);

  @Override
  public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    String ldtString = jsonElement.getAsString();
    return LocalDateTime.parse(ldtString, dateTimeFormatter);
  }

  @Override
  public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(localDateTime.format(dateTimeFormatter));
  }
}
