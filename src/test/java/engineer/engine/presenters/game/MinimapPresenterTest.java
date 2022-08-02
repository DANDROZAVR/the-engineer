package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.field.Field;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MinimapPresenterTest {
  @Test
  public void testInitialization() {
    GameState gameState = mock(GameState.class);
    doReturn(5).when(gameState).getColumns();
    doReturn(3).when(gameState).getRows();
    doAnswer(invocation -> {
            Field field = mock(Field.class);
            doReturn("Field["+invocation.getArgument(0)+","+invocation.getArgument(1)+"]")
                    .when(field).getBackground();
            return field;
    }).when(gameState).getField(anyInt(), anyInt());

    MinimapPresenter.View view = mock(MinimapPresenter.View.class);

    new MinimapPresenter(gameState, view);
    for (int column=0;column<5;column++) {
      for (int row=0;row<3;row++) {
        verify(view).drawOnBackground(row, column, "Field["+row+","+column+"]");
      }
    }
  }
}
