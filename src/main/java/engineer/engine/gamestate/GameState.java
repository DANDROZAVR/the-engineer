package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.resource.ResourceFactory;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import engineer.utils.JsonLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GameState {
  private final BoardFactory boardFactory;
  @SuppressWarnings("FieldCanBeLocal")
  private final BuildingFactory buildingFactory;
  @SuppressWarnings("FieldCanBeLocal")
  private final ResourceFactory resourceFactory = new ResourceFactory();
  private final Camera camera;

  private final Board board;
  @SuppressWarnings("FieldCanBeLocal")
  private final MobsController mobsController;
  private final TurnSystem turnSystem;
  
  public GameState(double boardViewWidth, double boardViewHeight) {
    // TODO: temporary solution
    resourceFactory.addResType("wood", "wood");
    resourceFactory.addResType("stone", "stone");

    // TODO: temporary solution
    buildingFactory = new BuildingFactory();
    buildingFactory.addMobType("Armorer House", "house", Collections.singletonList(resourceFactory.produce("wood").addResAmount(2)));
    buildingFactory.addMobType("Mayor's house", "house2", Arrays.asList(resourceFactory.produce("wood").addResAmount(15), resourceFactory.produce("stone").addResAmount(24)));

    boardFactory = new BoardFactory(new FieldFactory(), buildingFactory);

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
    
    mobsController = new MobsController(board, turnSystem, mobFactory, fightSystem);
    mobsController.setMob(new Coords(3, 5), mobsController.produceMob("wood", 15, players.get(0)));
    mobsController.setMob(new Coords(8, 8), mobsController.produceMob("wood", 5, players.get(1)));
    mobsController.setMob(new Coords(2, 7), mobsController.produceMob("exit", 1, players.get(1)));


    turnSystem.getCurrentPlayer().addResource(resourceFactory.produce("wood"));
    turnSystem.getCurrentPlayer().getResources().get(0).addResAmount(100);
  }

  public void build(Coords coords, String type) {
    Field field = board.getField(coords);
    Field newField = boardFactory.produceField(
            field.getBackground(),
            boardFactory.produceBuilding(type),
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
  public MobsController getMobsController() {
    return mobsController;
  }

  public List<Building> getAllBuildingsList() {
    // TODO: TEMP. WE SHOULD IMPLEMENT HOUSES TO MOVE FORWARD
    Building smallHouseSchema = boardFactory.produceBuilding("Armorer House");
    Building bigHouseSchema = boardFactory.produceBuilding("Mayor's house");
    return Arrays.asList(
        smallHouseSchema, null, null, null, null, bigHouseSchema);
  }
}
