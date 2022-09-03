package engineer.engine.gamestate.board;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.JsonLoader;
import engineer.utils.Coords;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BoardTest {
  private AutoCloseable closeable;

  @Mock private FieldFactory fieldFactory;
  @Mock private BuildingFactory buildingFactory;
  @Mock private MobFactory mobFactory;
  private final List<Player> players = new LinkedList<>();

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
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-3-5.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
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
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-3-5.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
    Field field = fieldFactory.produce("background", null, null,false);

    board.setField(new Coords(0, 0), field);

    assertEquals(field, board.getField(new Coords(0, 0)));
  }

  @Test
  public void testFieldObservers() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-3-5.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
    Board.Observer observer = Mockito.spy(new Board.Observer() {});
    Field field = mock(Field.class);
    Mob mob = mock(Mob.class);
    doReturn(mob).when(field).getMob();

    board.addObserver(observer);
    board.setField(new Coords(1, 2), field);
    board.selectField(new Coords(0, 3));
    board.removeObserver(observer);
    board.setField(new Coords(1, 2), field);
    board.selectField(null);

    verify(observer).onFieldChanged(new Coords(1, 2));
    verify(observer).onMobAdded(mob);
    verify(observer).onSelectionChanged(new Coords(0, 3));
    verifyNoMoreInteractions(observer);

    board.addObserver(observer);
    board.setField(new Coords(1, 2), field);

    verify(observer).onMobRemoved(mob);
  }

  @Test
  public void testSelectingField() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-3-5.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
    Board.Observer observer = mock(Board.Observer.class);

    assertNull(board.getSelectedCoords());

    board.addObserver(observer);
    board.selectField(new Coords(1, 2));

    assertEquals(new Coords(1, 2), board.getSelectedCoords());
    verify(observer).onSelectionChanged(new Coords(1, 2));
  }

  @Test
  public void testGetNearestFields() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-2-4.json");
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
    Field blockedField = boardFactory.produceField(null, null, null, false);
    board.setField(new Coords(0, 1), blockedField);

    Mob mob1 = mock(Mob.class);
    Player player1 = mock(Player.class);
    Player player2 = mock(Player.class);
    doReturn(mob1).when(board.getField(new Coords(1, 2))).getMob();
    doReturn(player1).when(mob1).getOwner();
    doReturn(player2).when(board.getField(new Coords(1, 3))).getOwner();

    assertNull(board.getNearestFields(new Coords(0, 1), 5));
    Collection<Coords> pair = board.getNearestFields(new Coords(1, 2), 2);
    assertThat(pair).containsExactlyInAnyOrder(
        new Coords(0, 2),
        new Coords(0, 3),
        new Coords(1, 0),
        new Coords(1, 1),
        new Coords(1, 2)
    );
    //assertThat(pair).containsExactlyInAnyOrder(new Coords(1, 3));
  }

  @Test
  public void testGetPath() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-2-4.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);

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
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-6-6.json");
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);

    assertThat(board.getMarkedFieldsToMove()).isEmpty();

    board.markFields(List.of(
        new Coords(3, 5),
        new Coords(3, 5),
        new Coords(2, 1)
    ), List.of(
        new Coords(1, 11),
        new Coords(12, 2)
    ));
    board.markFields(null, null);

    assertThat(board.getMarkedFieldsToMove()).containsExactlyInAnyOrder(
        new Coords(3, 5),
        new Coords(2, 1)
    );
    assertThat(board.getMarkedFieldsToAttack()).containsExactlyInAnyOrder(
        new Coords(1, 11),
        new Coords(12, 2)
    );

    board.unmarkAllFields();

    assertThat(board.getMarkedFieldsToMove()).isEmpty();
  }
}
