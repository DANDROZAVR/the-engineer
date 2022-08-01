package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Box;
import engineer.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BoardPresenterTest {
  @Mock private GameState gameState;
  @Mock private BoardPresenter.View view;
  @Mock private Field emptyField;
  @Mock private Field nonEmptyField;
  @Mock private Building building;
  private Pair selection;

  private static final double EPS = 0.001;

  private BoardPresenter presenter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(emptyField.getBackground()).thenReturn("Empty");
    when(nonEmptyField.getBackground()).thenReturn("Non-empty");
    when(nonEmptyField.getBuilding()).thenReturn(building);

    when(gameState.getBoardColumns()).thenReturn(8);
    when(gameState.getBoardRows()).thenReturn(10);

    when(gameState.getField(anyInt(), anyInt())).thenReturn(emptyField);
    when(gameState.getField(3, 4)).thenReturn(nonEmptyField);

    doAnswer(invocation ->
            selection = new Pair(invocation.getArgument(0), invocation.getArgument(1))
    ).when(gameState).selectField(anyInt(), anyInt());
    doAnswer(invocation ->
            selection = null
    ).when(gameState).unselectField();
    doAnswer(invocation ->
            selection
    ).when(gameState).getSelectedField();

    when(view.getViewWidth()).thenReturn(1 * 70.0);
    when(view.getViewHeight()).thenReturn(1 * 70.0);

    when(emptyField.isFree()).thenReturn(true);
    when(nonEmptyField.isFree()).thenReturn(true);

    presenter = new BoardPresenter(gameState, view);
  }

  @Test
  public void testCameraMove() {
    ArgumentCaptor<Box> captor = ArgumentCaptor.forClass(Box.class);
    List<Box> list;

    presenter.update(0.0);

    verify(gameState, atLeastOnce()).getField(0, 0);
    verify(gameState, never()).getField(3, 4);
    verify(emptyField, atLeastOnce()).getBackground();
    verify(nonEmptyField, never()).getBackground();
    verify(emptyField, atLeastOnce()).getBuilding();
    verify(nonEmptyField, never()).getBuilding();

    // Check what was drawn
    verify(view).drawField(captor.capture(), anyString());
    list = captor.getAllValues();
    assertFalse(list.stream().anyMatch(box ->
            (box.top() > 70.0 + EPS || box.bottom() < 0.0 - EPS) ||
            (box.left() > 70.0 + EPS || box.right() < 0.0 - EPS))
    );
    assertFalse(list.isEmpty());

    clearInvocations(gameState, emptyField, nonEmptyField, building);

    presenter.setCameraSpeedX(30.0);
    presenter.setCameraSpeedY(40.0);
    presenter.update(7.0);

    verify(gameState, never()).getField(0, 0);
    verify(gameState, atLeastOnce()).getField(3, 4);
    verify(emptyField, never()).getBuilding();
    verify(nonEmptyField, atLeastOnce()).getBuilding();
    verify(building, atLeastOnce()).getPicture();

    // Check what was drawn
    verify(view, atLeastOnce()).drawField(captor.capture(), anyString());
    list = captor.getAllValues();
    assertFalse(list.stream().anyMatch(box ->
            (box.top() > 70.0 + EPS || box.bottom() < 0.0 - EPS) ||
            (box.left() > 70.0 + EPS || box.right() < 0.0 - EPS))
    );
    assertFalse(list.isEmpty());
  }

  @Test
  public void testSelection() {
    presenter.selectField(EPS, EPS);
    presenter.update(0);
    assertEquals(new Pair(0, 0), selection);
    verify(view, atLeastOnce()).drawSelection(any(Box.class));

    clearInvocations(view);

    presenter.selectField(3*70.0 + EPS, 4*70.0 + EPS);
    presenter.update(0);
    assertEquals(new Pair(3, 4), selection);
    verify(view, never()).drawSelection(any(Box.class));

    clearInvocations(view);

    presenter.unselectField();
    presenter.update(0);
    assertNull(selection);
    verify(view, never()).drawSelection(any(Box.class));
  }
}
