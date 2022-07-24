package engineer.engine.board.logic;

public interface BoardDescription {
  int getRows();

  int getColumns();

  String getBackground(int row, int column);
}
