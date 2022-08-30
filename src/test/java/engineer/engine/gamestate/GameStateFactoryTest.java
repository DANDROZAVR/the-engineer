package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.resource.ResourceFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class GameStateFactoryTest {
  private AutoCloseable closeable;
  @Mock ResourceFactory resourceFactory;
  @Mock FieldFactory fieldFactory;
  @Mock BuildingFactory buildingFactory;
  @Mock Board board;
  @Mock TurnSystem turnSystem;
  @Mock MobFactory mobFactory;
  @Mock FightSystem fightSystem;
  @Mock BoardFactory boardFactory;
  @Mock Resource resource;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    doReturn(resource).when(resourceFactory).produce(any());
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void testProduceResourceFactory() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    ResourceFactory resourceFactory = gameStateFactory.produceResourceFactory();
    Resource resource = resourceFactory.produce("wood");
    assertEquals("wood", resource.getTexture());
  }

  @Test
  void testProduceBuildingFactory() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    BuildingFactory buildingFactory = gameStateFactory.produceBuildingFactory(resourceFactory);
    Building building = buildingFactory.produce("Armorer House", null);
    assertEquals("house", building.getTexture());
  }

  @Test
  void testProduceBoardFactory() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    assertNotNull(gameStateFactory.produceBoardFactory(fieldFactory, buildingFactory));
  }

  @Test
  void testProduceBoard() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    assertThrows(
        RuntimeException.class,
        () -> gameStateFactory.produceBoard(boardFactory, "not found")
    );
  }

  @Test
  void testProduceFightSystem() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    assertNotNull(gameStateFactory.produceFightSystem());
  }

  @Test
  void testProducePlayers() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    List<Player> players = gameStateFactory.producePlayers(List.of("one", "another"), resourceFactory);
    assertEquals("one", players.get(0).getNickname());
    assertEquals("another", players.get(1).getNickname());
  }

  @Test
  void testProduceTurnSystem() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    List<Player> players = List.of(mock(Player.class), mock(Player.class));
    assertNotNull(gameStateFactory.produceTurnSystem(players));
  }

  @Test
  void testProduceCamera() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    assertNotNull(gameStateFactory.produceCamera(board, 10, 20));
  }

  @Test
  void testProduceMobFactory() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    MobFactory mobFactory = gameStateFactory.produceMobFactory();
    assertEquals("wood", mobFactory.produce("wood", 1, null).getTexture());
  }

  @Test
  void testProduceMobsController() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    assertNotNull(gameStateFactory.produceMobsController(board, turnSystem, mobFactory, fightSystem));
  }

  @Test
  void testProduceBuildingController() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    assertNotNull(gameStateFactory.produceBuildingController(boardFactory, board));
  }
}
