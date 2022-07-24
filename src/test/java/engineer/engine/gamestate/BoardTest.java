package engineer.engine.gamestate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import engineer.engine.exceptions.IndexOutOfBoardException;
import engineer.engine.exceptions.InvalidBoardDescriptionException;
import org.junit.jupiter.api.Test;

public class BoardTest {
  public record SampleDescription(int rows, int columns) implements BoardDescription {
    @Override
    public int getRows() {
      return rows;
    }

    @Override
    public int getColumns() {
      return columns;
    }

    @Override
    public String getBackground(int row, int column) {
      return String.format("Row %d; Column %d", row, column);
    }
  }

  public static class SampleFieldFactory implements FieldFactory {
    @Override
    public Field produce(String background, boolean free) {
      return new Field(background, free);
    }
  }

  @Test
  public void testConstructor() {
    BoardDescription d1 = new SampleDescription(3, 5);
    FieldFactory factory = mock(FieldFactory.class);
    Board board = new Board(factory, d1);

    assertEquals(3, board.getRows());
    assertEquals(5, board.getColumns());
    for (int row = 0; row < 3; row++)
      for (int column = 0; column < 5; column++)
        verify(factory).produce(d1.getBackground(row, column), true);
    verifyNoMoreInteractions(factory);

    BoardDescription d2 = new SampleDescription(0, 7);
    assertThrows(InvalidBoardDescriptionException.class, () -> new Board(factory, d2));
  }

  @Test
  public void testGetField() {
    Board board = new Board(new SampleFieldFactory(), new SampleDescription(4, 5));

    for (int row = 0; row < 4; row++)
      for (int column = 0; column < 5; column++) {
        int finalRow = row;
        int finalColumn = column;
        assertDoesNotThrow(() -> board.getField(finalRow, finalColumn));
      }

    assertThrows(IndexOutOfBoardException.class, () -> board.getField(-1, 3));
    assertThrows(IndexOutOfBoardException.class, () -> board.getField(4, 3));
    assertThrows(IndexOutOfBoardException.class, () -> board.getField(2, -2));
    assertThrows(IndexOutOfBoardException.class, () -> board.getField(1, 6));
  }

  @Test
  public void testObservers() {
    Board board = new Board(new SampleFieldFactory(), new SampleDescription(5, 8));
    Board.Observer observer = mock(Board.Observer.class);

    board.addObserver(observer);
    board.setFieldContent(1, 2, null);
    board.removeObserver(observer);
    board.setFieldContent(4, 3, null);

    verify(observer).onFieldChanged(1, 2);
    verifyNoMoreInteractions(observer);
  }
}
