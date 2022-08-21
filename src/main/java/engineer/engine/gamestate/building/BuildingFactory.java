package engineer.engine.gamestate.building;

import engineer.engine.gamestate.resource.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingFactory {
  private record BuildingTraits(String texture, List<Resource> resToBuild) {}

  private final Map<String, BuildingTraits> descriptionMap = new HashMap<>();

  public void addMobType(String type, String texture, List<Resource> resToBuild) {
    descriptionMap.put(type, new BuildingTraits(texture, resToBuild));
  }
  private class BuildingImpl implements Building {
    private final String type;
    public BuildingImpl(String type) {
      this.type = type;
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
  }

  public Building produce(String type) {
    return new BuildingImpl(type);
  }
}
