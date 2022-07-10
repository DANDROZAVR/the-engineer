package engineer.engine.board.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldContentImplTest {
    @Test
    public void testGetPicture() {
        FieldContent fieldContent = new FieldContentImpl("Sample");

        assertEquals("Sample", fieldContent.getPicture());
    }
}