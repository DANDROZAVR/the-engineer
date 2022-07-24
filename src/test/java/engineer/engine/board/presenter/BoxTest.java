package engineer.engine.board.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BoxTest {
  @Test
  public void testGetBottomAndRight() {
    Box box = new Box(1, 2, 30, 40);

    assertEquals(box.right(), 31);
    assertEquals(box.bottom(), 42);
  }
}
