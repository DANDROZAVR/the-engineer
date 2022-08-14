package engineer.engine.gamestate.board;

import engineer.engine.gamestate.field.Field;
import engineer.utils.Coords;

public interface Board {
  interface Observer {
    default void onSelectionChanged(Coords coords) {}
    default void onFieldChanged(Coords coords) {}
  }

  void addObserver(Observer observer);
  void removeObserver(Observer observer);

  int getRows();
  int getColumns();

  Field getField(Coords coords);
  void setField(Coords coords, Field field);

  void selectField(Coords coords);
  Coords getSelectedCoords();
}
