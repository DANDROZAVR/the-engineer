package engineer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    private Stage stage;
    @Override
    public void start(Stage primaryStage) {
        //primaryStage.initStyle(StageStyle.UNDECORATED); uncomment if full screen
        stage = primaryStage;
        configStage();
        stage.show();
        Game game = new Game(primaryStage);
        game.run();
        System.out.println("started");
    }
    public void configStage() {
        stage.setTitle("The engineer");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("F11"));

    }
}
