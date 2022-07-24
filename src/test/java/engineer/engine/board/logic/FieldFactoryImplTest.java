package engineer.engine.board.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import engineer.engine.board.exceptions.TextureNotFoundException;
import engineer.gui.TextureManager;
import org.junit.jupiter.api.Test;

class FieldFactoryImplTest {
  @Test
  public void testProduce() {
    TextureManager tm = mock(TextureManager.class);
    doThrow(TextureNotFoundException.class).when(tm).loadTexture(anyString());
    doNothing().when(tm).loadTexture("tile");

    FieldFactory factory = new FieldFactoryImpl(tm);
    assertThrows(TextureNotFoundException.class, () -> factory.produce("Texture name", false));
    Field field = factory.produce("tile", false);

    assertNotNull(field);
    assertEquals("tile", field.getBackground());
    assertNull(field.getContent());
  }
}
