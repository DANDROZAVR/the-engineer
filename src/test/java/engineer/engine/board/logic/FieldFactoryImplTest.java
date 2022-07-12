package engineer.engine.board.logic;

import engineer.engine.board.exceptions.TextureNotKnownException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldFactoryImplTest {
    @Test
    public void testProduce() {
        FieldFactory factory = new FieldFactoryImpl();
        assertThrows(TextureNotKnownException.class, () -> factory.produce("Texture name"));
        Field field = factory.produce("tile");

        assertNotNull(field);
        assertEquals("tile", field.getBackground());
        assertNull(field.getContent());
    }
}