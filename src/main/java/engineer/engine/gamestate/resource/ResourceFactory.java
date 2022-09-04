package engineer.engine.gamestate.resource;


import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ResourceFactory {
  private record ResTraits(String texture) {}

  private final Map<String, ResTraits> descriptionMap = new HashMap<>();

  public void addResType(String type, String texture) {
    descriptionMap.put(type, new ResTraits(texture));
  }

  private class ResImpl implements Resource {
    private final String type;
    private int resAmount = 0;

    public ResImpl(String type) {
      this.type = type;
      if (!descriptionMap.containsKey(type))
        throw new RuntimeException("Resource type isn't added [" + type + "]");
    }

    public ResImpl(JsonObject jsonResource) {
      this.type = jsonResource.get("type").getAsString();
      this.resAmount = jsonResource.get("res_amount").getAsInt();
    }

    @Override
    public String getType() {
      return type;
    }

    @Override
    public String getTexture() {
      return descriptionMap.get(type).texture();
    }

    @Override
    public int getResAmount() {
      return resAmount;
    }

    @Override
    public Resource addResAmount(int amount) {
      resAmount += amount;
      return this;
    }

    @Override
    public boolean equals(Resource resource) {
      return resource.getType().equals(getType());
    }
  }

  public Resource produce(String type) {
    return new ResImpl(type);
  }

  public Resource produce(JsonObject jsonResource) {
    return new ResImpl(jsonResource);
  }

}
