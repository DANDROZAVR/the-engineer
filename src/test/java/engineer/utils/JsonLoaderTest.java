package engineer.utils;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonLoaderTest {
  @Test
  public void testLoadJson() {
    JsonObject object = new JsonLoader().loadJson("src/test/resources/json/sample.json");
    assertEquals("value", object.get("name").getAsString());
  }

  @Test
  public void testFileNotFound() {
    assertThrows(
            RuntimeException.class,
            () -> new JsonLoader().loadJson("src/test/resources/json/not_found.json")
    );
  }

  @Test
  public void testMalformedFile() {
    assertThrows(
            RuntimeException.class,
            () -> new JsonLoader().loadJson("src/test/resources/json/malformed.json")
    );
  }
}
