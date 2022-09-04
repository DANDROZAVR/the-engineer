package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.turns.Player;

public class FieldFactory {
  private record FieldImpl(String background, Building building, Mob mob) implements Field {
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
    public Player getOwner() {
      if (getMob() != null)
        return getMob().getOwner();
      if (getBuilding() != null)
        return getBuilding().getOwner();
      return null;
    }
  }

  public Field produce(String background, Building building, Mob mob) {
    return new FieldImpl(background, building, mob);
  }
}
