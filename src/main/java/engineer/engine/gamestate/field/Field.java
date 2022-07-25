package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;

public interface Field {
  boolean isFree();

  String getBackground();

  Building getBuilding();
}
