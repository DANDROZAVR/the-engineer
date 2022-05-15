package engineer.engine.board.presenter;

import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.BoardTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class BoardPresenterTest {
    @Test
    public void testObserver() {
        try {
            Board board = new Board(new BoardTest.SampleFieldFactory(), new BoardTest.SampleDescription(5, 8));
            BoardPresenter.Observer view = mock(BoardPresenter.Observer.class);
            BoardPresenter presenter = new BoardPresenter(board);

            presenter.startObserve();
            board.setFieldContent(0, 0, null);
            presenter.addObservers(view);
            board.setFieldContent(2, 7, null);
            presenter.stopObserve();

            board.setFieldContent(3, 4, null);
            presenter.startObserve();
            presenter.removeObservers(view);
            board.setFieldContent(1, 3, null);

            verify(view).onFieldChange(2, 7);
            verifyNoMoreInteractions(view);
        } catch (Exception e) {
            fail(e);
        }
    }
}