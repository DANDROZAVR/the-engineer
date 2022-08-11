package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Box;
import engineer.utils.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

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
  public void testCameraInteractions() {
    GameState gameState = mock(GameState.class);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.moveCamera(6.0, 9.0);
    presenter.zoomCamera(8.7);

    verify(gameState).moveCamera(6.0, 9.0);
    verify(gameState).zoomCamera(8.7);
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
    Mob mob = mock(Mob.class);

    when(building.getPicture()).thenReturn("Building");
    when(mob.getTexture()).thenReturn("Mob");

    when(gameState.getRows()).thenReturn(5);
    when(gameState.getColumns()).thenReturn(8);

    when(gameState.isFieldVisible(anyInt(), anyInt())).thenReturn(false);

    when(gameState.isFieldVisible(3, 4)).thenReturn(true);
    when(gameState.getField(3, 4)).thenReturn(visibleField);
    when(gameState.getFieldBox(3, 4)).thenReturn(visibleBox);
    when(visibleField.getBackground()).thenReturn("visibleFieldBackground");

    when(gameState.getField(5, 1)).thenReturn(notVisibleField);
    when(gameState.getFieldBox(5, 1)).thenReturn(notVisibleBox);

    // Nothing is selected, no mob and no building

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verifyNoMoreInteractions(view);

    clearInvocations(view);

    // Visible field is selected, visible mob, not-visible accessible field and not-visible building

    when(gameState.getSelectedField()).thenReturn(new Pair(3, 4));
    when(visibleField.getMob()).thenReturn(mob);
    when(notVisibleField.getBuilding()).thenReturn(building);
    when(gameState.getAccessibleFields()).thenReturn(List.of(new Pair(5, 1)));

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verify(view).drawField(visibleBox, "Mob");
    verify(view).drawSelection(visibleBox);
    verifyNoMoreInteractions(view);

    when(notVisibleField.getBuilding()).thenReturn(null);
    when(visibleField.getMob()).thenReturn(null);
    clearInvocations(view);

    // Not-visible field is selected, not-visible mob, visible accessible field and visible building

    when(gameState.getSelectedField()).thenReturn(new Pair(5, 1));
    when(visibleField.getBuilding()).thenReturn(building);
    when(notVisibleField.getMob()).thenReturn(mob);
    when(gameState.getAccessibleFields()).thenReturn(List.of(new Pair(3, 4)));

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verify(view).drawField(visibleBox, "Building");
    verify(view).enlightenField(visibleBox);
    verifyNoMoreInteractions(view);
  }
}
