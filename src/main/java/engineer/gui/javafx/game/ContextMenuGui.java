package engineer.gui.javafx.game;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingsController;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.engine.presenters.game.ContextMenuPresenter;
import engineer.gui.javafx.TextureManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;


public class ContextMenuGui implements ContextMenuPresenter.View {

  @FXML private VBox root;
  @FXML private AnchorPane rootBuildingTable, rootNewBuilding, rootBuildingInfo, rootGeneralInfo, rootMobInfo;
  @FXML private GridPane gridBuildingsTable;
  @FXML private Label nameNewBuilding, nameBuildingInfo, nameCurrentPlayer, infoMob, buildingHp;
  @FXML private ImageView imageNewBuilding, imageBuildingInfo;
  @FXML private VBox resVBox;
  @FXML private VBox rootDynamicNode;
  @FXML private HBox inputsNewBuilding;
  @FXML private HBox resBuildingUpgrade;
  @FXML private HBox resBuildingProduction;
  @FXML private Slider chooseMobNumber, numberOfMobsToMove;
  @FXML private Label chooseMobLabel;
  @FXML private Button destroyButton, updateButton, produceMobsButton;
  private TextureManager textureManager;
  private ContextMenuPresenter presenter;
  private TurnSystem turnSystem;
  @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
  private VBox window;
  private final int nGridColumns = 3;
  public void setup(VBox window, TextureManager textureManager, Board board, MobsController mobsController, TurnSystem turnSystem, BuildingsController buildingsController) {
    this.window = window;
    this.presenter = new ContextMenuPresenter(
        board,
        mobsController,
        turnSystem,
        buildingsController,
        this
    );
    this.turnSystem = turnSystem;
    this.textureManager = textureManager;
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
    turnSystem.nextTurn();
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
  public void showBuildingsChosenWindow(String picture, String type, List<Resource> resToBuild, List<Resource> resToUpgrade, List<Resource> resProduction) {
    rootDynamicNode.getChildren().clear();
    imageNewBuilding.setImage(textureManager.getTexture(picture));
    nameNewBuilding.setText(type);
    for (int idx = 0; idx < inputsNewBuilding.getChildren().size(); ++idx) {
      VBox resBox = ((VBox) inputsNewBuilding.getChildren().get(idx));
      setResourcesImageAndAmountHelper(resBox.getChildren(), idx, resToBuild);
    }
    for (int idx = 0; idx < resBuildingUpgrade.getChildren().size(); ++idx) {
      VBox resBox = ((VBox) resBuildingUpgrade.getChildren().get(idx));
      setResourcesImageAndAmountHelper(resBox.getChildren(), idx, resToUpgrade);
    }
    for (int idx = 0; idx < resBuildingProduction.getChildren().size(); ++idx) {
      VBox resBox = ((VBox) resBuildingProduction.getChildren().get(idx));
      setResourcesImageAndAmountHelper(resBox.getChildren(), idx, resProduction);
    }
    rootDynamicNode.getChildren().add(rootNewBuilding);
  }
  @Override
  public void showBuildingInfoWindow(String picture, String type, int level, int life, boolean isOwner) {
    rootDynamicNode.getChildren().clear();
    imageBuildingInfo.setImage(textureManager.getTexture(picture));
    nameBuildingInfo.setText(type + " level " + level);
    buildingHp.setText("Remaining HP: " + life);
    if (isOwner && !rootBuildingInfo.getChildren().contains(destroyButton)) {
      rootBuildingInfo.getChildren().add(destroyButton);
      rootBuildingInfo.getChildren().add(updateButton);
      rootBuildingInfo.getChildren().add(produceMobsButton);
      rootBuildingInfo.getChildren().add(chooseMobNumber);
    }
   if (!isOwner) {
      rootBuildingInfo.getChildren().remove(destroyButton);
      rootBuildingInfo.getChildren().remove(updateButton);
      rootBuildingInfo.getChildren().remove(produceMobsButton);
      rootBuildingInfo.getChildren().remove(chooseMobNumber);
    }
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
  public void showMobInfo(String type, int amount, boolean isOwner, int mobsLife, int mobsAttack) {
    rootDynamicNode.getChildren().clear();
    infoMob.setText("     " + amount + " " + type + " mobs\n     Each has " + mobsLife + " lifes\n     And his attack is " + mobsAttack);
    rootMobInfo.getChildren().remove(chooseMobLabel);
    rootMobInfo.getChildren().remove(numberOfMobsToMove);
    if (isOwner) {
      rootMobInfo.getChildren().add(chooseMobLabel);
      numberOfMobsToMove.setMax(amount);
      rootMobInfo.getChildren().add(numberOfMobsToMove);
    }
    rootDynamicNode.getChildren().add(rootMobInfo);
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

  public void onDestroy() {
    presenter.onDestroy();
  }

  public void onUpgrade() {
    presenter.onUpgrade();
  }

  public void onMobProductionRequest() {
    int number = (int) chooseMobNumber.getValue();
    presenter.onMobProductionRequest(number);
  }

  public void changeNumberOfMobsSelected() {
    presenter.setNumberOfMobsToMove((int) numberOfMobsToMove.getValue());
  }
}
