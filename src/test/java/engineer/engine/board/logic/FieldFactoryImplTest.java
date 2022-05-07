package engineer.engine.board.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldFactoryImplTest {
    @Test
    public void testProduce() {
        FieldFactory factory = new FieldFactoryImpl();
        Field field = factory.produce("Texture name");

        assertNotNull(field);
        assertEquals("Texture name", field.getBackground());
        assertNull(field.getContent());
    }
}