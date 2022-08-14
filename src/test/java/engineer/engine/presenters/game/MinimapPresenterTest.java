package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Box;
import engineer.utils.Coords;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

class MinimapPresenterTest {
  @Test
  public void testInitialization() {
    GameState gameState = mock(GameState.class);
    Camera camera = mock(Camera.class);
    Board board = mock(Board.class);
    when(gameState.getCamera()).thenReturn(camera);
    when(gameState.getBoard()).thenReturn(board);
    doReturn(5).when(board).getColumns();
    doReturn(3).when(board).getRows();
    doAnswer(invocation -> {
            Field field = mock(Field.class);
            Coords coords = invocation.getArgument(0);
            doReturn("Field["+coords.row()+","+coords.column()+"]")
                    .when(field).getBackground();
            return field;
    }).when(board).getField(any());

    MinimapPresenter.View view = mock(MinimapPresenter.View.class);

    new MinimapPresenter(gameState, view).start();
    for (int column=0;column<5;column++) {
      for (int row=0;row<3;row++) {
        verify(view).drawOnBackground(new Coords(row, column), "Field["+row+","+column+"]");
      }
    }

    verify(camera).addObserver(any());
  }

  @Test
  public void testObserver() {
    GameState gameState = mock(GameState.class);
    Camera camera = mock(Camera.class);
    Board board = mock(Board.class);
    when(gameState.getCamera()).thenReturn(camera);
    when(gameState.getBoard()).thenReturn(board);
    Box cameraBox = new Box(3, 4, 5, 6);

    when(camera.getCameraBox()).thenReturn(cameraBox);

    MinimapPresenter.View view = mock(MinimapPresenter.View.class);
    MinimapPresenter presenter = new MinimapPresenter(gameState, view);
    ArgumentCaptor<Camera.Observer> observerCaptor = ArgumentCaptor.forClass(Camera.Observer.class);

    presenter.start();
    verify(camera).addObserver(observerCaptor.capture());

    Camera.Observer observer = observerCaptor.getValue();
    observer.onCameraUpdate();
    verify(view).drawCameraBox(cameraBox);

    presenter.close();
    verify(camera).removeObserver(observer);
  }
}
