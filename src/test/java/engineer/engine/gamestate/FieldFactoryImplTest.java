package engineer.engine.gamestate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FieldFactoryImplTest {
  @Test
  public void testProduce() {
    FieldFactory factory = new FieldFactoryImpl();
    assertNotNull(factory.produce("tile", false));
  }
}
