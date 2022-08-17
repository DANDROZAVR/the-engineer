package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoardTest {
  private AutoCloseable closeable;

  @Mock private FieldFactory fieldFactory;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    doAnswer(invocation -> {
      Field field = mock(Field.class);
      doReturn(invocation.getArgument(0)).when(field).getBackground();
      doReturn(invocation.getArgument(1)).when(field).getBuilding();
      doReturn(invocation.getArgument(2)).when(field).getMob();
      doReturn(invocation.getArgument(3)).when(field).isFree();
      return field;
    }).when(fieldFactory).produce(nullable(String.class), nullable(Building.class), nullable(Mob.class), anyBoolean());
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testConstructor() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    assertEquals(3, board.getRows());
    assertEquals(5, board.getColumns());

    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < 5; column++) {
        assertNotNull(board.getField(new Coords(row, column)));
        assertTrue(board.getField(new Coords(row, column)).isFree());
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
  }

  @Test
  public void testFieldObservers() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(3, 5);
    Board.Observer observer = Mockito.spy(new Board.Observer() {});
    Field field = mock(Field.class);

    board.addObserver(observer);
    board.setField(new Coords(1, 2), field);
    board.selectField(new Coords(0, 3));
    board.removeObserver(observer);
    board.setField(new Coords(1, 2), field);
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

  @Test
  public void testGetNearestFields() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(2, 4);

    Field blockedField = boardFactory.produceField(null, null, null, false);
    board.setField(new Coords(0, 1), blockedField);

    assertNull(board.getNearestFields(new Coords(0, 1), 5));
    assertThat(board.getNearestFields(new Coords(1, 2), 2))
            .containsExactlyInAnyOrder(
                    new Coords(0, 2),
                    new Coords(0, 3),
                    new Coords(1, 0),
                    new Coords(1, 1),
                    new Coords(1, 2),
                    new Coords(1, 3)
            );
  }

  @Test
  public void testGetPath() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(2, 2);

    Field blockedField = boardFactory.produceField(null, null, null, false);
    board.setField(new Coords(0, 0), blockedField);

    assertThat(board.findPath(new Coords(1, 0), new Coords(0, 1)))
            .containsExactly(
                    new Coords(1, 0),
                    new Coords(1, 1),
                    new Coords(0, 1)
            );

    board.setField(new Coords(1, 1), blockedField);

    assertNull(board.findPath(new Coords(1, 0), new Coords(0, 1)));
  }

  @Test
  public void testMarkFields() {
    BoardFactory boardFactory = new BoardFactory(fieldFactory, null);
    Board board = boardFactory.produceBoard(10, 10);

    assertThat(board.getMarkedFields()).isEmpty();

    board.markFields(List.of(
            new Coords(3, 5),
            new Coords(3, 5),
            new Coords(2, 1)
    ));
    board.markFields(null);

    assertThat(board.getMarkedFields()).containsExactlyInAnyOrder(
            new Coords(3, 5),
            new Coords(2, 1)
    );

    board.unmarkAllFields();

    assertThat(board.getMarkedFields()).isEmpty();
  }
}
