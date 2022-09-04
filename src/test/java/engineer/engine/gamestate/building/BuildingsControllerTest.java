package engineer.engine.gamestate.building;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BuildingsControllerTest {
  private AutoCloseable closeable;

  @Mock private BoardFactory boardFactory;
  @Mock private Board board;
  @Mock private Player player;
  @Mock private TurnSystem turnSystem;
  @Mock private BuildingFactory buildingFactory;
  @Mock private Building standardBuilding;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    doReturn(standardBuilding).when(buildingFactory).produce(any(String.class), any(Player.class));
    doReturn(player).when(standardBuilding).getOwner();
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void getAllBuildingsList() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, buildingFactory, board, turnSystem);
    buildingsController.getAllBuildingsList();
    verify(buildingFactory).produce("Mayor's house", null);
  }

  @Test
  public void build() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, buildingFactory, board, turnSystem);
    Coords coords = new Coords(3, 5);

    buildingsController.build(coords, "type", player);
    verify(boardFactory).build(board, coords, standardBuilding);
  }

  @Test
  public void testDestroyBuilding() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, buildingFactory, board, turnSystem);
    Coords coords = new Coords(3, 5);
    buildingsController.destroyBuilding(coords);
    verify(boardFactory).destroyBuilding(board, coords);
  }

  @Test
  public void testOnTurnChange() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, buildingFactory, board, turnSystem);

    buildingsController.onBuildingAdded(standardBuilding);
    buildingsController.onTurnChange(player);

    verify(standardBuilding).produceOnEndOfTurn();
    verify(standardBuilding).getOwner();

    buildingsController.onBuildingRemoved(standardBuilding);
    buildingsController.onTurnChange(player);

    verifyNoMoreInteractions(standardBuilding);
  }

  @Test
  public void testBuildingControllerConstructor() {
    Field field = mock(Field.class);
    Building building = mock(Building.class);
    doReturn(1).when(board).getRows();
    doReturn(1).when(board).getColumns();
    doReturn(field).when(board).getField(new Coords(0, 0));
    doReturn(building).when(field).getBuilding();
    doReturn(mock(Player.class)).when(building).getOwner();

    BuildingsController buildingsController = new BuildingsController(boardFactory, buildingFactory, board, turnSystem);
    verify(field, atLeastOnce()).getBuilding();

    buildingsController.onTurnChange(mock(Player.class));

    verify(building).getOwner();
    verifyNoMoreInteractions(building);
  }
}
