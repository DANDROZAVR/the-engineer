//package engineer.engine.presenters;
//
//import engineer.engine.gamestate.GameState;
//import engineer.engine.gamestate.building.Building;
//import engineer.engine.gamestate.field.Field;
//import engineer.utils.Box;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
//class BoardPresenterTest {
//  @Mock private GameState gameState;
//  @Mock private BoardPresenter.View view;
//  @Mock private Field emptyField;
//  @Mock private Field nonEmptyField;
//  @Mock private Building building;
//
//  private static final double EPS = 0.001;
//
//  private BoardPresenter presenter;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//    when(emptyField.getBackground()).thenReturn("Empty");
//    when(nonEmptyField.getBackground()).thenReturn("Non-empty");
//    when(nonEmptyField.getBuilding()).thenReturn(building);
//
//    when(gameState.getBoardColumns()).thenReturn(10);
//    when(gameState.getBoardRows()).thenReturn(8);
//
//    when(gameState.getField(anyInt(), anyInt())).thenReturn(emptyField);
//    when(gameState.getField(3, 4)).thenReturn(nonEmptyField);
//
//    when(view.getViewWidth()).thenReturn(1 * 70.0);
//    when(view.getViewHeight()).thenReturn(1 * 70.0);
//
//    when(emptyField.isFree()).thenReturn(true);
//    when(nonEmptyField.isFree()).thenReturn(true);
//
//    presenter = new BoardPresenter(gameState, view);
//  }
//
//  @Test
//  public void testCameraMove() {
//    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
//    List<Box> list;
//
//    presenter.update(0.0);
//
//    verify(gameState, atLeastOnce()).getField(0, 0);
//    verify(gameState, never()).getField(3, 4);
//    verify(emptyField, atLeastOnce()).getBackground();
//    verify(nonEmptyField, never()).getBackground();
//    verify(emptyField, atLeastOnce()).getBuilding();
//    verify(nonEmptyField, never()).getBuilding();
//
//    // Check what was drawn
//    verify(view).drawField(captor.capture(), anyString());
//    list = captor.getAllValues();
//    assertFalse(
//        list.stream()
//            .anyMatch(
//                box ->
//                    (box.top() > 70.0 + EPS || box.bottom() < 0.0 - EPS)
//                        || (box.left() > 70.0 + EPS || box.right() < 0.0 - EPS)));
//    assertFalse(list.isEmpty());
//
//    clearInvocations(gameState, emptyField, nonEmptyField, building);
//
//    presenter.addCameraSpeedX(30.0);
//    presenter.setCameraSpeedX(0);
//    presenter.addCameraSpeedX(30.0);
//    presenter.addCameraSpeedY(40.0);
//    presenter.setCameraSpeedY(0);
//    presenter.addCameraSpeedY(40.0);
//    presenter.update(7.0);
//
//    verify(gameState, never()).getField(0, 0);
//    verify(gameState, atLeastOnce()).getField(3, 4);
//    verify(emptyField, never()).getBuilding();
//    verify(nonEmptyField, atLeastOnce()).getBuilding();
//    verify(building, atLeastOnce()).getPicture();
//
//    // Check what was drawn
//    verify(view, atLeastOnce()).drawField(captor.capture(), anyString());
//    list = captor.getAllValues();
//    assertFalse(
//        list.stream()
//            .anyMatch(
//                box ->
//                    (box.top() > 70.0 + EPS || box.bottom() < 0.0 - EPS)
//                        || (box.left() > 70.0 + EPS || box.right() < 0.0 - EPS)));
//    assertFalse(list.isEmpty());
//  }
//
//  @Test
//  public void testCameraZoomInAndOut() {
//    for (int i = 0; i < 100; i++) presenter.zoomOut();
//    presenter.update(0.0);
//
//    verify(gameState, atLeastOnce()).getField(3, 4);
//
//    clearInvocations(gameState);
//
//    for (int i = 0; i < 100; i++) presenter.zoomIn();
//    presenter.update(0.0);
//
//    verify(gameState, atMost(99)).getField(3, 4);
//  }
//
//  @Test
//  public void testButtons() {
//    presenter.addCameraSpeedX(3 * 70.0);
//    presenter.addCameraSpeedY(4 * 70.0);
//    presenter.update(1.0);
//
//    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//
//    presenter.setPressedButton("Button X");
//    presenter.changeContent(10.0, 10.0);
//
//    verify(gameState).build(eq(3), eq(4), captor.capture());
//    assertEquals("Button X", captor.getValue());
//
//    clearInvocations(gameState);
//    presenter.changeContent(20.0, 20.0);
//
//    verify(gameState).build(eq(3), eq(4), captor.capture());
//    assertNull(captor.getValue());
//  }
//
//  @Test
//  public void testSelection() {
//    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
//    presenter.selectField(0, 0);
//
//    presenter.update(0);
//    verify(view, atLeastOnce()).drawSelection(captor.capture());
//
//    presenter.unselectField();
//    clearInvocations(view);
//
//    presenter.update(0);
//    verify(view, never()).drawSelection(captor.capture());
//  }
//
//  @Test
//  public void testClicks() {
//    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
//    presenter.onMouseClick(BoardPresenter.SimpleMouseButton.PRIMARY, 1, 1, 3);
//    presenter.update(0);
//    verify(view, atLeastOnce()).drawSelection(captor.capture());
//  }
//
//  @Test
//  public void testCameraDraggingType1() {
//    presenter.onMouseMoved(view.getViewWidth(), view.getViewHeight());
//    presenter.update(10);
//    verify(gameState, never()).getField(0, 0);
//  }
//
//  @Test
//  public void testCameraDraggingType2() {
//    presenter.update(0);
//    verify(gameState, atLeastOnce()).getField(0, 0);
//    verify(gameState, never()).getField(0, 1);
//    presenter.onMousePressed(
//        BoardPresenter.SimpleMouseButton.SECONDARY, view.getViewWidth(), view.getViewHeight());
//    presenter.onMouseDragged(BoardPresenter.SimpleMouseButton.SECONDARY, 0, 0);
//    presenter.update(0);
//    verify(gameState, never()).getField(0, 1);
//    verify(gameState, atLeastOnce()).getField(1, 1);
//  }
//
//  @Test
//  public void testReleasedButton() {
//    presenter.onMouseReleased(BoardPresenter.SimpleMouseButton.PRIMARY, 0, 0);
//    // just to deal with coverage
//  }
//}
