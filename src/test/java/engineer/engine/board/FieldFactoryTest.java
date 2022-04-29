package engineer.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldFactoryTest {

    @Test
    void getEmptyBuildableField() {
        FieldFactory fieldFactory = new FieldFactory();
        int row = 2, column = 5;
        Field field = fieldFactory.getEmptyBuildableField(row, column);
        assertEquals(row, field.getRow());
        assertEquals(column, field.getColumn());
        assertTrue(field.canBuild());
    }
    @Test
    void getEmptyNotBuildableField() {
        FieldFactory fieldFactory = new FieldFactory();
        int row = 23, column = 51;
        Field field = fieldFactory.getEmptyNotBuildableField(row, column);
        assertEquals(row, field.getRow());
        assertEquals(column, field.getColumn());
        assertFalse(field.canBuild());
    }
}