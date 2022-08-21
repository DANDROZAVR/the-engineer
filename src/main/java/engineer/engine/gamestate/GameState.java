package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import engineer.utils.JsonLoader;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GameState {
  private final BoardFactory boardFactory;
  private final Camera camera;

  private final Board board;
  @SuppressWarnings("FieldCanBeLocal")
  private final MobsController mobsController;
  private final TurnSystem turnSystem;

  public GameState(double boardViewWidth, double boardViewHeight) {
    boardFactory = new BoardFactory(new FieldFactory(), new BuildingFactory());

    // TODO: temporary solution
    board = boardFactory.produceBoard(new JsonLoader().loadJson("/board/sample.json"));

    // TODO: temporary solution
    List<Player> players = new LinkedList<>();
    players.add(new Player("Winner"));
    players.add(new Player("Loser"));
    turnSystem = new TurnSystem(players);
    turnSystem.nextTurn();

    camera = new Camera(board.getRows(), board.getColumns(), boardViewWidth, boardViewHeight);

    // TODO: temporary solution
    MobFactory mobFactory = new MobFactory();
    mobFactory.addMobType("wood", "wood", 5);
    mobFactory.addMobType("exit", "stop", 3);

    mobsController = new MobsController(board, turnSystem, mobFactory);
    mobsController.setMob(new Coords(3, 5), mobsController.produceMob("wood", 15));
    mobsController.setMob(new Coords(8, 8), mobsController.produceMob("wood", 5));
    mobsController.setMob(new Coords(2, 7), mobsController.produceMob("exit", 1));
  }

  public void build(Coords coords, String building) {
    Field field = board.getField(coords);
    Field newField = boardFactory.produceField(
            field.getBackground(),
            boardFactory.produceBuilding(building),
            field.getMob(),
            field.isFree()
    );

    board.setField(coords, newField);
  }

  public Board getBoard() {
    return board;
  }

  public Camera getCamera() {
    return camera;
  }

  public TurnSystem getTurnSystem() {
    return turnSystem;
  }

  public List<Building> getAllBuildingsList() {
    // TODO: TEMP. WE SHOULD IMPLEMENT HOUSES TO MOVE FORWARD
    return Arrays.asList(
        boardFactory.produceBuilding("house"), null, null, null, null,
        boardFactory.produceBuilding("house2"));
  }
}
