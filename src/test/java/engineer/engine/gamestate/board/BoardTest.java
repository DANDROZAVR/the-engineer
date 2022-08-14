package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class BoardTest {
  private AutoCloseable closeable;

  @Mock private FieldFactory fieldFactory;
  @Mock private BuildingFactory buildingFactory;
  @Mock private Field standardField;
  @Mock private Building standardBuilding;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    when(fieldFactory.produce(anyString(), any(), any(), anyBoolean())).thenReturn(standardField);
    when(buildingFactory.produce(any())).thenReturn(standardBuilding);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testConstructor() {
    BoardFactory boardFactory = new BoardFactory(null, null);
    Board board = boardFactory.produceBoard(3, 5);
    assertEquals(3, board.getRows());
    assertEquals(5, board.getColumns());

    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < 5; column++) {
        assertNull(board.getField(new Coords(row, column)));
      }
    }
  }

  @Test
  public void testSetFields() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    Field field = fieldFactory.produce("background", null, null,false);

    board.setField(new Coords(0, 0), field);

    assertEquals(field, board.getField(new Coords(0, 0)));
    assertEquals(field, standardField);
  }

  @Test
  public void testFieldObservers() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    Board.Observer observer = Mockito.spy(new Board.Observer() {});

    board.addObserver(observer);
    board.setField(new Coords(1, 2), standardField);
    board.selectField(new Coords(0, 3));
    board.removeObserver(observer);
    board.setField(new Coords(1, 2), standardField);
    board.selectField(null);

    verify(observer).onFieldChanged(new Coords(1, 2));
    verify(observer).onSelectionChanged(new Coords(0, 3));
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testSelectingField() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    Board.Observer observer = mock(Board.Observer.class);

    assertNull(board.getSelectedCoords());

    board.addObserver(observer);
    board.selectField(new Coords(1, 2));

    assertEquals(new Coords(1, 2), board.getSelectedCoords());
    verify(observer).onSelectionChanged(new Coords(1, 2));
  }
}
