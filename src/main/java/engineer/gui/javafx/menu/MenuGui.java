package engineer.gui.javafx.menu;

import engineer.engine.gamestate.GameState;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.presenters.BoardPresenter;
import engineer.gui.javafx.TextureManager;
import engineer.gui.javafx.controllers.MouseController;
import engineer.gui.javafx.game.GameGui;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuGui {
  private static final String TITLE = "The Engineer";

  private Stage window;

  @FXML private Parent root;
  private Scene scene;

  private final TextureManager textureManager = new TextureManager();

  public void start(Stage window) {
    this.window = window;
    scene = new Scene(root);

    showMenu();
  }

  private void showMenu() {
    window.setTitle(TITLE);
    window.setResizable(false);
    window.setScene(scene);
    window.show();
  }

  public void startGame() {
    GameState gameState =
        new GameState(new BoardFactory(new FieldFactory(), new BuildingFactory()));
    GameGui gameGui = new GameGui(window, textureManager, this::showMenu);

    // Sample
    gameGui.start(
        () -> {
          MouseController mouseController = new MouseController();
          BoardPresenter boardPresenter = new BoardPresenter(gameState, gameGui.getBoardGui());
          gameGui.getBoardGui().start(boardPresenter, mouseController);
        });
  }

  public void exitGame() {
    window.close();
  }
}
