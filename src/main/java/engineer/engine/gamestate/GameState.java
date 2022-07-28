package engineer.engine.gamestate;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.field.Field;
import javafx.util.Pair;

public class GameState {
  private final BoardFactory boardFactory;
  private final Board board;
  private Pair<Integer, Integer> selectedField;

  @SuppressWarnings("unused")
  public void addBoardObserver(Board.Observer observer) {
    board.addObserver(observer);
  }

  @SuppressWarnings("unused")
  public void removeBoardObserver(Board.Observer observer) {
    board.removeObserver(observer);
  }

  public GameState(BoardFactory boardFactory) {
    this.boardFactory = boardFactory;

    board = boardFactory.produceBoard(40, 50);

    for (int i = 0; i < 40; i++)
      for (int j = 0; j < 50; j++) {
        Field field = boardFactory.produceField(
                "tile",
                boardFactory.produceBuilding(null),
                true
        );
        board.setField(i, j, field);
      }
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

  public Pair<Integer, Integer> getSelectedField() {
    return selectedField;
  }

  public void selectField(int x, int y) {
    selectedField = new Pair<>(x, y);
  }

  @SuppressWarnings("unused")
  public void unselectField() {
    selectedField = null;
  }

  public void build(int x, int y, String building) {
    Field field = getField(x, y);
    Field newField = boardFactory.produceField(
            field.getBackground(),
            boardFactory.produceBuilding(building),
            field.isFree()
    );

    board.setField(x, y, newField);
  }
}
