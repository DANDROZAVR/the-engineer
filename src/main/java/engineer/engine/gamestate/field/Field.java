package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.turns.Player;

public interface Field {
  String getBackground();
  Building getBuilding();
  Mob getMob();
  Player getOwner();
  boolean isFree();
}
