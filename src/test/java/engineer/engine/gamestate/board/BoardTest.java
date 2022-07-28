package engineer.engine.gamestate.board;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BoardTest {
  @Mock private FieldFactory fieldFactory;
  @Mock private BuildingFactory buildingFactory;
  @Mock private Field standardField;
  @Mock private Building standardBuilding;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(fieldFactory.produce(anyString(), any(), anyBoolean())).thenReturn(standardField);
    when(buildingFactory.produce(any())).thenReturn(standardBuilding);
  }

  @Test
  public void testConstructor() {
    BoardFactory boardFactory = new BoardFactory(null, null);
    Board board = boardFactory.produceBoard(3, 5);
    assertEquals(3, board.getRows());
    assertEquals(5, board.getColumns());

    for (int row = 0; row < 3; row++)
      for (int column = 0; column < 5; column++) assertNull(board.getField(row, column));
  }

  /*@Test
  public void testGetField() {
    FieldFactory fieldFactory = new FieldFactory();
    BuildingFactory buildingFactory = new BuildingFactory();
    BoardFactory boardFactory = new BoardFactory(fieldFactory, buildingFactory);
    Board board = boardFactory.produceBoard(4, 5);

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
  }*/

  @Test
  public void testSetFields() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    Field field = fieldFactory.produce("background", null, false);

    board.setField(0, 0, field);

    assertEquals(field, board.getField(0, 0));
    assertEquals(field, standardField);
  }

  @Test
  public void testObservers() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    Board.Observer observer = mock(Board.Observer.class);

    board.addObserver(observer);
    board.setField(1, 2, standardField);
    board.removeObserver(observer);
    board.setField(1, 2, standardField);

    verify(observer).onFieldChanged(1, 2);
    verifyNoMoreInteractions(observer);
  }
}
