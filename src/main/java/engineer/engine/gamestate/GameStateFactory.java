package engineer.engine.gamestate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public class GameStateFactory {
  public ResourceFactory produceResourceFactory() {
    ResourceFactory resourceFactory = new ResourceFactory();
    resourceFactory.addResType("wood", "resources/wood");
    resourceFactory.addResType("stone", "resources/stone");
    resourceFactory.addResType("heart", "resources/heart");
    return resourceFactory;
  }

  public BuildingFactory produceBuildingFactory(ResourceFactory resourceFactory, MobFactory mobFactory) {
    BuildingFactory buildingFactory = new BuildingFactory();
    buildingFactory.addBuildingType("Armorer House", "buildings/house",
        Collections.singletonList(resourceFactory.produce("wood").addResAmount(3)),
        Collections.singletonList(resourceFactory.produce("wood").addResAmount(2)),
        Collections.singletonList(resourceFactory.produce("wood").addResAmount(4)),
        40, mobFactory.produce("Dino", 0, null)
    );
    buildingFactory.addBuildingType("Mayor's house", "buildings/house2",
        Arrays.asList(resourceFactory.produce("wood").addResAmount(15), resourceFactory.produce("stone").addResAmount(24)),
        Collections.singletonList(resourceFactory.produce("stone").addResAmount(3)),
        Collections.singletonList(resourceFactory.produce("wood").addResAmount(3)),
        50, mobFactory.produce("Mushroomed", 0, null)
    );
    buildingFactory.addBuildingType("The Home", "buildings/nice_house",
        Arrays.asList(resourceFactory.produce("wood").addResAmount(12), resourceFactory.produce("stone").addResAmount(5)),
        Collections.singletonList(resourceFactory.produce("heart").addResAmount(1)),
        Arrays.asList(resourceFactory.produce("heart").addResAmount(2), resourceFactory.produce("wood").addResAmount(8)),
        20, mobFactory.produce("Mr Bird", 0, null)
    );
    buildingFactory.addBuildingType("Wall", "buildings/wall",
        Collections.singletonList(resourceFactory.produce("stone").addResAmount(3)),
        Collections.emptyList(),
        Collections.singletonList(resourceFactory.produce("stone").addResAmount(5)),
        8, mobFactory.produce("Foxolino", 0, null)
    );
    buildingFactory.addBuildingType("Pyramid", "buildings/pyramid",
        Arrays.asList(resourceFactory.produce("wood").addResAmount(2), resourceFactory.produce("stone").addResAmount(25)),
        Collections.singletonList(resourceFactory.produce("stone").addResAmount(1)),
        Arrays.asList(resourceFactory.produce("heart").addResAmount(3), resourceFactory.produce("stone").addResAmount(11)),
        120, mobFactory.produce("Ms Bat", 0, null)
    );
    buildingFactory.addBuildingType("Castle", "buildings/castle",
        Arrays.asList(resourceFactory.produce("wood").addResAmount(999), resourceFactory.produce("stone").addResAmount(999)),
        Arrays.asList(resourceFactory.produce("wood").addResAmount(3), resourceFactory.produce("stone").addResAmount(1)),
        Arrays.asList(resourceFactory.produce("wood").addResAmount(5), resourceFactory.produce("stone").addResAmount(3)),
        120, mobFactory.produce("Dragonion", 0, null)
    );

    return buildingFactory;
  }

  public BoardFactory produceBoardFactory(FieldFactory fieldFactory) {
    return new BoardFactory(fieldFactory);
  }

  public Board produceBoard(BoardFactory boardFactory, String boardPath, BuildingFactory buildingFactory, MobFactory mobFactory, List<Player> players) {
    return boardFactory.produceBoard(new JsonLoader().loadJson(boardPath), buildingFactory, mobFactory, players);
  }

  public FightSystem produceFightSystem() {
    return new FightSystem();
  }

  public List<Player> producePlayers(String boardPath, ResourceFactory resourceFactory) {
    JsonObject jsonPlayers = new JsonLoader().loadJson(boardPath);
    List<Player> players = new LinkedList<>();
    for (JsonElement jsonElement : jsonPlayers.get("players").getAsJsonArray()) {
      Player player = new Player(jsonElement.getAsJsonObject(), resourceFactory);
      player.addResource(resourceFactory.produce("wood"));
      player.addResource(resourceFactory.produce("stone"));
      player.addResource(resourceFactory.produce("heart"));
      players.add(player);
    }
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

  public MobFactory produceMobFactory(ResourceFactory resourceFactory) {
    MobFactory mobFactory = new MobFactory();
    mobFactory.addMobType("Dino", "mobs/dino", 5, 6, 7, Arrays.asList(resourceFactory.produce("heart").addResAmount(1), resourceFactory.produce("wood").addResAmount(2)));
    mobFactory.addMobType("Mushroomed", "mobs/mushroom", 3, 2, 6, Collections.singletonList(resourceFactory.produce("wood").addResAmount(2)));
    mobFactory.addMobType("Mr Bird", "mobs/bird", 7, 1, 1, Collections.singletonList(resourceFactory.produce("heart").addResAmount(1)));
    mobFactory.addMobType("Ms Bat", "mobs/bat", 3, 3, 10, Collections.singletonList(resourceFactory.produce("heart").addResAmount(2)));
    mobFactory.addMobType("Foxolino", "mobs/fox", 10, 1, 1, Arrays.asList(resourceFactory.produce("heart").addResAmount(2), resourceFactory.produce("wood").addResAmount(5), resourceFactory.produce("stone").addResAmount(3)));
    mobFactory.addMobType("Dragonion", "mobs/dragon", 2, 35, 35, Arrays.asList(resourceFactory.produce("heart").addResAmount(40), resourceFactory.produce("wood").addResAmount(250), resourceFactory.produce("stone").addResAmount(120)));
    return mobFactory;
  }

  public MobsController produceMobsController(Board board, TurnSystem turnSystem, MobFactory mobFactory, FightSystem fightSystem) {
    return new MobsController(board, turnSystem, mobFactory, fightSystem);
  }

  public BuildingsController produceBuildingController(BoardFactory boardFactory, BuildingFactory buildingFactory, Board board, TurnSystem turnSystem) {
    return new BuildingsController(boardFactory, buildingFactory, board, turnSystem);
  }
}
