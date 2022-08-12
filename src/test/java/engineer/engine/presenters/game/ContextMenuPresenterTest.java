package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Coords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class ContextMenuPresenterTest {
  Field emptyField = mock(Field.class);
  Field nonEmptyField = mock(Field.class);
  Building building = mock(Building.class);
  GameState gameState;
  ContextMenuPresenter.View callbackView;
  List<Building> buildingsList = Arrays.asList(null, building);
  @BeforeEach
  public void setup() {
    gameState = mock(GameState.class);
    callbackView = mock(ContextMenuPresenter.View.class);
    doReturn(buildingsList).when(gameState).getAllBuildingsList();
    doReturn(null).when(emptyField).getBuilding();
    doReturn(building).when(nonEmptyField).getBuilding();
    doReturn(emptyField).when(gameState).getField(new Coords(1, 0));
    doReturn(nonEmptyField).when(gameState).getField(new Coords(0, 1));
    doReturn("smth").when(building).getPicture();
  }
  @Test
  public void testObserver() {
    ContextMenuPresenter presenter = new ContextMenuPresenter(gameState, callbackView);
    ArgumentCaptor<GameState.SelectionObserver> observerCaptor = ArgumentCaptor.forClass(GameState.SelectionObserver.class);

    verify(gameState, never()).addSelectionObserver(observerCaptor.capture());
    presenter.start();
    verify(gameState).addSelectionObserver(observerCaptor.capture());

    GameState.SelectionObserver observer = observerCaptor.getValue();

    observer.onFieldSelection(new Coords(0, 1));
    verify(callbackView, atLeastOnce()).showBuildingInfoWindow("smth", "smth");
    verify(callbackView, never()).showBuildingsListWindow(any());

    observer.onFieldSelection(new Coords(1, 0));
    verify(callbackView, atLeastOnce()).showBuildingsListWindow(buildingsList);
    presenter.close();
    verify(gameState).removeSelectionObserver(observer);
    verifyNoMoreInteractions(callbackView);
  }
  @Test
  public void onGeneralInfoTest() {
    // change when some GameState functionality with buildings will be added
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowGeneralInfo();
    verify(callbackView).showGeneralInfoWindow();
    verifyNoMoreInteractions(callbackView);
  }
  @Test
  public void onShowBuildingsListTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    verify(callbackView).showBuildingsListWindow(buildingsList);
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void onBuildingChooseTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowBuildingsList();

    contextMenuPresenter.onBuildingChoose(0);
    verify(callbackView, never()).showBuildingsChosenWindow(any(), any());
    contextMenuPresenter.onBuildingChoose(1);
    verify(callbackView).showBuildingsChosenWindow(building.getPicture(), building.getPicture());
  }

  @Test
  public void onBuildTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    contextMenuPresenter.start();

    ArgumentCaptor<GameState.SelectionObserver> observerCaptor = ArgumentCaptor.forClass(GameState.SelectionObserver.class);
    verify(gameState).addSelectionObserver(observerCaptor.capture());
    GameState.SelectionObserver observer = observerCaptor.getValue();

    contextMenuPresenter.onBuildingChoose(1);
    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(any(), any());

    contextMenuPresenter.onBuildingChoose(0);
    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(any(), any());

    observer.onFieldSelection(new Coords(1, 0));
    doReturn(new Coords(1, 0)).when(gameState).getSelectedField();

    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(any(), any());

    contextMenuPresenter.onBuildingChoose(1);
    contextMenuPresenter.onBuild();
    verify(gameState).build(new Coords(1, 0), building.getPicture());
  }
}
