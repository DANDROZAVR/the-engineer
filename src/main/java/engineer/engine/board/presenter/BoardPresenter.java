package engineer.engine.board.presenter;

import engineer.engine.board.logic.Board;

public class BoardPresenter {
    public interface Observer {
        void onFieldChange(int row, int column);
    }

    private final Board board;
    private final Board.Observer boardObserver;

    public BoardPresenter(Board board, Observer observer) {
        this.board = board;
        boardObserver = observer::onFieldChange;
    }

    // Need to be called at the beginning and at the end of lifetime
    public void start() { board.addObserver(boardObserver); }
    public void stop() { board.removeObserver(boardObserver); }
}
