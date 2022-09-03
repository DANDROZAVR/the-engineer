package engineer.gui.javafx.menu;

import com.google.gson.JsonObject;
import engineer.gui.javafx.GuiLoader;
import engineer.gui.javafx.game.GameGui;
import engineer.utils.JsonLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuGui {
  private static final String TITLE = "The Engineer";
  private final String lastGamePath = "src/main/resources/board/lastGame.json";
  private final String sampleGamePath = "src/main/resources/board/sample3.json";
  @FXML private Button continueButton;

  public static void start(Stage window) {
    MenuGui menuGui = GuiLoader.loadGui("/fxml/menu.fxml");
    menuGui.setup(window);
  }

  private Stage window;

  @FXML private Parent root;
  private Scene scene;

  private void setup(Stage window) {
    this.window = window;
    scene = new Scene(root);

    showMenu();
  }

  private void showMenu() {
    JsonObject jsonGame = new JsonLoader().loadJson(lastGamePath);
    continueButton.setDisable(jsonGame == null || jsonGame.size() == 0);
    window.setTitle(TITLE);
    window.setResizable(false);
    window.setScene(scene);
    window.show();
  }

  public void startGame() {
    GameGui.start(window, this::showMenu, sampleGamePath);
  }

  public void exitGame() {
    window.close();
  }

  public void continueGame() {
    GameGui.start(window, this::showMenu, lastGamePath);
  }
}
