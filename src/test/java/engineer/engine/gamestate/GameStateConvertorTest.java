package engineer.engine.gamestate;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.resource.ResourceFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.JsonLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateConvertorTest {

  @Test
  void testProduceJsonFromBoard() {
    GameStateFactory gameStateFactory = new GameStateFactory();
    ResourceFactory resourceFactory = gameStateFactory.produceResourceFactory();
    BoardFactory boardFactory = gameStateFactory.produceBoardFactory(new FieldFactory());
    MobFactory mobFactory = gameStateFactory.produceMobFactory(resourceFactory);
    BuildingFactory buildingFactory = gameStateFactory.produceBuildingFactory(resourceFactory, mobFactory);

    /* We assume GameState is well-tested here. It will help us not to configure all factories by hand
       We can imagine that all json constructors are parts of one big class. so we can create its instances" */

    String pathJsonBoard = "src/test/resources/board/mock-1-4_buildings_and_mobs.json";
    JsonObject loadedJsonBoard = new JsonLoader().loadJson(pathJsonBoard);
    List<Player> players = gameStateFactory.producePlayers(pathJsonBoard, resourceFactory);
    Board board = boardFactory.produceBoard(loadedJsonBoard, buildingFactory, mobFactory, players);

    JsonObject jsonBoard = GameStateConvertor.produceJsonFromBoard(board, players);
    assertEquals(loadedJsonBoard, jsonBoard);
  }
}
