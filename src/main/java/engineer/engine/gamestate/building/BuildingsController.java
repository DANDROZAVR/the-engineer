package engineer.engine.gamestate.building;

import com.google.gson.JsonObject;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;

import java.util.Arrays;
import java.util.List;

public class BuildingsController {
  private final BuildingFactory buildingFactory;
  private final BoardFactory boardFactory;
  private final Board board;
  public BuildingsController(BoardFactory boardFactory, BuildingFactory buildingFactory, Board board) {
    this.boardFactory = boardFactory;
    this.buildingFactory = buildingFactory;
    this.board = board;

    parseBuildings(board);
  }
  public List<Building> getAllBuildingsList() {
    Building smallHouse = buildingFactory.produce("Armorer House", null);
    Building bigHouse = buildingFactory.produce("Mayor's house", null);
    return Arrays.asList(
        smallHouse, null, null, null, null, bigHouse);
  }

  public Building produceBuilding(JsonObject jsonBuilding, List<Player> players) {
    return buildingFactory.produce(jsonBuilding, players);
  }

  public void build(Coords coords, String buildingType, Player owner) {
    Building building = buildingFactory.produce(buildingType, owner);
    boardFactory.build(board, coords, building);
  }

  public void destroyBuilding(Coords coords) {
    boardFactory.destroyBuilding(board, coords);
  }

  private void parseBuildings(Board board) {
    for (int i = 0; i < board.getRows(); ++i)
      for (int j = 0; j < board.getColumns(); ++j) {
        Field field = board.getField(new Coords(i, j));
        field.getBuilding();
        // addBuilding
      }

  }
}
