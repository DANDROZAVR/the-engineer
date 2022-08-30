package engineer.engine.gamestate.building;

import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;

import java.util.List;

public interface Building {
  String getTexture();
  String getType();
  List<Resource> getResToBuild();
  Player getOwner();
  int getLifeRemaining();
  void reduceLifeRemaining(int amount);
  void upgrade();
  int getLevel();
}
