package engineer.engine.gamestate.board;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Coords;

import java.util.Collection;
import java.util.List;

public interface Board {
  interface Observer {
    default void onSelectionChanged(Coords coords) {}
    default void onFieldChanged(Coords coords) {}
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
  List<Coords> findPath(Coords start, Coords finish);

  void markFields(Collection<Coords> collection);
  void unmarkAllFields();
  Collection<Coords> getMarkedFields();
}
