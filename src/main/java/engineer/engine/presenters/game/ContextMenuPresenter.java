package engineer.engine.presenters.game;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingsController;
import engineer.engine.gamestate.field.Field;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.utils.Coords;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuPresenter {
  public interface View {
    void showGeneralInfoWindow(String playerName, List<Resource> resources);
    void showBuildingsChosenWindow(String picture, String type, List<Resource> input);
    void showBuildingInfoWindow(String picture, String type, int level, int life, boolean isOwner);
    void showBuildingsListWindow(List<Building> buildings);
    void showMobInfo(String type, int amount, boolean isOwner);
  }

  private final Board board;
  private final MobsController mobsController;
  private final TurnSystem turnSystem;
  private final BuildingsController buildingsController;

  private final View view;
  private List<Building> tempListOfAllBuildings;
  public ContextMenuPresenter(Board board, MobsController mobsController, TurnSystem turnSystem, BuildingsController buildingsController, View view) {
    this.board = board;
    this.view = view;
    this.mobsController = mobsController;
    this.turnSystem = turnSystem;
    this.buildingsController = buildingsController;
  }

  private Building chosenBuilding;
  private boolean mobProductionRequested;
  private Mob mobRequestedType;
  private int mobRequestedNumber;
  private int NumberOfMobsToMove = 1;

  private final Board.Observer boardObserver = new Board.Observer() {
    @Override
    public void onSelectionChanged(Coords coords) {
      mobsController.onSelectionChangedMobs(coords, NumberOfMobsToMove);
      onFieldSelection(coords);
    }
  };

  public void start() {
    board.addObserver(boardObserver);
  }

  public void close() {
    board.removeObserver(boardObserver);
  }

  public void onFieldSelection(Coords coords) {
    Field field = board.getField(coords);

    if (mobProductionRequested) {
      if (field.getBuilding() == null && field.getMob() == null) {
        onMobProduction();
      }
      mobProductionRequested = false;
    }
    if (field.getMob() != null) {
      onShowMobInfo(field.getMob());
    } else if (field.getBuilding() == null) {
      onShowBuildingsList();
    } else {
      view.showBuildingInfoWindow(field.getBuilding().getTexture(), field.getBuilding().getType(), field.getBuilding().getLevel(), field.getBuilding().getLifeRemaining(), field.getBuilding().getOwner().equals(turnSystem.getCurrentPlayer()));
    }
  }

  private void onShowMobInfo(Mob mob) {
    view.showMobInfo(mob.getType(), mob.getMobsAmount(), mob.getOwner().equals(turnSystem.getCurrentPlayer()));
  }

  public void onShowGeneralInfo() {
    mobProductionRequested = false;
    Player player = turnSystem.getCurrentPlayer();
    String nickname = player.getNickname();
    List<Resource> resources = player.getResources();
    view.showGeneralInfoWindow(nickname, resources);
  }

  public void onBuildingChoose(int index) {
    if (index >= tempListOfAllBuildings.size())
      return;
    Building building = tempListOfAllBuildings.get(index);
    chosenBuilding = null;
    if (building != null) {
      chosenBuilding = building;
      view.showBuildingsChosenWindow(building.getTexture(), building.getType(), building.getResToBuild());
    }
  }

  public void onShowBuildingsList() {
    mobProductionRequested = false;
    tempListOfAllBuildings = buildingsController.getAllBuildingsList();
    view.showBuildingsListWindow(tempListOfAllBuildings);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  public void onBuild() {
    Coords selectedField = board.getSelectedCoords();
    if (chosenBuilding != null && selectedField != null && board.getField(selectedField).getBuilding() == null && board.getField(selectedField).getMob() == null) {
      Player player = turnSystem.getCurrentPlayer();
      if (player.retrieveResourcesFromSchema(chosenBuilding.getResToBuild(), 1)) {
        buildingsController.build(selectedField, chosenBuilding.getType(), turnSystem.getCurrentPlayer());
        view.showBuildingInfoWindow(chosenBuilding.getTexture(), chosenBuilding.getType(), chosenBuilding.getLevel(), chosenBuilding.getLifeRemaining(),true);
      } else {
        // TODO: show pop-up about insufficient resources
      }
    }
  }
  public void onDestroy() {
    Coords selectedField = board.getSelectedCoords();
    if (selectedField != null) {
      buildingsController.destroyBuilding(selectedField);
      onFieldSelection(selectedField);
    }
  }

  public void onUpgrade() {
    Coords selectedField = board.getSelectedCoords();
    Player player = turnSystem.getCurrentPlayer();
    if (player.retrieveResourcesFromSchema(chosenBuilding.getResToUpgrade(), 1)) {
      Field field = board.getField(selectedField);
      field.getBuilding().upgrade();
      view.showBuildingInfoWindow(field.getBuilding().getTexture(), field.getBuilding().getType(), field.getBuilding().getLevel(), field.getBuilding().getLifeRemaining(), field.getBuilding().getOwner().equals(turnSystem.getCurrentPlayer()));
    }
  }

  public void onMobProductionRequest(int number) {
    Coords selectedField = board.getSelectedCoords();
    Field field = board.getField(selectedField);
    mobProductionRequested = true;
    mobRequestedType = field.getBuilding().getTypeOfProducedMob();
    mobRequestedNumber = number;
  }

  public void onMobProduction() {
    Coords selectedField = board.getSelectedCoords();
    Player player = turnSystem.getCurrentPlayer();
    if (player.retrieveResourcesFromSchema(mobRequestedType.getResToProduce(), mobRequestedNumber)) {
      mobsController.makeMob(selectedField, mobRequestedType.getType(), mobRequestedNumber, turnSystem.getCurrentPlayer());
    }
  }

  public void setNumberOfMobsToMove(int value) {
    NumberOfMobsToMove = value;
  }
}
