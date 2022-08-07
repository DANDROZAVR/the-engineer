package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.mob.Mob;

public class FieldFactory {
  private record FieldImpl(String background, Building building, Mob mob, boolean free) implements Field {
    @Override
    public String getBackground() {
      return background;
    }

    @Override
    public Building getBuilding() {
      return building;
    }

    @Override
    public Mob getMob() {
      return mob;
    }

    @Override
    public boolean isFree() {
      return free;
    }
  }

  public Field produce(String background, Building building, Mob mob, boolean free) {
    return new FieldImpl(background, building, mob, free);
  }
}
