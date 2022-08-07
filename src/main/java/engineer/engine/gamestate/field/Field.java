package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.mob.Mob;

public interface Field {
  boolean isFree();

  String getBackground();

  Building getBuilding();
  Mob getMob();
}
