package engineer.gui.javafx;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameGui {
    public static final String title = "The Engineer";
    private static final int windowWidth = 1080;
    private static final int windowHeight = 720;
    private final BoardGui boardGui;
    private final boolean[] pausing = new boolean[1];

    interface ExitGameCallback {
        void exit();
    }

    private final Scene gameScene;
    private final Stage window;
    private final Button button, button2;
    public GameGui(Stage window, ExitGameCallback exitGameCallback) {
        this.window = window;
        Canvas canvas = new Canvas(windowWidth, windowHeight);

        button = new Button("house");
        button.setFocusTraversable(false);
        button.setId("house");
        button.setLayoutX(20);
        button.setLayoutY(20);

        button2 = new Button("house2");
        button2.setFocusTraversable(false);
        button2.setId("house2");
        button2.setLayoutX(20);
        button2.setLayoutY(60);

        // TODO: change to TextureManager
        Image pauseImg = new Image("file:src/main/resources/images/pause.png");
        ImageView pauseImgView = new ImageView(pauseImg);

        AnchorPane root = new AnchorPane();

        Image pauseTextImg = new Image("file:src/main/resources/images/pauseText.png");
        ImageView pauseTextImgView = new ImageView(pauseTextImg);
        pauseTextImgView.setVisible(false);

        Image stopImg = new Image("file:src/main/resources/images/stop.png");
        ImageView stopImgView = new ImageView(stopImg);
        stopImgView.setVisible(false);

        // TODO: if we want to make windows resizable, think about binding the width and the height (just callbacks)
        AnchorPane.setTopAnchor(pauseTextImgView, canvas.getHeight() / 2);
        AnchorPane.setLeftAnchor(pauseTextImgView, canvas.getWidth() / 2 - pauseTextImg.getWidth() / 2);

        AnchorPane.setTopAnchor(pauseImgView, 5.);
        AnchorPane.setLeftAnchor(pauseImgView, canvas.getWidth() / 2 - pauseImg.getWidth() / 2);

        AnchorPane.setTopAnchor(stopImgView, canvas.getHeight() * 0.8 - stopImg.getHeight());
        AnchorPane.setLeftAnchor(stopImgView, canvas.getWidth() / 2 - stopImg.getWidth() / 2);

        root.getChildren().addAll(canvas, pauseTextImgView, pauseImgView, stopImgView, button, button2);
        gameScene = new Scene(root);

        boardGui = new BoardGui(canvas.getGraphicsContext2D());
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (!pausing[0]) {
                boardGui.getOnFieldClickHandler().handle(mouseEvent);
            }
            mouseEvent.consume();
        });
        button.setOnAction(boardGui.getButtonClickedHandler());
        button2.setOnAction(boardGui.getButtonClickedHandler());
        pauseImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            pausing[0] = !pausing[0];
            if (pausing[0]) {
                disableInterface(pauseTextImgView, stopImgView);
            } else {
                enableInterface(pauseTextImgView, stopImgView);
            }
            System.out.println("Pause pressed");
            mouseEvent.consume();
        });
        stopImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            pausing[0] = false;
            enableInterface(pauseTextImgView, stopImgView);
            exitGameCallback.exit();
        });
        window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> boardGui.close());
    }

    public void start(Runnable runnable) {
        window.setTitle(title);
        window.setResizable(false);
        window.setScene(gameScene);

        window.getScene().setOnMouseClicked(boardGui.getOnFieldClickHandler());
        window.getScene().setOnKeyPressed(e -> {
            if (!pausing[0])
                boardGui.getKeyHandler().handleKey(e.getCode(), true);
            e.consume();
        });
        window.getScene().setOnKeyReleased(e -> {
            if (!pausing[0])
                boardGui.getKeyHandler().handleKey(e.getCode(), false);
            e.consume();
        });

        window.show();
        runnable.run();
    }

    public BoardGui getBoardGui() {
        return boardGui;
    }

    private void disableInterface(ImageView pauseTextImgView, ImageView stopImgView) {
        button.setDisable(true);
        button2.setDisable(true);
        pauseTextImgView.setVisible(true);
        stopImgView.setVisible(true);
        //   As soon as new buttons will appear, add them here
        //   can be changed to List of Objects, in the case of a big number of buttons
    }

    private void enableInterface(ImageView pauseTextImgView, ImageView stopImgView) {
        button.setDisable(false);
        button2.setDisable(false);
        pauseTextImgView.setVisible(false);
        stopImgView.setVisible(false);
        // and here
    }
}