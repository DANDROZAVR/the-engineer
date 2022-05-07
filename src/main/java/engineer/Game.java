package engineer;

import engineer.engine.board.exceptions.InvalidBoardDescriptionException;
import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.BoardDescription;
import engineer.engine.board.logic.FieldFactoryImpl;
import engineer.engine.board.presenter.BoardPresenter;
import engineer.gui.BoardGUI;
import engineer.gui.GameGUI;
import engineer.gui.TextureManager;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Game extends App {
    // Start game
    // Creates Scene, GameGUI, Board
    Scene scene;
    Board board = null;
    GameGUI gameGUI = null;
    BoardGUI boardGUI = null;
    BoardPresenter boardPresenter;
    TextureManager textureManager;
    final static int defWidth = 60;
    final static int defHeight = 60;
    final static int fieldWidth = 30, fieldHeight = 30;
    final static int maxFieldWidth = 50, maxFieldHeight = 50;
    final static int paddingWidth = 15, paddingHeight = 15;
    public Game(Stage mainStage) {
        this(mainStage, defWidth, defHeight);
    }

    public Game(Stage mainStage, int width, int height) {
        //...
        int widthSz = maxFieldWidth * width + 2 * paddingWidth;
        int heightSz = maxFieldHeight * height + 2 * paddingHeight;
        Canvas canvas = new Canvas(widthSz,  heightSz);
        Pane pane = new Pane(canvas);
        pane.setMinSize(widthSz , heightSz);
        pane.setPrefSize(widthSz, heightSz);
        pane.setMaxSize(widthSz, heightSz);
        scene = new Scene(new BorderPane(pane), mainStage.getWidth(), mainStage.getHeight());
        mainStage.setScene(scene);
        createTextureManager();
        createBoard(width, height);
        createBoardPresenter();
    }
    public void run() {
        createGameGUI();
        createBoardGUI();
        boardPresenter.start();
    }
    private void createTextureManager() {
        this.textureManager = new TextureManager();
    }
    private void createBoard(int width, int height) {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.WHITE);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        try {
            board = new Board(new FieldFactoryImpl(), new BoardDescription() {
                @Override
                public int getWidth() {
                    return width;
                }

                @Override
                public int getHeight() {
                    return height;
                }

                @Override
                public String getBackground(int row, int column) {
                    return colors.get((row + column) % colors.size()).toString();
                }
            });
        } catch (InvalidBoardDescriptionException e) {
            e.printStackTrace();
            exit(1);
        }
    }
    private void createBoardGUI() {
        boardGUI = new BoardGUI(scene, board, textureManager, fieldWidth, fieldHeight, paddingWidth, paddingHeight);
    }
    private void createBoardPresenter() {
        assert board != null;
        boardPresenter = new BoardPresenter(board, (row, column) -> {
            if (boardGUI != null) {
                boardGUI.onFieldChange(row, column); // correct use?
            }
        });
    }
    private void createGameGUI() {
        assert board != null;
        gameGUI = new GameGUI(scene, boardGUI, new TextureManager());
    }


}
