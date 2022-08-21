package engineer.utils;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonLoaderTest {
  @Test
  public void testLoadJson() {
    JsonObject object = new JsonLoader().loadJson("/json/sample.json");
    assertEquals("value", object.get("name").getAsString());
  }

  @Test
  public void testFileNotFound() {
    assertThrows(
            RuntimeException.class,
            () -> new JsonLoader().loadJson("/json/not_found.json")
    );
  }

  @Test
  public void testMalformedFile() {
    assertThrows(
            RuntimeException.class,
            () -> new JsonLoader().loadJson("/json/malformed.json")
    );
  }
}
