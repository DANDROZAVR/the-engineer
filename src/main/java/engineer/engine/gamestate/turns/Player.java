package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.Mob;

import java.util.LinkedList;
import java.util.List;

public class Player {
  private final List<Mob> mobs = new LinkedList<>();

  public void onTurnStart() {
    for (Mob mob : mobs)
      mob.reset();
  }

  public void addMob(Mob mob) {
    mobs.add(mob);
  }

  public void removeMob(Mob mob) {
    mobs.remove(mob);
  }

  public boolean isMobOwner(Mob mob) {
    return mobs.contains(mob);
  }
}
