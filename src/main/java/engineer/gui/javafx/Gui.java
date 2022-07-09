package engineer.gui.javafx;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
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

            Button button = new Button("house");
            button.setFocusTraversable(false);
            button.setId("house");
            button.setLayoutX(20);
            button.setLayoutY(20);

            Button button2 = new Button("house2");
            button2.setFocusTraversable(false);
            button2.setId("house2");
            button2.setLayoutX(20);
            button2.setLayoutY(60);

            AnchorPane root = new AnchorPane();
            root.getChildren().addAll(canvas, button, button2);

            window.setScene(new Scene(root));
            window.show();

            boardGui = new BoardGui(canvas.getGraphicsContext2D());
            window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> boardGui.close());
            window.getScene().setOnKeyPressed(e -> boardGui.getKeyHandler().handleKey(e.getCode(), true));
            window.getScene().setOnKeyReleased(e -> boardGui.getKeyHandler().handleKey(e.getCode(), false));

            window.getScene().setOnMouseClicked(boardGui.getOnFieldClickHandler());

            button.setOnAction(boardGui.getButtonClickedHandler());
            button2.setOnAction(boardGui.getButtonClickedHandler());

            runnable.run();
        });
    }

    public BoardGui getBoardGui() {
        return boardGui;
    }
}