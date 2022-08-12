package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
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
    doReturn(5).when(gameState).getColumns();
    doReturn(3).when(gameState).getRows();
    doAnswer(invocation -> {
            Field field = mock(Field.class);
            Coords coords = invocation.getArgument(0);
            doReturn("Field["+coords.row()+","+coords.column()+"]")
                    .when(field).getBackground();
            return field;
    }).when(gameState).getField(any());

    MinimapPresenter.View view = mock(MinimapPresenter.View.class);

    new MinimapPresenter(gameState, view).start();
    for (int column=0;column<5;column++) {
      for (int row=0;row<3;row++) {
        verify(view).drawOnBackground(new Coords(row, column), "Field["+row+","+column+"]");
      }
    }
  }

  @Test
  public void testObserver() {
    GameState gameState = mock(GameState.class);
    Box cameraBox = new Box(3, 4, 5, 6);

    when(gameState.getCameraBox()).thenReturn(cameraBox);

    MinimapPresenter.View view = mock(MinimapPresenter.View.class);
    MinimapPresenter presenter = new MinimapPresenter(gameState, view);
    ArgumentCaptor<Camera.Observer> observerCaptor = ArgumentCaptor.forClass(Camera.Observer.class);

    presenter.start();
    verify(gameState).addCameraObserver(observerCaptor.capture());

    Camera.Observer observer = observerCaptor.getValue();
    observer.onCameraUpdate();
    verify(view).drawCameraBox(cameraBox);

    presenter.close();
    verify(gameState).removeCameraObserver(observer);
  }
}
