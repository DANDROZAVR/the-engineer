package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.resource.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Player {
  private final List<Resource> resources = new LinkedList<>();
  private final String nickname;

  public Player(String nickname) {
    this.nickname = nickname;
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

  public boolean retrieveResourcesFromSchema(List<Resource> resourcesToBuild) {
    for (Resource buildRes : resourcesToBuild) {
      if (!enoughResource(buildRes))
        return false;
    }
    for (Resource buildRes : resourcesToBuild) {
      Resource playerRes = findPlayerResource(buildRes);
      playerRes.addResAmount(-buildRes.getResAmount()); // change to reduce?
    }
    return true;
  }

  private Resource findPlayerResource(Resource res) {
    Optional<Resource> foundedRes = resources.stream().filter(r -> r.equals(res)).findAny();
    return foundedRes.orElse(null);
  }

  private boolean enoughResource(Resource res) {
    Resource foundedRes = findPlayerResource(res);
    if (foundedRes == null) return false;
    return foundedRes.getResAmount() >= res.getResAmount();
  }
}
