package engineer.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldContentImpTest {

    @Test
    void canBuildTrue() {
        FieldContentImp fieldContent = new FieldContentImp(true);
        assertTrue(fieldContent.canBuild());
    }
    @Test
    void canBuildFalse() {
        FieldContentImp fieldContent = new FieldContentImp(false);
        assertFalse(fieldContent.canBuild());
    }

}