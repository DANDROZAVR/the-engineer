package engineer.utils;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonSaverTest {

  @Test
  void testSaveJson() {
    JsonObject object = new JsonLoader().loadJson("src/test/resources/json/sample.json");
    new JsonSaver().saveJson("src/test/resources/json/sampleTest.json", object);

    assertEquals(object, new JsonLoader().loadJson("src/test/resources/json/sampleTest.json"));
  }

  @Test
  public void testFileNotFound() {
    assertThrows(
        RuntimeException.class,
        () -> new JsonSaver().saveJson("src/test/resources/jsoNN/not_found.json", null)
    );
  }

  @Test
  void testClearJson() {
    new JsonSaver().clearJson("src/test/resources/json/sampleTest.json");
    assertNull(new JsonLoader().loadJson("src/test/resources/json/sampleTest.json"));
  }

  @Test
  public void testClearJsonFileNotFound() {
    assertThrows(
        RuntimeException.class,
        () -> new JsonSaver().clearJson("src/test/resources/jsoNN/not_found.json")
    );
  }
}
