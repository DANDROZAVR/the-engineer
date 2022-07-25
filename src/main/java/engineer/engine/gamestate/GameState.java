package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.field.Field;

public class GameState {
  private final BoardFactory boardFactory;

  private final Board board;

  public void addBoardObserver(Board.Observer observer) {
    board.addObserver(observer);
  }

  public void removeBoardObserver(Board.Observer observer) {
    board.removeObserver(observer);
  }

  public GameState(BoardFactory boardFactory) {
    this.boardFactory = boardFactory;

    board = boardFactory.produceBoard(40, 50);

    for (int i = 0; i < 40; i++)
      for (int j = 0; j < 50; j++)
        board.setField(
            i, j, boardFactory.produceField("tile", boardFactory.produceBuilding(null), true));
  }

  public int getBoardRows() {
    return board.getRows();
  }

  public int getBoardColumns() {
    return board.getColumns();
  }

  public Field getField(int x, int y) {
    return board.getField(x, y);
  }

  public void build(int x, int y, String building) {
    Field field = getField(x, y);
    board.setField(
        x,
        y,
        boardFactory.produceField(
            field.getBackground(), boardFactory.produceBuilding(building), field.isFree()));
  }
}
