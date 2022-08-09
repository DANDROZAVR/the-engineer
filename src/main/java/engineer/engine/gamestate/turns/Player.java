package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.Mob;

import java.util.LinkedList;
import java.util.List;

public class Player {
  private final List<Mob> mobs = new LinkedList<>();

  public void onTurnStart() {
    // TODO: Reset mobs
  }

  public void addMob(Mob mob) {
    mobs.add(mob);
  }

  public void removeMob(Mob mob) {
    mobs.remove(mob);
  }
}
