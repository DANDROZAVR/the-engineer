package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Box;
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

  @Test
  public void testRedrawVisibleFields() {
    GameState gameState = mock(GameState.class);
    BoardPresenter.View view = mock(BoardPresenter.View.class);
    BoardPresenter presenter = new BoardPresenter(gameState, view);

    Field visibleField = mock(Field.class), notVisibleField = mock(Field.class);
    Box visibleBox = new Box(3, 4, 15, 8);
    Box notVisibleBox = new Box(2, 8, 9, 1);
    Building building = mock(Building.class);

    when(building.getPicture()).thenReturn("Building");

    when(gameState.getRows()).thenReturn(5);
    when(gameState.getColumns()).thenReturn(8);

    when(gameState.isFieldVisible(anyInt(), anyInt())).thenReturn(false);

    when(gameState.isFieldVisible(3, 4)).thenReturn(true);
    when(gameState.getField(3, 4)).thenReturn(visibleField);
    when(gameState.getFieldBox(3, 4)).thenReturn(visibleBox);
    when(visibleField.getBackground()).thenReturn("visibleFieldBackground");

    when(gameState.getField(5, 1)).thenReturn(notVisibleField);
    when(gameState.getFieldBox(5, 1)).thenReturn(notVisibleBox);

    // Nothing is selected and there is no building

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verifyNoMoreInteractions(view);

    clearInvocations(view);

    // Visible field is selected and there is not-visible building

    when(gameState.getSelectedField()).thenReturn(new Pair(3, 4));
    when(notVisibleField.getBuilding()).thenReturn(building);

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verify(view).drawSelection(visibleBox);
    verifyNoMoreInteractions(view);

    when(notVisibleField.getBuilding()).thenReturn(null);
    clearInvocations(view);

    // Not-visible field is selected and there is visible building

    when(gameState.getSelectedField()).thenReturn(new Pair(5, 1));
    when(visibleField.getBuilding()).thenReturn(building);

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verify(view).drawField(visibleBox, "Building");
    verifyNoMoreInteractions(view);
  }
}
