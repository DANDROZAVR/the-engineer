package engineer.engine.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FieldContentImplTest {
  @Test
  public void testGetPicture() {
    FieldContent fieldContent = new FieldContentImpl("Sample");

    assertEquals("Sample", fieldContent.getPicture());
  }
}
