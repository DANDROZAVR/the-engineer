package engineer.engine.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FieldFactoryTest {
    @Test
    public void testProduce() {
        FieldFactory factory = new FieldFactory();
        assertInstanceOf(Field.class, factory.produce());
    }
}