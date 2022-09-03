package engineer.engine.gamestate.building;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
  void testDestroyBuilding() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, buildingFactory, board, turnSystem);
    Coords coords = new Coords(3, 5);
    buildingsController.destroyBuilding(coords);
    verify(boardFactory).destroyBuilding(board, coords);
  }
}
