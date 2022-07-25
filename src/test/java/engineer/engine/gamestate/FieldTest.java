// package engineer.engine.gamestate;
//
// import engineer.engine.gamestate.building.Building;
// import engineer.engine.gamestate.field.Field;
// import org.junit.jupiter.api.Test;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// class FieldTest {
//  @Test
//  public void testConstructor() {
//    Field field = new Field(null, false);
//    assertNull(field.getBackground());
//    assertFalse(field.isFree());
//
//    field = new Field("Texture name", true);
//    assertEquals("Texture name", field.getBackground());
//    assertTrue(field.isFree());
//  }
//
//  @Test
//  public void testSetContent() {
//    Field field = new Field(null, false);
//    Building content = new BuildingImpl("name") {};
//
//    assertNull(field.getContent());
//    field.setContent(content);
//    assertSame(content, field.getContent());
//  }
// }
