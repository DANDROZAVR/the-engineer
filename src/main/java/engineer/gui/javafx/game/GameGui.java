package engineer.gui.javafx.game;

import engineer.engine.gamestate.Camera;
import engineer.engine.gamestate.GameStateConvertor;
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
    // List<Player> players = gameStateFactory.producePlayers(List.of("Winner", "Loser"), resourceFactory);
    List<Player> players = gameStateFactory.producePlayers(jsonGamePath, resourceFactory);
    Board board = gameStateFactory.produceBoard(boardFactory, jsonGamePath, buildingFactory, mobFactory, players);
    //Board board = gameStateFactory.produceBoard(boardFactory, "/board/sample.json");
    Camera camera = gameStateFactory.produceCamera(board, boardCanvas.getWidth(), boardCanvas.getHeight());

    TurnSystem turnSystem = gameStateFactory.produceTurnSystem(players);
    MobsController mobsController = gameStateFactory.produceMobsController(board, turnSystem, mobFactory, fightSystem);

    BuildingsController buildingsController = gameStateFactory.produceBuildingController(boardFactory, buildingFactory, board, turnSystem);

    boardGui = new BoardGui(boardCanvas, textureManager, board, camera);
    minimapGui = new MinimapGui(minimap, textureManager, board, camera);
    contextMenuGui = GuiLoader.loadGui("/fxml/contextMenu.fxml");
    contextMenuGui.setup(contextMenu, textureManager, board, mobsController, fightSystem, turnSystem, buildingsController);
    pauseGui = GuiLoader.loadGui("/fxml/pause.fxml");
    pauseGui.setup(root, this::endGame);

    //root.getScene().getWindow().setOnCloseRequest(x ->
    //        new JsonSaver().saveJson("src/main/resources/board/lastGame.json", GameStateConvertor.produceJsonFromBoard(board, players)));

    tempBoard = board;
    tempPlayers = players;

    scene = new Scene(root);
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
    new JsonSaver().saveJson("src/main/resources/board/lastGame.json", GameStateConvertor.produceJsonFromBoard(tempBoard, tempPlayers));
    boardGui.close();
    minimapGui.close();
    contextMenuGui.close();
    menuController.endGame();
  }
}
