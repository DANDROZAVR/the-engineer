package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.gui.javafx.TextureManager;
import engineer.gui.javafx.menu.MenuGui;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameGui {
  public interface MenuController {
    void endGame();
  }

  public static final String TITLE = "The Engineer";

  public static void start(Stage window, MenuController menuController) {
    try {
      FXMLLoader loader = new FXMLLoader();
      URL path = MenuGui.class.getResource("/fxml/game.fxml");
      loader.setLocation(path);
      loader.load();

      GameGui gameGui = loader.getController();
      gameGui.setup(window, menuController);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Stage window;
  private MenuController menuController;

  @FXML private StackPane root;
  private Scene scene;

  @FXML private HBox toolbar;
  @FXML private Canvas board;
  @FXML private Canvas minimap;
  @FXML private VBox contextMenu;
  @FXML private VBox pauseMenu;

  private final TextureManager textureManager = new TextureManager();
  private BoardGui boardGui;

  private void setup(Stage window, MenuController menuController) {
    this.window = window;
    this.menuController = menuController;
    scene = new Scene(root);

    // TODO: temporary solution
    GameState gameState =
        new GameState(new BoardFactory(new FieldFactory(), new BuildingFactory()));
    boardGui = new BoardGui(board, textureManager, gameState);

    root.getChildren().remove(pauseMenu);

    startGame();
  }

  private void startGame() {
    window.setTitle(TITLE);
    window.setResizable(false);
    window.setScene(scene);

    boardGui.start();

    window.show();
  }

  public void pauseGame() {
    root.getChildren().add(pauseMenu);
  }

  public void resumeGame() {
    root.getChildren().remove(pauseMenu);
  }

  public void endGame() {
    menuController.endGame();
  }
}
