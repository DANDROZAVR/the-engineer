package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.turns.Player;
import engineer.utils.Coords;

import java.util.Collection;
import java.util.List;

public interface Board {
  interface Observer {
    default void onSelectionChanged(Coords coords) {}
    default void onFieldChanged(Coords coords) {}
    default void onMobRemoved(Mob mob) {}
    default void onMobAdded(Mob mob) {}
    default void onBuildingRemoved(Building building) {}
    default void onBuildingAdded(Building building) {}
  }

  void addObserver(Observer observer);
  void removeObserver(Observer observer);

  int getRows();
  int getColumns();

  Field produceField(String background, Building building, Mob mob, boolean free);
  Field getField(Coords coords);
  void setField(Coords coords, Field field);

  void selectField(Coords coords);
  Coords getSelectedCoords();

  Collection<Coords> getNearestFields(Coords coords, int range);

  Collection<Coords> getFieldsToAttack(Coords v, Player player);

  List<Coords> findPath(Coords start, Coords finish);

  void markFields(Collection<Coords> fieldsToMark, Collection<Coords> fieldsToFight);

  void unmarkAllFields();
  Collection<Coords> getMarkedFieldsToMove();
  Collection<Coords> getMarkedFieldsToAttack();
}
