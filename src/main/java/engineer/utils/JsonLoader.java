package engineer.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.Reader;

public class JsonLoader {
  public JsonObject loadJson(String path) {
    try (Reader fileReader = new FileReader(path)) {
      return new Gson().fromJson(fileReader, JsonObject.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
