package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.building.Building;
import engineer.engine.presenters.ContextMenuPresenter;
import engineer.gui.javafx.TextureManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;


public class ContextMenuGui implements ContextMenuPresenter.View {
  @FXML private VBox root;
  @FXML private AnchorPane rootBuildingTable, rootNewBuilding, rootBuildingInfo, rootGeneralInfo;
  @FXML private GridPane gridBuildingsTable;
  @FXML private Label nameNewBuilding, nameBuildingInfo;
  @FXML private ImageView imageNewBuilding, imageBuildingInfo;
  @FXML private StackPane rootDynamicNode;

  private TextureManager textureManager;
  @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
  private GameState gameState;
  private ContextMenuPresenter presenter;
  @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
  private VBox window;
  private final int nGridColumns = 3;
  public void setup(VBox window, TextureManager textureManager, GameState gameState) {
    this.window = window;
    this.presenter = new ContextMenuPresenter(gameState, this);
    this.textureManager = textureManager;
    this.gameState = gameState;

    rootDynamicNode.getChildren().clear();
    rootDynamicNode.getChildren().add(rootGeneralInfo);
    window.getChildren().add(root);
  }

  public void onShowBuildingsList() {
    presenter.onShowBuildingsList();
  }
  public void onShowGeneralInfo() { presenter.onShowGeneralInfo(); }
  public void onBuild() {
    presenter.onBuild();
  }

  public void onBuildingChoose(ActionEvent e) {
    String button =  e.getSource().toString();
    for (Node child : gridBuildingsTable.getChildren()) {
      if (child.toString().equals(button)) {
        int row  = GridPane.getRowIndex(child) == null ? 0 : GridPane.getRowIndex(child);
        int col = GridPane.getColumnIndex(child) == null ? 0 : GridPane.getColumnIndex(child);
        presenter.onBuildingChoose(row * nGridColumns + col);
        return;
      }
    }
  }

  @Override
  public void showGeneralInfoWindow() {
    rootDynamicNode.getChildren().clear();
    rootDynamicNode.getChildren().add(rootGeneralInfo);
  }

  @Override
  public void showBuildingsChosenWindow(String picture, String name) {
    rootDynamicNode.getChildren().clear();
    imageNewBuilding.setImage(textureManager.getTexture(picture));
    nameNewBuilding.setText(name);
    rootDynamicNode.getChildren().add(rootNewBuilding);
  }
  @Override
  public void showBuildingInfoWindow(String picture, String name) {
    rootDynamicNode.getChildren().clear();
    imageBuildingInfo.setImage(textureManager.getTexture(picture));
    nameBuildingInfo.setText(name);
    rootDynamicNode.getChildren().add(rootBuildingInfo);
  }
  @Override
  public void showBuildingsListWindow(List<Building> buildings) {
    rootDynamicNode.getChildren().clear();
    for (Node node : gridBuildingsTable.getChildren()) {
      int row = GridPane.getRowIndex(node) == null ? 0 : GridPane.getRowIndex(node);
      int col = GridPane.getColumnIndex(node) == null ? 0 : GridPane.getColumnIndex(node);
      int buildingIndex = row * nGridColumns + col;
      if (buildingIndex >= buildings.size()) break;
      if (node instanceof Button button && buildings.get(buildingIndex) != null) {
        button.setStyle(getCssButtonConfigurationForPicture(buildings.get(buildingIndex).getPicture()));
      }
    }
    rootDynamicNode.getChildren().add(rootBuildingTable);
  }

  private String getCssButtonConfigurationForPicture(String picture) {
    return "-fx-background-image: url('" + textureManager.getTexturePath() + picture + ".png'); -fx-background-size: 66.6px 66.6px; -fx-background-color: transparent;";
  }
}
