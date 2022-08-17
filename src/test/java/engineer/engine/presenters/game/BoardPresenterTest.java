package engineer.engine.presenters.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.utils.Box;
import engineer.utils.Coords;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.*;

class BoardPresenterTest {
  @Test
  public void testStartAndClose() {
    GameState gameState = mock(GameState.class);
    Camera camera = mock(Camera.class);
    Board board = mock(Board.class);
    when(gameState.getCamera()).thenReturn(camera);
    when(gameState.getBoard()).thenReturn(board);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.start();
    presenter.close();

    ArgumentCaptor<Board.Observer> boardCaptor = ArgumentCaptor.forClass(Board.Observer.class);
    ArgumentCaptor<Camera.Observer> cameraCaptor = ArgumentCaptor.forClass(Camera.Observer.class);

    verify(board).addObserver(boardCaptor.capture());
    verify(camera).addObserver(cameraCaptor.capture());
    verify(board).removeObserver(boardCaptor.getValue());
    verify(camera).removeObserver(cameraCaptor.getValue());
  }

  @Test
  public void testCameraInteractions() {
    GameState gameState = mock(GameState.class);
    Camera camera = mock(Camera.class);
    when(gameState.getCamera()).thenReturn(camera);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.moveCamera(6.0, 9.0);
    presenter.zoomCamera(8.7);

    verify(camera).moveCamera(6.0, 9.0);
    verify(camera).zoom(8.7);
  }

  @Test
  public void testSelection() {
    GameState gameState = mock(GameState.class);
    Camera camera = mock(Camera.class);
    Board board = mock(Board.class);
    when(gameState.getCamera()).thenReturn(camera);
    when(gameState.getBoard()).thenReturn(board);
    doReturn(new Coords(3, 5)).when(camera).getFieldByPoint(4.0, 15.0);
    BoardPresenter presenter = new BoardPresenter(gameState, mock(BoardPresenter.View.class));

    presenter.selectField(4.0, 15.0);
    presenter.unselectField();

    verify(board).selectField(new Coords(3, 5));
    verify(board).selectField(null);
  }

  @Test
  public void testRedrawVisibleFields() {
    GameState gameState = mock(GameState.class);

    Camera camera = mock(Camera.class);
    Board board = mock(Board.class);
    when(gameState.getCamera()).thenReturn(camera);
    when(gameState.getBoard()).thenReturn(board);

    BoardPresenter.View view = mock(BoardPresenter.View.class);
    BoardPresenter presenter = new BoardPresenter(gameState, view);

    Field visibleField = mock(Field.class), notVisibleField = mock(Field.class);
    Box visibleBox = new Box(3, 4, 15, 8);
    Box notVisibleBox = new Box(2, 8, 9, 1);
    Building building = mock(Building.class);
    Mob mob = mock(Mob.class);

    when(building.getPicture()).thenReturn("Building");
    when(mob.getTexture()).thenReturn("Mob");

    when(board.getRows()).thenReturn(5);
    when(board.getColumns()).thenReturn(8);

    when(camera.isFieldVisible(any())).thenReturn(false);

    when(camera.isFieldVisible(new Coords(3, 4))).thenReturn(true);
    when(board.getField(new Coords(3, 4))).thenReturn(visibleField);
    when(camera.getFieldBox(new Coords(3, 4))).thenReturn(visibleBox);
    when(visibleField.getBackground()).thenReturn("visibleFieldBackground");

    when(board.getField(new Coords(5, 1))).thenReturn(notVisibleField);
    when(camera.getFieldBox(new Coords(5, 1))).thenReturn(notVisibleBox);

    // Nothing is selected, no mob and no building

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verifyNoMoreInteractions(view);

    clearInvocations(view);

    // Visible field is selected, visible mob, not-visible accessible field and not-visible building

    when(board.getSelectedCoords()).thenReturn(new Coords(3, 4));
    when(visibleField.getMob()).thenReturn(mob);
    when(notVisibleField.getBuilding()).thenReturn(building);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(5, 1)));

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verify(view).drawField(visibleBox, "Mob");
    verify(view).drawSelection(visibleBox);
    verifyNoMoreInteractions(view);

    when(notVisibleField.getBuilding()).thenReturn(null);
    when(visibleField.getMob()).thenReturn(null);
    clearInvocations(view);

    // Not-visible field is selected, not-visible mob, visible accessible field and visible building

    when(board.getSelectedCoords()).thenReturn(new Coords(5, 1));
    when(visibleField.getBuilding()).thenReturn(building);
    when(notVisibleField.getMob()).thenReturn(mob);
    when(board.getMarkedFields()).thenReturn(List.of(new Coords(3, 4)));

    presenter.redrawVisibleFields();

    verify(view).drawField(visibleBox, "visibleFieldBackground");
    verify(view).drawField(visibleBox, "Building");
    verify(view).markField(visibleBox);
    verifyNoMoreInteractions(view);
  }

  @Test
  public void testBoardObserver() {
    GameState gameState = mock(GameState.class);
    Camera camera = mock(Camera.class);
    Board board = mock(Board.class);
    when(gameState.getCamera()).thenReturn(camera);
    when(gameState.getBoard()).thenReturn(board);
    BoardPresenter.View view = mock(BoardPresenter.View.class);
    ArgumentCaptor<Board.Observer> boardCaptor = ArgumentCaptor.forClass(Board.Observer.class);

    BoardPresenter presenter = new BoardPresenter(gameState, view);

    when(board.getRows()).thenReturn(3);
    when(board.getColumns()).thenReturn(3);
    Box box = new Box(0, 0, 0, 0);

    when(camera.isFieldVisible(any())).thenReturn(true);
    when(camera.getFieldBox(new Coords(1, 2))).thenReturn(box);
    when(board.getField(any())).thenReturn(mock(Field.class));

    presenter.start();
    verify(board).addObserver(boardCaptor.capture());

    Board.Observer observer = boardCaptor.getValue();
    observer.onFieldChanged(new Coords(1, 2));

    verify(view).drawField(eq(box), any());
  }
}
