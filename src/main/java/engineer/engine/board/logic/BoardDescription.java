package engineer.engine.board.logic;

public interface BoardDescription {
    int getWidth();
    int getHeight();
    String getBackground(int row, int column);
}
