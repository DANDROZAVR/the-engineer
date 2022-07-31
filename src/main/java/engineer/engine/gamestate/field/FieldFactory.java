package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;

public class FieldFactory {
  private record FieldImpl(String background, Building building, boolean free) implements Field {
    @Override
    public String getBackground() {
      return background;
    }

    @Override
    public Building getBuilding() {
      return building;
    }

    @Override
    public boolean isFree() {
      return free;
    }
  }

  public Field produce(String background, Building building, boolean free) {
    return new FieldImpl(background, building, free);
  }
}
