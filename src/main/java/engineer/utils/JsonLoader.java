package engineer.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class JsonLoader {
  public JsonObject loadJson(String path) {
    try (Reader fileReader = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(path)))) {
      return new Gson().fromJson(fileReader, JsonObject.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
