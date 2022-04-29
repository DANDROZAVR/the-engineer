package engineer.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
    @Test
    void getColumn() {
        int column = 2;
        Field f = new Field(2, column, new FieldContentForTest());
        assertEquals(column, f.getColumn());
    }
    @Test
    void getRow() {
        int row = 2;
        Field f = new Field(row, 5, new FieldContentForTest());
        assertEquals(row, f.getRow());
    }
    @Test
    void checkContextNotNull() {
        FieldContent context = new FieldContentForTest();
        Field f = new Field(2, 5, context);
        assertNotNull(f.getContent());
    }
    @Test
    void canBuild() {
        FieldContent context = new FieldContentForTest();
        Field f = new Field(2, 5, context);
        assertEquals(context.canBuild(), f.canBuild());
    }
}