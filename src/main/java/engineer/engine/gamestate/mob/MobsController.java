package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.field.Field;
import engineer.utils.Pair;

import java.util.*;


public class MobsController {
  public interface GameStateCallback {
    void setMob(int row, int column, Mob mob);
  }
  private final List<Pair> accessibleFields = new LinkedList<>();
  private final MobFactory mobFactory;

  private final GameStateCallback callback;
  public MobsController(GameStateCallback callback, MobFactory mobFactory) {
    this.callback = callback;
    this.mobFactory = mobFactory;
  }
  private List<Pair> getNeighbours(Pair field){
    List<Pair> neighbours = new ArrayList<>();
    neighbours.add(new Pair(field.first()-1, field.second()));
    neighbours.add(new Pair(field.first()+1, field.second()));
    neighbours.add(new Pair(field.first(), field.second()+1));
    neighbours.add(new Pair(field.first(), field.second()-1));
    return neighbours;
  }

  private void setAccessibleFieldsFrom(Pair field, int range) {
    accessibleFields.clear();
    List<Pair> tempList = new LinkedList<>();

    accessibleFields.add(new Pair(field.first(), field.second()));
    for (int i = 0; i < range; i++) {
      for (Pair j : accessibleFields) {
        for (Pair k : getNeighbours(j)) {
          if (!accessibleFields.contains(k)) {
            tempList.add(k);
          }
        }
      }
      accessibleFields.addAll(tempList);
      tempList.clear();
    }
  }

  public void onFieldSelection(Pair selectedFieldXY, Pair lastSelectedFieldXY, Field selectedField, Field lastSelectedField) {
    if (accessibleFields.contains(selectedFieldXY)) {
      if (!selectedFieldXY.equals(lastSelectedFieldXY))
        moveMob(lastSelectedFieldXY, selectedFieldXY, lastSelectedField, selectedField, 1);
    }
    accessibleFields.clear();
    if (selectedField.getMob() != null) {
      setAccessibleFieldsFrom(selectedFieldXY, selectedField.getMob().getRemainingSteps());
    }
  }

  private void moveMob(Pair lastSelectedFieldXY, Pair selectedFieldXY, Field fieldFrom, Field fieldTo, int ignoredMobsFromAmounts) {
    if (fieldTo.getMob() != null && !Objects.equals(fieldTo.getMob().getType(), fieldFrom.getMob().getType())) return;
    int distSteps = Math.abs(selectedFieldXY.first() - lastSelectedFieldXY.first()) +
                    Math.abs(selectedFieldXY.second() - lastSelectedFieldXY.second());
    if (distSteps > fieldFrom.getMob().getRemainingSteps()) return;

    Mob fromMob = fieldFrom.getMob();
    Mob toMob = fieldTo.getMob();
    fromMob.reduceRemainingSteps(distSteps);

    if (toMob != null && Objects.equals(fromMob.getType(), toMob.getType())) {
      int newMobsAmount = fromMob.getMobsAmount() + toMob.getMobsAmount(); // use mobsFromAmount
      Mob mob = produceMob(fromMob.getType(), newMobsAmount);
      int deductionSteps = Math.max(mob.getRemainingSteps() - fromMob.getRemainingSteps(), mob.getRemainingSteps() - toMob.getRemainingSteps());
      mob.reduceRemainingSteps(deductionSteps);
      callback.setMob(selectedFieldXY.first(), selectedFieldXY.second(), mob);
    } else {
      callback.setMob(selectedFieldXY.first(), selectedFieldXY.second(), fromMob);
    }
    callback.setMob(lastSelectedFieldXY.first(), lastSelectedFieldXY.second(), null);
  }

  public Mob produceMob(String type, int mobsAmount) {
    return mobFactory.produce(type, mobsAmount);
  }

  public List<Pair> getAccessibleFields() {
    return accessibleFields;
  }

  public void onTurnStart() {
    accessibleFields.clear();
  }
}
