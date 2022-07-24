package engineer.engine.presenters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import engineer.engine.gamestate.Board;
import engineer.engine.gamestate.Field;
import engineer.engine.gamestate.FieldContent;
import engineer.utils.Box;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BoardPresenterTest {
  @Mock private Board board;
  @Mock private BoardPresenter.View view;
  @Mock private Field emptyField;
  @Mock private Field nonEmptyField;
  @Mock private FieldContent content;

  private static final double EPS = 0.001;

  private BoardPresenter presenter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    when(emptyField.getBackground()).thenReturn("Empty");
    when(nonEmptyField.getBackground()).thenReturn("Non-empty");
    when(nonEmptyField.getContent()).thenReturn(content);

    when(board.getColumns()).thenReturn(10);
    when(board.getRows()).thenReturn(8);

    when(board.getField(anyInt(), anyInt())).thenReturn(emptyField);
    when(board.getField(3, 4)).thenReturn(nonEmptyField);

    when(view.getViewWidth()).thenReturn(1 * 70.0);
    when(view.getViewHeight()).thenReturn(1 * 70.0);

    when(emptyField.isFree()).thenReturn(true);
    when(nonEmptyField.isFree()).thenReturn(true);

    presenter = new BoardPresenter(board, view);
  }

  @Test
  public void testCameraMove() {
    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
    List<Box> list;

    presenter.update(0.0);

    verify(board, atLeastOnce()).getField(0, 0);
    verify(board, never()).getField(3, 4);
    verify(emptyField, atLeastOnce()).getContent();
    verify(nonEmptyField, never()).getContent();

    // Check what was drawn
    verify(view).drawField(captor.capture(), anyString());
    list = captor.getAllValues();
    assertFalse(
        list.stream()
            .anyMatch(
                box ->
                    (box.top() > 70.0 + EPS || box.bottom() < 0.0 - EPS)
                        || (box.left() > 70.0 + EPS || box.right() < 0.0 - EPS)));
    assertFalse(list.isEmpty());

    clearInvocations(board, emptyField, nonEmptyField, content);

    presenter.addCameraSpeedX(30.0);
    presenter.setCameraSpeedX(0);
    presenter.addCameraSpeedX(30.0);
    presenter.addCameraSpeedY(40.0);
    presenter.setCameraSpeedY(0);
    presenter.addCameraSpeedY(40.0);
    presenter.update(7.0);

    verify(board, never()).getField(0, 0);
    verify(board, atLeastOnce()).getField(3, 4);
    verify(emptyField, never()).getContent();
    verify(nonEmptyField, atLeastOnce()).getContent();
    verify(content, atLeastOnce()).getPicture();

    // Check what was drawn
    verify(view, atLeastOnce()).drawField(captor.capture(), anyString());
    list = captor.getAllValues();
    assertFalse(
        list.stream()
            .anyMatch(
                box ->
                    (box.top() > 70.0 + EPS || box.bottom() < 0.0 - EPS)
                        || (box.left() > 70.0 + EPS || box.right() < 0.0 - EPS)));
    assertFalse(list.isEmpty());
  }

  @Test
  public void testCameraZoomInAndOut() {
    for (int i = 0; i < 100; i++) presenter.zoomOut();
    presenter.update(0.0);

    verify(board, atLeastOnce()).getField(3, 4);

    clearInvocations(board);

    for (int i = 0; i < 100; i++) presenter.zoomIn();
    presenter.update(0.0);

    verify(board, atMost(99)).getField(3, 4);
  }

  @Test
  public void testButtons() {
    presenter.addCameraSpeedX(3 * 70.0);
    presenter.addCameraSpeedY(4 * 70.0);
    presenter.update(1.0);

    ArgumentCaptor<FieldContent> captor = ArgumentCaptor.forClass(FieldContent.class);

    presenter.setPressedButton("Button X");
    presenter.changeContent(10.0, 10.0);

    verify(board).setFieldContent(eq(3), eq(4), captor.capture());
    assertEquals("Button X", captor.getValue().getPicture());

    clearInvocations(board);
    presenter.changeContent(20.0, 20.0);

    verify(board).setFieldContent(eq(3), eq(4), captor.capture());
    assertNull(captor.getValue().getPicture());
  }

  @Test
  public void testSelection() {
    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
    presenter.setSelectedField(0, 0);

    presenter.update(0);
    verify(view, atLeastOnce()).drawSelection(captor.capture());

    presenter.cancelSelectedField();
    clearInvocations(view);

    presenter.update(0);
    verify(view, never()).drawSelection(captor.capture());
  }

  @Test
  public void testClicks() {
    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
    presenter.onMouseClick(BoardPresenter.SimpleMouseButton.PRIMARY, 1, 1, 3);
    presenter.update(0);
    verify(view, atLeastOnce()).drawSelection(captor.capture());
  }

  @Test
  public void testCameraDraggingType1() {
    presenter.onMouseMoved(view.getViewWidth(), view.getViewHeight());
    presenter.update(10);
    verify(board, never()).getField(0, 0);
  }

  @Test
  public void testCameraDraggingType2() {
    presenter.update(0);
    verify(board, atLeastOnce()).getField(0, 0);
    verify(board, never()).getField(0, 1);
    presenter.onMousePressed(
        BoardPresenter.SimpleMouseButton.SECONDARY, view.getViewWidth(), view.getViewHeight());
    presenter.onMouseDragged(BoardPresenter.SimpleMouseButton.SECONDARY, 0, 0);
    presenter.update(0);
    verify(board, never()).getField(0, 1);
    verify(board, atLeastOnce()).getField(1, 1);
  }

  @Test
  public void testReleasedButton() {
    presenter.onMouseReleased(BoardPresenter.SimpleMouseButton.PRIMARY, 0, 0);
    // just to deal with coverage
  }
}
