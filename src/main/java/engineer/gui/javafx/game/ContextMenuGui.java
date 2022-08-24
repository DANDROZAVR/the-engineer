package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.presenters.game.ContextMenuPresenter;
import engineer.gui.javafx.TextureManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;


public class ContextMenuGui implements ContextMenuPresenter.View {
  @FXML private VBox root;
  @FXML private AnchorPane rootBuildingTable, rootNewBuilding, rootBuildingInfo, rootGeneralInfo;
  @FXML private GridPane gridBuildingsTable;
  @FXML private Label nameNewBuilding, nameBuildingInfo, nameCurrentPlayer;
  @FXML private ImageView imageNewBuilding, imageBuildingInfo;
  @FXML private VBox resVBox;
  @FXML private VBox rootDynamicNode;
  @FXML private ListView<String> showFight;

  @FXML private HBox inputsNewBuilding;

  private TextureManager textureManager;
  @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
  private GameState gameState;
  private ContextMenuPresenter presenter;
  @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
  private VBox window;
  private final int nGridColumns = 3;

  private final ObservableList<String> fightInfo = FXCollections.observableArrayList();

  public void setup(VBox window, TextureManager textureManager, GameState gameState) {
    this.window = window;
    this.presenter = new ContextMenuPresenter(gameState, this);
    this.textureManager = textureManager;
    this.gameState = gameState;

    rootDynamicNode.getChildren().clear();
    onShowGeneralInfo();
    window.getChildren().add(root);
  }

  public void start() {
    presenter.start();
  }

  public void close() {
    presenter.close();
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

  public void onTurnEnd() {
    gameState.getTurnSystem().nextTurn();
    presenter.onShowGeneralInfo();
  }

  @Override
  public void showGeneralInfoWindow(String playerName, List<Resource> resources) {
    rootDynamicNode.getChildren().clear();
    for (int idx = 0; idx < resVBox.getChildren().size(); ++idx) {
      HBox resBox = ((HBox) resVBox.getChildren().get(idx));
      setResourcesImageAndAmountHelper(resBox.getChildren(), idx, resources);
    }
    nameCurrentPlayer.setText(playerName);
    rootDynamicNode.getChildren().add(rootGeneralInfo);
  }
  @Override
  public void showBuildingsChosenWindow(String picture, String type, List<Resource> resToBuild) {
    rootDynamicNode.getChildren().clear();
    imageNewBuilding.setImage(textureManager.getTexture(picture));
    nameNewBuilding.setText(type);
    for (int idx = 0; idx < inputsNewBuilding.getChildren().size(); ++idx) {
      VBox resBox = ((VBox) inputsNewBuilding.getChildren().get(idx));
      setResourcesImageAndAmountHelper(resBox.getChildren(), idx, resToBuild);
    }
    rootDynamicNode.getChildren().add(rootNewBuilding);
  }
  @Override
  public void showBuildingInfoWindow(String picture, String type) {
    rootDynamicNode.getChildren().clear();
    imageBuildingInfo.setImage(textureManager.getTexture(picture));
    nameBuildingInfo.setText(type);
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
        button.setStyle(getCssButtonConfigurationForPicture(buildings.get(buildingIndex).getTexture()));
      }
    }
    rootDynamicNode.getChildren().add(rootBuildingTable);
  }

  @Override
  public void startFight() {
    fightInfo.clear();
  }

  @Override
  public void showFight(List<String> newItems) {
    fightInfo.addAll(newItems);
    rootDynamicNode.getChildren().clear();
    showFight.setItems(fightInfo);
    rootDynamicNode.getChildren().add(showFight);
  }
  
  private void setResourcesImageAndAmountHelper(ObservableList<Node> boxChildren, int idx, List<Resource> resourcesToBuild) {
    ImageView resImage = ((ImageView) boxChildren.get(0));
    Label resAmount = ((Label) boxChildren.get(1));

    if (idx < resourcesToBuild.size()) {
      resImage.setImage(textureManager.getTexture(resourcesToBuild.get(idx).getTexture()));
      resAmount.setText(String.valueOf(resourcesToBuild.get(idx).getResAmount()));
    } else {
      resImage.setImage(null);
      resAmount.setText("");
    }
  }

  private String getCssButtonConfigurationForPicture(String picture) {
    return "-fx-background-image: url('" + textureManager.getTexturePath() + picture + ".png'); -fx-background-size: 66.6px 66.6px; -fx-background-color: transparent;";
  }
}
