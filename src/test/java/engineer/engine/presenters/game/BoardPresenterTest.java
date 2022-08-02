package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.utils.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

class BoardPresenterTest {
  @Test
  public void testStartAndClose() {
    GameState gameState = mock(GameState.class);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.start();
    presenter.close();

    ArgumentCaptor<Board.Observer> boardCaptor = ArgumentCaptor.forClass(Board.Observer.class);
    ArgumentCaptor<Camera.Observer> cameraCaptor = ArgumentCaptor.forClass(Camera.Observer.class);

    verify(gameState).addBoardObserver(boardCaptor.capture());
    verify(gameState).addCameraObserver(cameraCaptor.capture());
    verify(gameState).removeBoardObserver(boardCaptor.getValue());
    verify(gameState).removeCameraObserver(cameraCaptor.getValue());
  }

  @Test
  public void testCameraMove() {
    GameState gameState = mock(GameState.class);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.moveCamera(6.0, 9.0);

    verify(gameState).moveCamera(6.0, 9.0);
  }

  @Test
  public void testSelection() {
    GameState gameState = mock(GameState.class);
    doReturn(new Pair(3, 5)).when(gameState).getFieldByPoint(4.0, 15.0);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.selectField(4.0, 15.0);
    presenter.unselectField();

    verify(gameState).selectField(3, 5);
    verify(gameState).unselectField();
  }
}
