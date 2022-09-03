package engineer.engine.presenters.game;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingsController;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class ContextMenuPresenterTest {
  private AutoCloseable closeable;

  private List<Building> buildingList;
  @Mock private Field emptyField;
  @Mock private Field nonEmptyField;
  @Mock private Building building;
  @Mock private Board board;
  @Mock private ContextMenuPresenter.View callbackView;
  @Mock private TurnSystem turnSystem;
  @Mock private Player player;
  @Mock private MobsController mobsController;
  @Mock private FightSystem fightSystem;
  @Mock private Mob mob1;
  @Mock private Mob mob2;
  @Mock private BuildingsController buildingsController;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    buildingList = Arrays.asList(null, building);
    doReturn(buildingList).when(buildingsController).getAllBuildingsList();
    doReturn(null).when(emptyField).getBuilding();
    doReturn(building).when(nonEmptyField).getBuilding();
    doReturn(emptyField).when(board).getField(new Coords(1, 0));
    doReturn(nonEmptyField).when(board).getField(new Coords(0, 1));
    doReturn(player).when(turnSystem).getCurrentPlayer();
    doReturn(fightSystem).when(mobsController).getFightSystem();
    doReturn("texture").when(building).getTexture();
    doReturn("type").when(building).getType();
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }
/*
  @Test
  public void testObserver() {
    ContextMenuPresenter presenter = new ContextMenuPresenter(board, mobsController, fightSystem, turnSystem, buildingsController, callbackView);
    ArgumentCaptor<Board.Observer> observerCaptor = ArgumentCaptor.forClass(Board.Observer.class);

    verify(board, never()).addObserver(observerCaptor.capture());
    presenter.start();
    verify(board).addObserver(observerCaptor.capture());

    Board.Observer observer = observerCaptor.getValue();

    observer.onSelectionChanged(new Coords(0, 1));
    //verify(callbackView, atLeastOnce()).showBuildingInfoWindow("texture", "type", 0);
    verify(callbackView, never()).showBuildingsListWindow(any());

    observer.onSelectionChanged(new Coords(1, 0));
    verify(callbackView, atLeastOnce()).showBuildingsListWindow(buildingList);
    presenter.close();
    verify(board).removeObserver(observer);
    verifyNoMoreInteractions(callbackView);
  }
*/

  @Test
  public void onGeneralInfoTest() {
    // change when some GameState functionality with buildings will be added
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowGeneralInfo();
    verify(callbackView).showGeneralInfoWindow(any(), any());
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void onShowBuildingsListTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    verify(callbackView).showBuildingsListWindow(buildingList);
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void onBuildingChooseTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowBuildingsList();

    contextMenuPresenter.onBuildingChoose(0);
    verify(callbackView, never()).showBuildingsChosenWindow(any(), any(), any());
    contextMenuPresenter.onBuildingChoose(1);
    verify(callbackView).showBuildingsChosenWindow(eq("texture"), eq("type"), any());
  }

  @Test
  public void onBuildTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    contextMenuPresenter.start();

    ArgumentCaptor<Board.Observer> observerCaptor = ArgumentCaptor.forClass(Board.Observer.class);
    verify(board).addObserver(observerCaptor.capture());
    Board.Observer observer = observerCaptor.getValue();

    contextMenuPresenter.onBuildingChoose(1);
    contextMenuPresenter.onBuild();
    verify(buildingsController, never()).build(any(), any(), any());

    contextMenuPresenter.onBuildingChoose(0);
    contextMenuPresenter.onBuild();
    verify(buildingsController, never()).build(any(), any(), any());

    observer.onSelectionChanged(new Coords(1, 0));
    doReturn(new Coords(1, 0)).when(board).getSelectedCoords();

    contextMenuPresenter.onBuild();
    verify(buildingsController, never()).build(any(), any(), any());

    contextMenuPresenter.onBuildingChoose(1);
    contextMenuPresenter.onBuild();
    verify(buildingsController, never()).build(any(), any(), any());

    /* now we "will add" resources to the player */
    doReturn(true).when(player).retrieveResourcesFromSchema(any(), anyInt());
    contextMenuPresenter.onBuild();
    verify(buildingsController).build(new Coords(1, 0), buildingList.get(1).getType(), turnSystem.getCurrentPlayer());
  }
/*
  @Test
  public void testOnDestroy() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, fightSystem, turnSystem, buildingsController, callbackView);
    Coords selectedField = new Coords(0, 1);

    contextMenuPresenter.onDestroy();
    verify(board).getSelectedCoords();
    verify(buildingsController, never()).destroyBuilding(any());
    clearInvocations(board);

    doReturn(selectedField).when(board).getSelectedCoords();
    contextMenuPresenter.onDestroy();
    verify(board).getSelectedCoords();
    verify(buildingsController).destroyBuilding(selectedField);
  }

  @Test
  public void onUpgrade() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, fightSystem, turnSystem, buildingsController, callbackView);
    Coords selectedField = new Coords(0, 1);
    Field field = mock(Field.class);

    doReturn(selectedField).when(board).getSelectedCoords();
    doReturn(true).when(player).retrieveResourcesFromSchema(any(), 1);
    doReturn(field).when(board).getField(selectedField);
    doReturn(1).when(building).getLevel();
    doReturn(building).when(field).getBuilding();

    contextMenuPresenter.onFieldSelection(selectedField);
    //verify(callbackView).showBuildingInfoWindow(any(), any(), eq(1));

    doReturn(3).when(building).getLevel();
    contextMenuPresenter.onUpgrade();
    verify(building).upgrade();
    //verify(callbackView).showBuildingInfoWindow(any(), any(), eq(3));
  }

  @Test
  public void onShowMobInfo() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, fightSystem, turnSystem, buildingsController, callbackView);
    Field field = mock(Field.class);
    Mob mob = mock(Mob.class);

    doReturn(mob).when(field).getMob();
    doReturn(field).when(board).getField(any());

    contextMenuPresenter.onFieldSelection(new Coords(0, 0));
    //verify(callbackView).showMobInfo();
  }
  */
}
