package engineer.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.Writer;

public class JsonSaver {
  public void saveJson(String path, JsonObject jsonObject) {
    try (Writer writer = new FileWriter(path)) {
      new Gson().toJson(jsonObject, writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  public void clearJson(String path) {
    try (Writer writer = new FileWriter(path)) {
      writer.write("");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
