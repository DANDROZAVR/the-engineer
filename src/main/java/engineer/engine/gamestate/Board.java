package engineer.engine.gamestate;

import engineer.engine.exceptions.IndexOutOfBoardException;
import engineer.engine.exceptions.InvalidBoardDescriptionException;
import java.util.ArrayList;
import java.util.List;

public class Board {
  /* Observer part start */

  public interface Observer {
    void onFieldChanged(int row, int column);
  }

  private final List<Observer> observerList = new ArrayList<>();

  public void addObserver(Observer observer) {
    observerList.add(observer);
  }

  public void removeObserver(Observer observer) {
    observerList.remove(observer);
  }

  private void onFieldChanged(int row, int column) {
    observerList.forEach(o -> o.onFieldChanged(row, column));
  }

  /* Observer part end */

  private final int rows;
  private final int columns;
  private final Field[][] board;

  public Board(FieldFactory factory, BoardDescription description)
      throws InvalidBoardDescriptionException {
    rows = description.getRows();
    columns = description.getColumns();

    if (getRows() <= 0 || getColumns() <= 0) throw new InvalidBoardDescriptionException();

    board = new Field[getRows()][getColumns()];
    for (int row = 0; row < getRows(); row++)
      for (int column = 0; column < getColumns(); column++)
        board[row][column] = factory.produce(description.getBackground(row, column), true);
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public Field getField(int row, int column) throws IndexOutOfBoardException {
    try {
      return board[row][column];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IndexOutOfBoardException(e);
    }
  }

  public void setFieldContent(int row, int column, FieldContent content)
      throws IndexOutOfBoardException {
    getField(row, column).setContent(content);
    onFieldChanged(row, column);
  }
}
