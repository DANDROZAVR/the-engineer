package engineer.engine.gamestate.building;

import engineer.engine.gamestate.mob.Mob;
import com.google.gson.JsonObject;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildingFactory {
  private record BuildingTraits(String texture, List<Resource> resToBuild, List<Resource> resProduced, List<Resource> resToUpgrade, int life, Mob typeOfProducedMob) {}

  private final Map<String, BuildingTraits> descriptionMap = new HashMap<>();

  public void addBuildingType(String type, String texture, List<Resource> resToBuild, List<Resource> resProduced, List<Resource> resToUpgrade, int life, Mob typeOfProducedMob) {
    descriptionMap.put(type, new BuildingTraits(texture, resToBuild, resProduced, resToUpgrade, life, typeOfProducedMob));
  }

  private class BuildingImpl implements Building {
    private final String type;
    private int lifeRemaining;
    private int level;
    private final Player player;
    public BuildingImpl(String type, Player player) {
      this.type = type;
      this.lifeRemaining = descriptionMap.get(type).life;
      this.player = player;
      this.level = 1;
    }

    public BuildingImpl(JsonObject jsonBuilding, List<Player> players) {
      if (jsonBuilding == null)
        throw new RuntimeException("Passing nullable JsonObject for resource's constructor");
      this.type = jsonBuilding.get("type").getAsString();
      this.lifeRemaining = jsonBuilding.get("life_remaining").getAsInt();
      this.level = jsonBuilding.get("level").getAsInt();
      this.player = players.stream().filter(x -> x.getNickname().equals(jsonBuilding.get("owner").getAsString())).findAny().orElse(null);
    }

    @Override
    public String getTexture() {
      return descriptionMap.get(type).texture;
    }

    @Override
    public String getType() {
      return type;
    }

    @Override
    public List<Resource> getResToBuild() {
      return descriptionMap.get(type).resToBuild;
    }

    @Override
    public List<Resource> getResToUpgrade() {
      return descriptionMap.get(type).resToUpgrade;
    }

    public int getLifeRemaining() {
      return lifeRemaining;
    }

    @Override
    public void reduceLifeRemaining(int amount) {
      lifeRemaining = Math.max(lifeRemaining - amount, 0);
    }

    @Override
    public void upgrade() {
      level += 1;
    }

    @Override
    public int getLevel() {
      return level;
    }

    @Override
    public void produceOnEndOfTurn() {
      for (Resource res : descriptionMap.get(type).resProduced) {
        player.addResource(res);
      }
    }

    @Override
    public Mob getTypeOfProducedMob() {
      return descriptionMap.get(type).typeOfProducedMob;
    }

    public Player getOwner() {
      return player;
    }
  }

  public Building produce(String type, Player player) {
    return new BuildingImpl(type, player);
  }

  public Building produce(JsonObject jsonBuilding, List<Player> players) {
    if (jsonBuilding.size() == 0)
      return null;
    return new BuildingImpl(jsonBuilding, players);
  }
}
