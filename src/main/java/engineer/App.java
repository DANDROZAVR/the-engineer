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
        stage = primaryStage;
        configStage();
        stage.show();
        Game game = new Game(primaryStage);
        game.run();
        System.out.println("started");
    }
    private void configStage() {
        stage.setTitle("The engineer");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(1000);
        stage.setHeight(700);
    }
}
