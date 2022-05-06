package engineer.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FieldTest {
    @Test
    public void testSetContent() {
        Field field = new Field();
        FieldContent content = new FieldContent() {};

        assertNull(field.getContent());
        field.setContent(content);
        assertSame(content, field.getContent());
    }
}