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
    private Scene scene;
    private Board board = null;
    private GameGUI gameGUI = null;
    private BoardGUI boardGUI = null;
    private BoardPresenter boardPresenter;
    private TextureManager textureManager;
    private final static int defWidth = 100;
    private final static int defHeight = 100;
    private double fieldWidth = 30, fieldHeight = 30;
    private final static int maxFieldWidth = 50, maxFieldHeight = 50, minFieldWidth = 5, minFieldHeight = 5;
    private final static int paddingWidth = 15, paddingHeight = 15;
    final static double speedChangingSize = 1.1;
    public Game(Stage mainStage) {
        this(mainStage, defWidth, defHeight);
    }

    public Game(Stage mainStage, int width, int height) {
        createScene(mainStage, width, height);
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
    private void createScene(Stage mainStage, int width, int height) {
        int widthSz = maxFieldWidth * width + 2 * paddingWidth;
        int heightSz = maxFieldHeight * height + 2 * paddingHeight;
        Canvas canvas = new Canvas(widthSz,  heightSz);
        Pane pane = new Pane(canvas);
        pane.setMinSize(widthSz , heightSz);
        pane.setPrefSize(widthSz, heightSz);
        pane.setMaxSize(widthSz, heightSz);
        scene = new Scene(new BorderPane(pane), mainStage.getWidth(), mainStage.getHeight());
    }

    private void createTextureManager() {
        this.textureManager = new TextureManager();
    }
    private void createBoard(int width, int height) {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.GRAY); // TEMP
        colors.add(Color.WHITE);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        try {
            board = new Board(new FieldFactoryImpl(), new BoardDescription() {
                @Override
                public int getRows() {
                    return width;
                }

                @Override
                public int getColumns() {
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
        BoardGUI.FieldSizeNotifier notifier = new BoardGUI.FieldSizeNotifier() {
            @Override
            public void increaseRequest() {
                increaseFieldsSize();
            }
            @Override
            public void decreaseRequest() {
                decreaseFieldsSize();
            }
        };
        boardGUI = new BoardGUI(scene, textureManager, boardPresenter, notifier, fieldWidth, fieldHeight, paddingWidth, paddingHeight);
    }
    private void createBoardPresenter() {
        if (board == null)
            throw new RuntimeException("board is null for presenter");
        boardPresenter = new BoardPresenter(board, this::onFieldChange);
    }
    private void onFieldChange(int row, int col) {
        boardGUI.onFieldChange(row, col);
    }
    private void createGameGUI() {
        if (board == null)
            throw new RuntimeException("board is null for GameGui");
        gameGUI = new GameGUI(scene, boardGUI, new TextureManager());
    }
    private void increaseFieldsSize() {
        if (fieldWidth * speedChangingSize < maxFieldWidth && fieldHeight * speedChangingSize < maxFieldHeight) {
            fieldWidth *= speedChangingSize;
            fieldHeight *= speedChangingSize;
            boardGUI.setFieldsSize(fieldWidth, fieldHeight);
        }
    }
    private void decreaseFieldsSize() {
        if (fieldWidth / speedChangingSize > minFieldWidth && fieldHeight * speedChangingSize > minFieldHeight) {
            fieldWidth /= speedChangingSize;
            fieldHeight /= speedChangingSize;
            boardGUI.setFieldsSize(fieldWidth, fieldHeight);
        }
    }
}
