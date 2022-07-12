package engineer.engine.board.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
    @Test
    public void testGetBackground() {
        Field field = new Field(null);
        assertNull(field.getBackground());

        field = new Field("Texture name");
        assertEquals("Texture name", field.getBackground());
    }

    @Test
    public void testSetContent() {
        Field field = new Field(null);
        FieldContent content = new FieldContentImpl("name") {};

        assertNull(field.getContent());
        field.setContent(content);
        assertSame(content, field.getContent());
    }

    @Test
    public void testSetBackgroundFeatures() {
        Field field = new Field("Texture name");
        assertEquals(1, field.getNumberOfMovesNeeded());
        assertTrue(field.getBuildingEnabled());
        field.setNumberOfMovesNeeded(4);
        field.setBuildingEnabled(false);
        assertEquals(4, field.getNumberOfMovesNeeded());
        assertFalse(field.getBuildingEnabled());

    }
}