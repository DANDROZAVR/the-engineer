package engineer.gui.javafx;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Gui {
    private static final String title = "The Engineer";
    private static final int windowWidth = 1080;
    private static final int windowHeight = 720;

    private BoardGui boardGui;

    public void start(Runnable runnable) {
        Platform.startup(() -> {
            Stage window = new Stage();
            window.setTitle(title);
            window.setResizable(false);

            Canvas canvas = new Canvas(windowWidth, windowHeight);
            boardGui = new BoardGui(canvas.getGraphicsContext2D());

            Group root = new Group();
            root.getChildren().add(canvas);
            window.setScene(new Scene(root));
            window.show();
            window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> boardGui.close());

            window.getScene().setOnKeyPressed(e -> boardGui.getKeyHandler().handleKey(e.getCode(), true));
            window.getScene().setOnKeyReleased(e -> boardGui.getKeyHandler().handleKey(e.getCode(), false));

            runnable.run();
        });
    }

    public BoardGui getBoardGui() {
        return boardGui;
    }
}