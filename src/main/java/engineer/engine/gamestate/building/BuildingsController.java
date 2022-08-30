package engineer.engine.gamestate.building;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;

import java.util.Arrays;
import java.util.List;

public class BuildingsController {
  private final BoardFactory boardFactory;
  private final Board board;
  public BuildingsController(BoardFactory boardFactory, Board board) {
    this.boardFactory = boardFactory;
    this.board = board;
  }
  public List<Building> getAllBuildingsList() {
    Building smallHouse = boardFactory.produceBuilding("Armorer House", null);
    Building bigHouse = boardFactory.produceBuilding("Mayor's house", null);
    return Arrays.asList(
        smallHouse, null, null, null, null, bigHouse);
  }

  public void build(Coords coords, String buildingType, Player owner) {
    boardFactory.build(board, coords, buildingType, owner);
  }
  public void destroyBuilding(Coords coords) {
    boardFactory.destroyBuilding(board, coords);
  }
}
