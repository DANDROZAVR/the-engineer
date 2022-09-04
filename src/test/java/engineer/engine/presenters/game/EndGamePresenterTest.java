package engineer.engine.presenters.game;


import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
class EndGamePresenterTest {
  private AutoCloseable closeable;

  @Mock Board board;
  @Mock TurnSystem turnSystem;
  @Mock Player player;
  @Mock EndGamePresenter.View callbackView;


  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testObserver() {
    EndGamePresenter presenter = new EndGamePresenter(board, turnSystem, callbackView);
    ArgumentCaptor<Board.Observer> observerCaptor = ArgumentCaptor.forClass(Board.Observer.class);

    verify(board, never()).addObserver(observerCaptor.capture());
    presenter.start();
    verify(board).addObserver(observerCaptor.capture());

    Board.Observer observer = observerCaptor.getValue();
    when(turnSystem.getCurrentPlayer()).thenReturn(player);
    observer.onGameEnded();

    verify(callbackView).showEndGame(turnSystem.getCurrentPlayer().getNickname());
    presenter.close();

    verify(board).removeObserver(observer);
  }
}
