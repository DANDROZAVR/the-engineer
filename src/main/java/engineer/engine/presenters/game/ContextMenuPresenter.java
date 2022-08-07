package engineer.engine.presenters.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.field.Field;
import engineer.utils.Pair;

import java.util.List;

public class ContextMenuPresenter {
  public interface View {
    void showGeneralInfoWindow();
    void showBuildingsChosenWindow(String picture, String name);
    void showBuildingInfoWindow(String picture, String name);
    void showBuildingsListWindow(List<Building> buildings);
  }

  private final GameState gameState;
  private final View view;
  private List<Building> tempListOfAllBuildings;
  @SuppressWarnings("FieldCanBeLocal")
  public ContextMenuPresenter(GameState gameState, View view) {
    this.gameState = gameState;
    this.view = view;
  }
  private Building chosenBuilding;

  private final GameState.SelectionObserver selectionObserver = this::onFieldSelection;

  public void start() {
    gameState.addSelectionObserver(selectionObserver);
  }

  public void close() {
    gameState.removeSelectionObserver(selectionObserver);
  }

  public void onFieldSelection(Pair fieldXY) {
    Field field = gameState.getField(fieldXY.first(), fieldXY.second());
    if (field.getBuilding() == null) {
      // we should somehow understand that field is covered with some resources
      onShowBuildingsList();
    } else {
      view.showBuildingInfoWindow(field.getBuilding().getPicture(), field.getBuilding().getPicture());
    }
  }

  public void onShowGeneralInfo() {
    // gamestate.getAllYourSecrets()
    view.showGeneralInfoWindow();
  }
  public void onBuildingChoose(int index){
    Building building = tempListOfAllBuildings.get(index);
    chosenBuilding = null;
    if (building != null) {
      chosenBuilding = building;
      view.showBuildingsChosenWindow(building.getPicture(), building.getPicture());
    }
  }
  public void onShowBuildingsList() {
    tempListOfAllBuildings = gameState.getAllBuildingsList();
    view.showBuildingsListWindow(tempListOfAllBuildings);
  }
  public void onBuild() {
    Pair selectedField = gameState.getSelectedField();
    if (chosenBuilding != null && selectedField != null) {
      gameState.build(selectedField.first(), selectedField.second(), chosenBuilding.getPicture());
      view.showBuildingInfoWindow(chosenBuilding.getPicture(), chosenBuilding.getPicture());
    }
  }
}
