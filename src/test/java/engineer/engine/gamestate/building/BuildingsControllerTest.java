package engineer.engine.gamestate.building;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class BuildingsControllerTest {
  private AutoCloseable closeable;

  @Mock private BoardFactory boardFactory;
  @Mock private Board board;
  @Mock private Player player;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void getAllBuildingsList() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, board);
    buildingsController.getAllBuildingsList();
    verify(boardFactory).produceBuilding("Mayor's house", null);
  }

  @Test
  public void build() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, board);
    Coords coords = new Coords(3, 5);

    buildingsController.build(coords, "type", player);
    verify(boardFactory).build(board, coords, "type", player);
  }

  @Test
  public void destroyBuild() {
    BuildingsController buildingsController = new BuildingsController(boardFactory, board);
    Coords coords = new Coords(3, 5);

    buildingsController.destroyBuilding(coords);
    verify(boardFactory).destroyBuilding(board, coords);
  }
}
