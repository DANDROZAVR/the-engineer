package engineer.gui.javafx;

import engineer.engine.board.logic.Board;
import engineer.engine.board.logic.BoardDescription;
import engineer.engine.board.logic.FieldFactoryImpl;
import engineer.engine.board.presenter.BoardPresenter;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static engineer.gui.javafx.GameGui.title;

public class Gui {
    private static Stage window;
    private static final int windowWidth = 1080;
    private static final int windowHeight = 720;
    private final Scene startingScene;
    private final GameGui gameGui;

    public Gui() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(5.);

        Image exitImg = new Image("file:src/main/resources/images/exit.png");
        ImageView exitImgView = new ImageView(exitImg);
        exitImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> window.close());

        Image startGameImg = new Image("file:src/main/resources/images/startGame.png");
        ImageView startGameImgView = new ImageView(startGameImg);
        startGameImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> startGame());

        vbox.getChildren().addAll(startGameImgView, exitImgView);

        StackPane root = new StackPane();
        Image backgroundImg = new Image("file:src/main/resources/images/startBackground.png");
        ImageView backgroundImgView = new ImageView(backgroundImg);
        backgroundImgView.setFitWidth(windowWidth);
        backgroundImgView.setFitHeight(windowHeight);

        root.getChildren().addAll(backgroundImgView, vbox);
        startingScene = new Scene(root, windowWidth, windowHeight);

        window = new Stage();
        gameGui = new GameGui(window, this::start);
    }

    public void start() {
        window.setTitle(title);
        window.setResizable(false);
        window.setScene(startingScene);
        window.show();
    }

    public void startGame() {
        Board board = new Board(new FieldFactoryImpl(), new BoardDescription() {
            @Override
            public int getRows() { return 40; }
            @Override
            public int getColumns() { return 50; }
            @Override
            public String getBackground(int row, int column) {
                return "tile";
            }
        });

        // Sample
        gameGui.start(() -> {
            BoardPresenter boardPresenter = new BoardPresenter(board, gameGui.getBoardGui());
            gameGui.getBoardGui().start(boardPresenter);
        });
    }
}
