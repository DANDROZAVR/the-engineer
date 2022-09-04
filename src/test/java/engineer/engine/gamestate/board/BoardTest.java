package engineer.engine.gamestate.board;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.GameStateFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.resource.ResourceFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.JsonLoader;
import engineer.utils.Coords;
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
  @Mock private Building castle;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    doAnswer(invocation -> {
      Field field = mock(Field.class);
      doReturn(invocation.getArgument(0)).when(field).getBackground();
      doReturn(invocation.getArgument(1)).when(field).getBuilding();
      doReturn(invocation.getArgument(2)).when(field).getMob();
      return field;
    }).when(fieldFactory).produce(nullable(String.class), nullable(Building.class), nullable(Mob.class));
    when(castle.getType()).thenReturn("Castle");
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
      }
    }
  }



  @Test
  public void testSetFields() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-3-5.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
    Field field = fieldFactory.produce("background", null, null);

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
    Field emptyField = mock(Field.class);
    Mob mob = mock(Mob.class);
    Building building = mock(Building.class);
    doReturn(mob).when(field).getMob();
    doReturn(building).when(field).getBuilding();

    board.addObserver(observer);
    board.setField(new Coords(1, 2), field);
    board.selectField(new Coords(0, 3));
    board.setField(new Coords(1, 2), emptyField);
    board.removeObserver(observer);
    board.setField(new Coords(1, 2), field);
    board.selectField(null);

    verify(observer, times(2)).onFieldChanged(new Coords(1, 2));
    verify(observer).onMobAdded(mob);
    verify(observer).onBuildingAdded(building);
    verify(observer).onMobRemoved(mob);
    verify(observer).onBuildingRemoved(building);
    verify(observer).onSelectionChanged(new Coords(0, 3));
    verifyNoMoreInteractions(observer);
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
  public void testEndGame() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    ResourceFactory resourceFactory = gameStateFactory.produceResourceFactory();
    BoardFactory boardFactory = gameStateFactory.produceBoardFactory(new FieldFactory());
    MobFactory mobFactory = gameStateFactory.produceMobFactory(resourceFactory);
    BuildingFactory buildingFactory = gameStateFactory.produceBuildingFactory(resourceFactory, mobFactory);

    Board board = gameStateFactory.produceBoard(boardFactory, "src/test/resources/board/mock-3-5.json", buildingFactory, mobFactory, players);
    Board.Observer observer = Mockito.spy(new Board.Observer() {});
    board.addObserver(observer);

    board.setField(new Coords(0, 0), fieldFactory.produce("background", null, null));

    verify(observer).onGameEnded();
  }


  @Test
  public void testGetNearestFields() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-2-4.json");
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);

    Mob mob1 = mock(Mob.class);
    Player player1 = mock(Player.class);
    Player player2 = mock(Player.class);
    doReturn(mob1).when(board.getField(new Coords(1, 2))).getMob();
    doReturn(player1).when(mob1).getOwner();
    doReturn(player2).when(board.getField(new Coords(1, 3))).getOwner();

    Collection<Coords> pair = board.getNearestFields(new Coords(1, 2), 2);
    assertThat(pair).containsExactlyInAnyOrder(
        new Coords(0, 2),
        new Coords(0, 3),
        new Coords(0, 1),
        new Coords(1, 0),
        new Coords(1, 1),
        new Coords(1, 2)
    );
  }

  @Test
  public void testGetPath() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-2-4.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);

    assertThat(board.findPath(new Coords(1, 0), new Coords(0, 1)))
        .containsExactly(
            new Coords(1, 0),
            new Coords(1, 1),
            new Coords(0, 1)
        );
  }

  @Test
  public void testGetPathNotAccessible() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-2-4.json");

    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);

    Mob mob1 = mock(Mob.class);
    Player player1 = mock(Player.class);
    Player player2 = mock(Player.class);
    doReturn(mob1).when(board.getField(new Coords(1, 2))).getMob();
    doReturn(player1).when(mob1).getOwner();
    doReturn(player2).when(board.getField(new Coords(1, 1))).getOwner();
    doReturn(player2).when(board.getField(new Coords(0, 1))).getOwner();

    assertNull(board.findPath(new Coords(1, 2), new Coords(0, 0)));
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

  @Test
  public void testGetFieldsToAttack() {
    JsonObject boardJson = new JsonLoader().loadJson("src/test/resources/board/mock-6-6.json");
    BoardFactory boardFactory = new BoardFactory(fieldFactory);
    Coords coords = new Coords(1, 1);
    Player player1 = new Player("nick1");
    Player player2 = new Player("nick2");
    List<Player> players = List.of(player1, player2);
    Board board = boardFactory.produceBoard(boardJson, buildingFactory, mobFactory, players);
    Field field = mock(Field.class);

    board.setField(new Coords(1, 2), field);
    doReturn(player2).when(field).getOwner();

    Collection<Coords> fields = board.getFieldsToAttack(coords, player1);
    assertEquals(List.of(new Coords(1, 2)), fields);
  }
}
