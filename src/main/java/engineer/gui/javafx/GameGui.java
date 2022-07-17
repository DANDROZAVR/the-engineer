package engineer.gui.javafx;

import engineer.gui.TextureManager;
import javafx.event.EventType;
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
    // TODO: remove these two constants
    private static final int windowWidth = 1080;
    private static final int windowHeight = 720;
    private final BoardGui boardGui;
    private boolean pausing;

    interface ExitGameCallback {
        void exit();
    }

    private final Scene gameScene;
    private final Stage window;
    private final Button button, button2;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final TextureManager textureManager;

    public GameGui(Stage window, TextureManager textureManager, ExitGameCallback exitGameCallback) {
        this.window = window;
        this.textureManager = textureManager;

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

        Image pauseImg = textureManager.getTexture("pause");
        ImageView pauseImgView = new ImageView(pauseImg);

        AnchorPane root = new AnchorPane();

        Image pauseTextImg = textureManager.getTexture("pauseText");
        ImageView pauseTextImgView = new ImageView(pauseTextImg);
        pauseTextImgView.setVisible(false);

        Image stopImg = textureManager.getTexture("stop");
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

        boardGui = new BoardGui(canvas.getGraphicsContext2D(), textureManager);

        addMouseHandler(root, MouseEvent.MOUSE_CLICKED);
        addMouseHandler(root, MouseEvent.MOUSE_DRAGGED);
        //addMouseHandler(root, MouseEvent.MOUSE_ENTERED);
        //addMouseHandler(root, MouseEvent.MOUSE_EXITED);
        addMouseHandler(root, MouseEvent.MOUSE_PRESSED);
        addMouseHandler(root, MouseEvent.MOUSE_RELEASED);

        button.setOnAction(e -> boardGui.onButtonClicked(((Button) e.getTarget()).getId()));
        button2.setOnAction(e -> boardGui.onButtonClicked(((Button) e.getTarget()).getId()));
        pauseImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            pausing = !pausing;
            if (pausing) {
                disableInterface(pauseTextImgView, stopImgView);
            } else {
                enableInterface(pauseTextImgView, stopImgView);
            }
            mouseEvent.consume();
        });
        stopImgView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            pausing = false;
            enableInterface(pauseTextImgView, stopImgView);
            exitGameCallback.exit();
        });
        window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> boardGui.close());
    }

    public void start(Runnable runnable) {
        window.setTitle(title);
        window.setResizable(false);
        window.setScene(gameScene);

        window.getScene().setOnKeyPressed(e -> {
            if (!pausing)
                boardGui.getKeyHandler().handleKey(e.getCode(), true);
            e.consume();
        });
        window.getScene().setOnKeyReleased(e -> {
            if (!pausing)
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

    private void addMouseHandler(AnchorPane root, EventType<MouseEvent> eventType) {
        root.addEventHandler(eventType, e -> {
            if (!pausing)
                boardGui.onMouseEvent(eventType, e);
            e.consume();
        });
    }
}