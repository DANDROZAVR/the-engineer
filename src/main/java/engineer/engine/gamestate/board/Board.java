package engineer.engine.gamestate.board;

import engineer.engine.gamestate.field.Field;

public interface Board {
  interface Observer {
    void onFieldChanged(int x, int y);
  }

  void addObserver(Observer observer);

  void removeObserver(Observer observer);

  int getRows();

  int getColumns();

  Field getField(int x, int y);

  void setField(int x, int y, Field field);
}
