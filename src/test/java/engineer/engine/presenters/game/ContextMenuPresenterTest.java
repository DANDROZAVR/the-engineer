package engineer.engine.presenters.game;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingsController;
import engineer.engine.gamestate.field.Field;
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
  @Mock private Mob mob1;
  @Mock private BuildingsController buildingsController;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    buildingList = Arrays.asList(null, building);
    doReturn(buildingList).when(buildingsController).getAllBuildingsList();
    doReturn(null).when(emptyField).getBuilding();
    doReturn(building).when(nonEmptyField).getBuilding();
    doReturn(player).when(building).getOwner();
    doReturn(emptyField).when(board).getField(new Coords(1, 0));
    doReturn(nonEmptyField).when(board).getField(new Coords(0, 1));
    doReturn(player).when(turnSystem).getCurrentPlayer();
    doReturn(player).when(mob1).getOwner();
    doReturn("texture").when(building).getTexture();
    doReturn("type").when(building).getType();
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testObserver() {
    ContextMenuPresenter presenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    ArgumentCaptor<Board.Observer> observerCaptor = ArgumentCaptor.forClass(Board.Observer.class);

    verify(board, never()).addObserver(observerCaptor.capture());
    presenter.start();
    verify(board).addObserver(observerCaptor.capture());

    Board.Observer observer = observerCaptor.getValue();

    observer.onSelectionChanged(new Coords(0, 1));
    verify(callbackView, never()).showBuildingsListWindow(any());

    observer.onSelectionChanged(new Coords(1, 0));
    verify(callbackView, atLeastOnce()).showBuildingsListWindow(buildingList);
    presenter.close();
    verify(board).removeObserver(observer);
  }

  @Test
  public void testOnGeneralInfoTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowGeneralInfo();
    verify(callbackView).showGeneralInfoWindow(any(), any());
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void testOnShowBuildingsListTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    verify(callbackView).showBuildingsListWindow(buildingList);
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void testOnBuildingChooseTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    contextMenuPresenter.onShowBuildingsList();

    contextMenuPresenter.onBuildingChoose(0);
    verify(callbackView, never()).showBuildingsChosenWindow(any(), any(), any(), any(), any());
    contextMenuPresenter.onBuildingChoose(1);
    verify(callbackView).showBuildingsChosenWindow(eq("texture"), eq("type"), any(), any(), any());
  }

  @Test
  public void testOnBuildTest() {
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

  @Test
  public void testOnDestroy() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
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
  public void testOnUpgrade() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    Coords selectedField = new Coords(0, 1);
    Field field = mock(Field.class);

    doReturn(selectedField).when(board).getSelectedCoords();
    doReturn(true).when(player).retrieveResourcesFromSchema(any(), eq(1));
    doReturn(field).when(board).getField(selectedField);
    doReturn(1).when(building).getLevel();
    doReturn(building).when(field).getBuilding();

    contextMenuPresenter.onFieldSelection(selectedField);
    verify(callbackView).showBuildingInfoWindow(any(), any(), eq(1), anyInt(), anyBoolean());

    doReturn(3).when(building).getLevel();
    contextMenuPresenter.onUpgrade();
    verify(building).upgrade();
    verify(callbackView).showBuildingInfoWindow(any(), any(), eq(1), anyInt(), anyBoolean());
  }

  @Test
  public void testOnShowMobInfo() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    Field field = mock(Field.class);
    Mob mob = mock(Mob.class);

    doReturn(mob).when(field).getMob();
    doReturn(field).when(board).getField(any());
    doReturn(player).when(mob).getOwner();
    doReturn("type").when(mob).getType();
    doReturn(1).when(mob).getMobsAmount();
    doReturn(2).when(mob).getMobsAttack();
    doReturn(3).when(mob).getMobsLife();

    contextMenuPresenter.onFieldSelection(new Coords(0, 0));
    verify(callbackView).showMobInfo(eq(mob.getType()), eq(1), eq(true), eq(3), eq(2));
  }

  @Test
  public void testOnMobProduction() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    Coords coords = new Coords(1, 1);
    Field field = mock(Field.class);

    doReturn(field).when(board).getField(coords);
    doReturn(building).when(field).getBuilding();
    doReturn(coords).when(board).getSelectedCoords();
    doReturn(mob1).when(building).getTypeOfProducedMob();
    doReturn(true).when(player).retrieveResourcesFromSchema(any(), eq(2));

    contextMenuPresenter.onMobProductionRequest(2);

    doReturn(null).when(field).getBuilding();
    contextMenuPresenter.onFieldSelection(coords);

    verify(mobsController).makeMob(coords, mob1.getType(), 2, turnSystem.getCurrentPlayer());
  }

  @Test
  public void testOnMobMove() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(board, mobsController, turnSystem, buildingsController, callbackView);
    ArgumentCaptor<Board.Observer> boardCaptor = ArgumentCaptor.forClass(Board.Observer.class);
    Coords coords = new Coords(0, 1);
    contextMenuPresenter.start();
    contextMenuPresenter.setNumberOfMobsToMove(5);

    verify(board).addObserver(boardCaptor.capture());
    Board.Observer observer = boardCaptor.getValue();
    observer.onSelectionChanged(coords);
    verify(mobsController).onSelectionChangedMobs(coords, 5);
  }
}
