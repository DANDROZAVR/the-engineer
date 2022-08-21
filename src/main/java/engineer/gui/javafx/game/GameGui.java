package engineer.gui.javafx.game;

import engineer.engine.gamestate.GameState;
import engineer.gui.javafx.GuiLoader;
import engineer.gui.javafx.TextureManager;
import javafx.fxml.FXML;
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
    GameGui gameGui = GuiLoader.loadGui("/fxml/game.fxml");
    gameGui.setup(window, menuController);
  }



  private Stage window;
  private MenuController menuController;

  @FXML private StackPane root;
  private Scene scene;

  @SuppressWarnings("unused")
  @FXML private HBox toolbar;
  @FXML private Canvas board;
  @FXML private StackPane minimap;
  @FXML private VBox contextMenu;

  private final TextureManager textureManager = new TextureManager();
  private BoardGui boardGui;
  private MinimapGui minimapGui;
  private ContextMenuGui contextMenuGui;
  private PauseGui pauseGui;

  private void setup(Stage window, MenuController menuController) {
    this.window = window;
    this.menuController = menuController;
    scene = new Scene(root);

    GameState gameState = new GameState(board.getWidth(), board.getHeight());

    boardGui = new BoardGui(board, textureManager, gameState);
    minimapGui = new MinimapGui(minimap, textureManager, gameState);

    contextMenuGui = GuiLoader.loadGui("/fxml/contextMenu.fxml");
    contextMenuGui.setup(contextMenu, textureManager, gameState);

    pauseGui = GuiLoader.loadGui("/fxml/pause.fxml");
    pauseGui.setup(root, this::endGame);

    startGame();
  }

  private void startGame() {
    window.setTitle(TITLE);
    window.setResizable(false);
    window.setScene(scene);

    boardGui.start();
    minimapGui.start();
    contextMenuGui.start();

    window.show();
  }

  public void pauseGame() {
    pauseGui.pauseGame();
  }

  public void endGame() {
    boardGui.close();
    minimapGui.close();
    contextMenuGui.close();

    menuController.endGame();
  }
}
