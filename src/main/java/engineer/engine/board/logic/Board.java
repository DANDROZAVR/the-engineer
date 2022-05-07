    package engineer.engine.board.logic;

import engineer.engine.board.exceptions.IndexOutOfBoardException;
import engineer.engine.board.exceptions.InvalidBoardDescriptionException;

import java.util.ArrayList;
import java.util.List;

public class Board {
    /* Observer part start */

    public interface Observer {
        void onFieldChanged(int row, int column);
    }

    private final List<Observer> observerList = new ArrayList<>();

    public void addObserver(Observer observer) { observerList.add(observer); }
    public void removeObserver(Observer observer) { observerList.remove(observer); }

    private void onFieldChanged(int row, int column) {
        for (Observer o : observerList)
            o.onFieldChanged(row, column);
    }

    /* Observer part end */

    private final int width;
    private final int height;
    private final Field[][] board;

    public Board(FieldFactory factory, BoardDescription description) throws InvalidBoardDescriptionException {
        width = description.getWidth();
        height = description.getHeight();

        if(getWidth() <= 0 || getHeight() <= 0)
            throw new InvalidBoardDescriptionException();

        board = new Field[getWidth()][getHeight()];
        for (int row=0;row<getWidth();row++)
            for(int column=0;column<getHeight();column++)
                board[row][column] = factory.produce(description.getBackground(row, column));
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    Field getField(int row, int column) throws IndexOutOfBoardException {
        try {
            return board[row][column];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IndexOutOfBoardException();
        }
    }

    public void setFieldContent(int row, int column, FieldContent content) throws IndexOutOfBoardException {
        getField(row, column).setContent(content);
        onFieldChanged(row, column);
    }
}
