package engineer.engine.gamestate.turns;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.resource.ResourceFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Player {
  private final List<Resource> resources = new LinkedList<>();
  private final String nickname;

  public Player(String nickname) {
    this.nickname = nickname;
  }
  public Player(JsonObject jsonPlayer, ResourceFactory resourceFactory) {
    if (jsonPlayer == null)
      throw new RuntimeException("Passing nullable JsonObject for player's constructor");
    this.nickname = jsonPlayer.get("nickname").getAsString();
    for (JsonElement jsonResource : jsonPlayer.getAsJsonArray("resources")) {
      Resource resource = resourceFactory.produce(jsonResource.getAsJsonObject());
      resources.add(resource);
    }
  }

  public String getNickname() {
    return nickname;
  }

  public List<Resource> getResources() {
    return resources;
  }

  public void addResource(Resource resource) {
    Resource playerRes = findPlayerResource(resource);
    if (playerRes == null) {
      resources.add(resource);
    } else {
      playerRes.addResAmount(resource.getResAmount());
    }
  }

  public boolean retrieveResourcesFromSchema(List<Resource> resourcesToBuild, int numberOfCopies) {
    for (Resource buildRes : resourcesToBuild) {
      if (!enoughResource(buildRes, numberOfCopies))
        return false;
    }
    for (Resource buildRes : resourcesToBuild) {
      Resource playerRes = findPlayerResource(buildRes);
      playerRes.addResAmount(-buildRes.getResAmount() * numberOfCopies); // change to reduce?
    }
    return true;
  }

  private Resource findPlayerResource(Resource res) {
    Optional<Resource> foundedRes = resources.stream().filter(r -> r.equals(res)).findAny();
    return foundedRes.orElse(null);
  }

  private boolean enoughResource(Resource res, int numberOfCopies) {
    Resource foundedRes = findPlayerResource(res);
    if (foundedRes == null) return false;
    return foundedRes.getResAmount() >= res.getResAmount() * numberOfCopies;
  }
}
