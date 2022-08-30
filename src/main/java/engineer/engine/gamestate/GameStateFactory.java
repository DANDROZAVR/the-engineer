package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.building.BuildingsController;
import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.resource.ResourceFactory;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.JsonLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GameStateFactory {
  public ResourceFactory produceResourceFactory() {
    ResourceFactory resourceFactory = new ResourceFactory();
    resourceFactory.addResType("wood", "wood");
    resourceFactory.addResType("stone", "stone");
    return resourceFactory;
  }

  public BuildingFactory produceBuildingFactory(ResourceFactory resourceFactory) {
    BuildingFactory buildingFactory = new BuildingFactory();
    // TODO: add from all existing buildings types there
    buildingFactory.addBuildingType("Armorer House", "house", Collections.singletonList(resourceFactory.produce("wood").addResAmount(2)), 40);
    buildingFactory.addBuildingType("Mayor's house", "house2", Arrays.asList(resourceFactory.produce("wood").addResAmount(15), resourceFactory.produce("stone").addResAmount(24)), 50);
    return buildingFactory;
  }

  public BoardFactory produceBoardFactory(FieldFactory fieldFactory, BuildingFactory buildingFactory) {
    return new BoardFactory(fieldFactory, buildingFactory);
  }

  public Board produceBoard(BoardFactory boardFactory, String boardPath) {
    return boardFactory.produceBoard(new JsonLoader().loadJson(boardPath));
  }

  public FightSystem produceFightSystem() {
    return new FightSystem(new Random());
  }

  public List<Player> producePlayers(List<String> names, ResourceFactory resourceFactory) {
    List<Player> players = new LinkedList<>();
    for (String name : names)
      players.add(new Player(name));
    players.get(0).addResource(resourceFactory.produce("wood"));
    players.get(0).getResources().get(0).addResAmount(100);
    return players;
  }

  public TurnSystem produceTurnSystem(List<Player> players) {
    TurnSystem turnSystem = new TurnSystem(players);
    turnSystem.nextTurn();
    return turnSystem;
  }

  public Camera produceCamera(Board board, double boardViewWidth, double boardViewHeight) {
    return new Camera(board.getRows(), board.getColumns(), boardViewWidth, boardViewHeight);
  }

  public MobFactory produceMobFactory() {
    MobFactory mobFactory = new MobFactory();
    // TODO: temp, let's find normal names and textures
    mobFactory.addMobType("wood", "wood", 5, 6, 3);
    mobFactory.addMobType("exit", "stop", 3, 2, 6);
    return mobFactory;
  }

  public MobsController produceMobsController(Board board, TurnSystem turnSystem, MobFactory mobFactory, FightSystem fightSystem) {
    return new MobsController(board, turnSystem, mobFactory, fightSystem);
  }

  public BuildingsController produceBuildingController(BoardFactory boardFactory, Board board) {
    return new BuildingsController(boardFactory, board);
  }
}
