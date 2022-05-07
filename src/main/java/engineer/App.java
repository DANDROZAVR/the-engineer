package engineer;
import engineer.Game;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    private Stage stage;
    private BooleanProperty fullBody;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.show();
        configStage();
        Game game = new Game();
        //game.run();
        System.out.println("started");
    }
    public void configStage() {
        stage.setFullScreen(true);;
        stage.setMaximized(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("F11"));
    }
}
