package engineer.engine.board;

import engineer.engine.board.exceptions.IndexOutOfBoardException;
import engineer.engine.board.exceptions.InvalidBoardDescriptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardTest {
    private record Description(int width, int height) implements BoardDescription {
        @Override
        public int getWidth() { return width; }
        @Override
        public int getHeight() { return height; }
    }

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> {
            BoardDescription d1 = new Description(3, 5);
            Board board = new Board(d1);
            assertEquals(3, board.getWidth());
            assertEquals(5, board.getHeight());
        });

        BoardDescription d2 = new Description(0, 7);
        assertThrows(InvalidBoardDescriptionException.class, () -> new Board(d2));
    }

    @Test
    public void testGetField() {
        try {
            Board board = new Board(new Description(4, 5));

            for (int row=0;row<4;row++)
                for (int column=0;column<5;column++) {
                    int finalRow = row;
                    int finalColumn = column;
                    assertDoesNotThrow(() -> board.getField(finalRow, finalColumn));
                }

            assertThrows(IndexOutOfBoardException.class, () -> board.getField(-1, 3));
            assertThrows(IndexOutOfBoardException.class, () -> board.getField(4, 3));
            assertThrows(IndexOutOfBoardException.class, () -> board.getField(2, -2));
            assertThrows(IndexOutOfBoardException.class, () -> board.getField(1, 6));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testObservers() {
        try {
            Board board = new Board(new Description(5, 8));
            Board.Observer observer = mock(Board.Observer.class);

            board.addObserver(observer);
            board.setFieldContent(1, 2, null);
            board.removeObserver(observer);
            board.setFieldContent(4, 3, null);

            verify(observer).onFieldChanged(1, 2);
            verifyNoMoreInteractions(observer);
        } catch (Exception e) {
            fail(e);
        }
    }
}