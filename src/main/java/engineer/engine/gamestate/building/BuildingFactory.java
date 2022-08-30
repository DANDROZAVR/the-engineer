package engineer.engine.gamestate.building;

import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuildingFactory {
  private record BuildingTraits(String texture, List<Resource> resToBuild, int life) {}

  private final Map<String, BuildingTraits> descriptionMap = new HashMap<>();

  public void addBuildingType(String type, String texture, List<Resource> resToBuild, int life) {
    descriptionMap.put(type, new BuildingTraits(texture, resToBuild, life));
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

    public Player getOwner() {
      return player;
    }
  }

  public Building produce(String type, Player player) {
    return new BuildingImpl(type, player);
  }

}
