package engineer.engine.board.logic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FieldTest {
  @Test
  public void testConstructor() {
    Field field = new Field(null, false);
    assertNull(field.getBackground());
    assertFalse(field.isFree());

    field = new Field("Texture name", true);
    assertEquals("Texture name", field.getBackground());
    assertTrue(field.isFree());
  }

  @Test
  public void testSetContent() {
    Field field = new Field(null, false);
    FieldContent content = new FieldContentImpl("name") {};

    assertNull(field.getContent());
    field.setContent(content);
    assertSame(content, field.getContent());
  }
}
