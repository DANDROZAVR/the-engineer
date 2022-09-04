package engineer.gui.javafx.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameStateConverter;
import engineer.engine.gamestate.GameStateFactory;
import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.board.BoardFactory;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.building.BuildingsController;
import engineer.engine.gamestate.field.FieldFactory;
import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.mob.MobFactory;
import engineer.engine.gamestate.mob.MobsController;
import engineer.engine.gamestate.resource.ResourceFactory;
import engineer.engine.gamestate.turns.Player;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.gui.javafx.GuiLoader;
import engineer.gui.javafx.TextureManager;
import engineer.utils.JsonSaver;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GameGui {
  public interface MenuController {
    void endGame();
  }

  public static final String TITLE = "The Engineer";
  private static final String lastGamePath = "src/main/resources/board/lastGame.json";

  public static void start(Stage window, MenuController menuController, String jsonGamePath) {
    GameGui gameGui = GuiLoader.loadGui("/fxml/game.fxml");
    gameGui.setup(window, menuController, jsonGamePath);
  }


  private Stage window;
  private MenuController menuController;

  @FXML private StackPane root;
  private Scene scene;

  @SuppressWarnings("unused")
  @FXML private HBox toolbar;
  @FXML private Canvas boardCanvas;
  @FXML private StackPane minimap;
  @FXML private VBox contextMenu;

  private final TextureManager textureManager = new TextureManager();
  private BoardGui boardGui;
  private MinimapGui minimapGui;
  private ContextMenuGui contextMenuGui;
  private PauseGui pauseGui;
  private EndGameGui endGameGui;
  private FightGui fightGui;

  private Board tempBoard;
  private List<Player> tempPlayers;

  private void setup(Stage window, MenuController menuController, String jsonGamePath) {
    this.window = window;
    this.menuController = menuController;
    GameStateFactory gameStateFactory = new GameStateFactory();
    FieldFactory fieldFactory = new FieldFactory(); // should it be also in gamestate?
    ResourceFactory resourceFactory = gameStateFactory.produceResourceFactory();
    MobFactory mobFactory = gameStateFactory.produceMobFactory(resourceFactory);
    BuildingFactory buildingFactory = gameStateFactory.produceBuildingFactory(resourceFactory, mobFactory);
    BoardFactory boardFactory = gameStateFactory.produceBoardFactory(fieldFactory);
    FightSystem fightSystem = gameStateFactory.produceFightSystem();
    List<Player> players = gameStateFactory.producePlayers(jsonGamePath, resourceFactory);
    Board board = gameStateFactory.produceBoard(boardFactory, jsonGamePath, buildingFactory, mobFactory, players);
    Camera camera = gameStateFactory.produceCamera(board, boardCanvas.getWidth(), boardCanvas.getHeight());

    TurnSystem turnSystem = gameStateFactory.produceTurnSystem(players);
    MobsController mobsController = gameStateFactory.produceMobsController(board, turnSystem, mobFactory, fightSystem);

    BuildingsController buildingsController = gameStateFactory.produceBuildingController(boardFactory, buildingFactory, board, turnSystem);

    boardGui = new BoardGui(boardCanvas, textureManager, board, camera);
    minimapGui = new MinimapGui(minimap, textureManager, board, camera);
    contextMenuGui = GuiLoader.loadGui("/fxml/contextMenu.fxml");
    contextMenuGui.setup(contextMenu, textureManager, board, mobsController, turnSystem, buildingsController);
    pauseGui = GuiLoader.loadGui("/fxml/pause.fxml");
    pauseGui.setup(root, this::endGame);
    endGameGui = GuiLoader.loadGui("/fxml/endGame.fxml");
    endGameGui.setup(root, board, turnSystem, this::endGame);
    fightGui = GuiLoader.loadGui("/fxml/fight.fxml");
    fightGui.setup(root, fightSystem);

    tempBoard = board;
    tempPlayers = players;

    scene = new Scene(root);
    startGame();
    root.getScene().getWindow().setOnCloseRequest(x -> endGame(false));
  }

  private void startGame() {
    window.setTitle(TITLE);
    window.setResizable(false);
    window.setScene(scene);

    boardGui.start();
    minimapGui.start();
    contextMenuGui.start();
    endGameGui.start();
    fightGui.start();

    window.show();
  }

  public void pauseGame() {
    pauseGui.pauseGame();
  }

  public void endGame(boolean endedNormally) {
    if (!endedNormally) {
      new JsonSaver().saveJson(lastGamePath, GameStateConverter.produceJsonFromBoard(tempBoard, tempPlayers));
    } else {
      new JsonSaver().clearJson(lastGamePath);
    }

    boardGui.close();
    minimapGui.close();
    contextMenuGui.close();
    menuController.endGame();
  }
}
