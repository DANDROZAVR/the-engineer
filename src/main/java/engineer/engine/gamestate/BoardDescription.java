package engineer.engine.gamestate;

public interface BoardDescription {
  int getRows();

  int getColumns();

  String getBackground(int row, int column);
}
