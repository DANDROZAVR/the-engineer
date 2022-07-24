package engineer.gui.javafx.menu;

import static engineer.gui.javafx.game.GameGui.title;

import engineer.engine.gamestate.Board;
import engineer.engine.gamestate.BoardDescription;
import engineer.engine.gamestate.FieldFactoryImpl;
import engineer.engine.presenters.BoardPresenter;
import engineer.gui.javafx.TextureManager;
import engineer.gui.javafx.controllers.MouseController;
import engineer.gui.javafx.game.GameGui;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuGui {
  private static Stage window;

  private static final int windowWidth = 1080;
  private static final int windowHeight = 720;

  private final Scene startingScene;
  private final GameGui gameGui;

  @SuppressWarnings("FieldCanBeLocal")
  private final TextureManager textureManager = new TextureManager();

  public MenuGui() {
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(5.);

    Image exitImg = textureManager.getTexture("exit");
    ImageView exitImgView = new ImageView(exitImg);
    exitImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> window.close());

    Image startGameImg = textureManager.getTexture("startGame");
    ImageView startGameImgView = new ImageView(startGameImg);
    startGameImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> startGame());

    vbox.getChildren().addAll(startGameImgView, exitImgView);

    StackPane root = new StackPane();
    Image backgroundImg = textureManager.getTexture("startBackground");
    ImageView backgroundImgView = new ImageView(backgroundImg);
    backgroundImgView.setFitWidth(windowWidth);
    backgroundImgView.setFitHeight(windowHeight);

    root.getChildren().addAll(backgroundImgView, vbox);
    startingScene = new Scene(root, windowWidth, windowHeight);

    window = new Stage();
    gameGui = new GameGui(window, textureManager, this::start);
  }

  public void start() {
    window.setTitle(title);
    window.setResizable(false);
    window.setScene(startingScene);
    window.show();
  }

  public void startGame() {
    Board board =
        new Board(
            new FieldFactoryImpl(),
            new BoardDescription() {
              @Override
              public int getRows() {
                return 40;
              }

              @Override
              public int getColumns() {
                return 50;
              }

              @Override
              public String getBackground(int row, int column) {
                if ((row + column) % 5 == 1) return "wood";
                return "tile";
              }
            });

    // Sample
    gameGui.start(
        () -> {
          MouseController mouseController = new MouseController();
          BoardPresenter boardPresenter = new BoardPresenter(board, gameGui.getBoardGui());
          gameGui.getBoardGui().start(boardPresenter, mouseController);
        });
  }
}
