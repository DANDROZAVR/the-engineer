package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.field.Field;
import engineer.utils.Coords;

import java.util.*;


public class MobsController {
  public interface GameStateCallback {
    void setMob(Coords coords, Mob mob);
  }
  private final List<Coords> accessibleFields = new LinkedList<>();
  private final MobFactory mobFactory;

  private final GameStateCallback callback;
  public MobsController(GameStateCallback callback, MobFactory mobFactory) {
    this.callback = callback;
    this.mobFactory = mobFactory;
  }
  private List<Coords> getNeighbours(Coords field){
    List<Coords> neighbours = new ArrayList<>();
    neighbours.add(new Coords(field.row()-1, field.column()));
    neighbours.add(new Coords(field.row()+1, field.column()));
    neighbours.add(new Coords(field.row(), field.column()+1));
    neighbours.add(new Coords(field.row(), field.column()-1));
    return neighbours;
  }

  private void setAccessibleFieldsFrom(Coords field, int range) {
    accessibleFields.clear();
    List<Coords> tempList = new LinkedList<>();

    accessibleFields.add(new Coords(field.row(), field.column()));
    for (int i = 0; i < range; i++) {
      for (Coords j : accessibleFields) {
        for (Coords k : getNeighbours(j)) {
          if (!accessibleFields.contains(k)) {
            tempList.add(k);
          }
        }
      }
      accessibleFields.addAll(tempList);
      tempList.clear();
    }
  }

  public void onFieldSelection(Coords selectedFieldXY, Coords lastSelectedFieldXY, Field selectedField, Field lastSelectedField) {
    if (accessibleFields.contains(selectedFieldXY)) {
      if (!selectedFieldXY.equals(lastSelectedFieldXY))
        moveMob(lastSelectedFieldXY, selectedFieldXY, lastSelectedField, selectedField, 1);
    }
    accessibleFields.clear();
    if (selectedField.getMob() != null) {
      setAccessibleFieldsFrom(selectedFieldXY, selectedField.getMob().getRemainingSteps());
    }
  }

  private void moveMob(Coords lastSelectedFieldXY, Coords selectedFieldXY, Field fieldFrom, Field fieldTo, int ignoredMobsFromAmounts) {
    if (fieldTo.getMob() != null && !Objects.equals(fieldTo.getMob().getType(), fieldFrom.getMob().getType())) return;
    int distSteps = Math.abs(selectedFieldXY.row() - lastSelectedFieldXY.row()) +
                    Math.abs(selectedFieldXY.column() - lastSelectedFieldXY.column());
    if (distSteps > fieldFrom.getMob().getRemainingSteps()) return;

    Mob fromMob = fieldFrom.getMob();
    Mob toMob = fieldTo.getMob();
    fromMob.reduceRemainingSteps(distSteps);

    if (toMob != null && Objects.equals(fromMob.getType(), toMob.getType())) {
      int newMobsAmount = fromMob.getMobsAmount() + toMob.getMobsAmount(); // use mobsFromAmount
      Mob mob = produceMob(fromMob.getType(), newMobsAmount);
      int deductionSteps = Math.max(mob.getRemainingSteps() - fromMob.getRemainingSteps(), mob.getRemainingSteps() - toMob.getRemainingSteps());
      mob.reduceRemainingSteps(deductionSteps);
      callback.setMob(selectedFieldXY, mob);
    } else {
      callback.setMob(selectedFieldXY, fromMob);
    }
    callback.setMob(lastSelectedFieldXY, null);
  }

  public Mob produceMob(String type, int mobsAmount) {
    return mobFactory.produce(type, mobsAmount);
  }

  public List<Coords> getAccessibleFields() {
    return accessibleFields;
  }

  public void onTurnStart() {
    accessibleFields.clear();
  }
}
