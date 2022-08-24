package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
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
  @Mock private GameState gameState;
  @Mock private Board board;
  @Mock private ContextMenuPresenter.View callbackView;
  @Mock private TurnSystem turnSystem;
  @Mock private Player player;
  @Mock private MobsController mobsController;
  @Mock private FightSystem fightSystem;
  @Mock private Mob mob1;
  @Mock private Mob mob2;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);

    buildingList = Arrays.asList(null, building);
    doReturn(buildingList).when(gameState).getAllBuildingsList();
    doReturn(null).when(emptyField).getBuilding();
    doReturn(building).when(nonEmptyField).getBuilding();
    doReturn(board).when(gameState).getBoard();
    doReturn(emptyField).when(board).getField(new Coords(1, 0));
    doReturn(nonEmptyField).when(board).getField(new Coords(0, 1));
    doReturn(turnSystem).when(gameState).getTurnSystem();
    doReturn(player).when(turnSystem).getCurrentPlayer();
    doReturn(mobsController).when(gameState).getMobsController();
    doReturn(fightSystem).when(mobsController).getFightSystem();
    doReturn("texture").when(building).getTexture();
    doReturn("type").when(building).getType();
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testObserver() {
    ContextMenuPresenter presenter = new ContextMenuPresenter(gameState, callbackView);
    ArgumentCaptor<Board.Observer> observerCaptor = ArgumentCaptor.forClass(Board.Observer.class);

    verify(board, never()).addObserver(observerCaptor.capture());
    presenter.start();
    verify(board).addObserver(observerCaptor.capture());

    Board.Observer observer = observerCaptor.getValue();

    observer.onSelectionChanged(new Coords(0, 1));
    verify(callbackView, atLeastOnce()).showBuildingInfoWindow("texture", "type");
    verify(callbackView, never()).showBuildingsListWindow(any());

    observer.onSelectionChanged(new Coords(1, 0));
    verify(callbackView, atLeastOnce()).showBuildingsListWindow(buildingList);
    presenter.close();
    verify(board).removeObserver(observer);
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void testFightObserver() {
    ContextMenuPresenter presenter = new ContextMenuPresenter(gameState, callbackView);
    ArgumentCaptor<FightSystem.Observer> observerCaptor = ArgumentCaptor.forClass(FightSystem.Observer.class);

    verify(fightSystem, never()).addObserver(observerCaptor.capture());
    presenter.start();
    verify(fightSystem).addObserver(observerCaptor.capture());

    FightSystem.Observer observer = observerCaptor.getValue();

    observer.onFightStart(mob1, mob2);
    verify(callbackView, atLeastOnce()).startFight();
    verify(callbackView, atLeastOnce()).showFight(any());

    observer.onFightTurn(1, 1);
    verify(callbackView, atLeastOnce()).showFight(any());
    presenter.close();
    verify(fightSystem).removeObserver(observer);
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void onGeneralInfoTest() {
    // change when some GameState functionality with buildings will be added
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowGeneralInfo();
    verify(callbackView).showGeneralInfoWindow(any(), any());
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void onShowBuildingsListTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    verify(callbackView).showBuildingsListWindow(buildingList);
    verifyNoMoreInteractions(callbackView);
  }

  @Test
  public void onBuildingChooseTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowBuildingsList();

    contextMenuPresenter.onBuildingChoose(0);
    verify(callbackView, never()).showBuildingsChosenWindow(any(), any(), any());
    contextMenuPresenter.onBuildingChoose(1);
    verify(callbackView).showBuildingsChosenWindow(eq("texture"), eq("type"), any());
  }

  @Test
  public void onBuildTest() {
    ContextMenuPresenter contextMenuPresenter = new ContextMenuPresenter(gameState, callbackView);
    contextMenuPresenter.onShowBuildingsList();
    contextMenuPresenter.start();

    ArgumentCaptor<Board.Observer> observerCaptor = ArgumentCaptor.forClass(Board.Observer.class);
    verify(board).addObserver(observerCaptor.capture());
    Board.Observer observer = observerCaptor.getValue();

    contextMenuPresenter.onBuildingChoose(1);
    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(any(), any());

    contextMenuPresenter.onBuildingChoose(0);
    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(any(), any());

    observer.onSelectionChanged(new Coords(1, 0));
    doReturn(new Coords(1, 0)).when(board).getSelectedCoords();

    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(any(), any());

    contextMenuPresenter.onBuildingChoose(1);
    contextMenuPresenter.onBuild();
    verify(gameState, never()).build(new Coords(1, 0), buildingList.get(1).getType());

    /* now we "will add" resources to the player */
    doReturn(true).when(player).retrieveResourcesFromSchema(any());
    contextMenuPresenter.onBuild();
    verify(gameState).build(new Coords(1, 0), buildingList.get(1).getType());
  }
}
