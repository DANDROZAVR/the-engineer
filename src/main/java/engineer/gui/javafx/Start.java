package engineer.gui.javafx;

import engineer.gui.javafx.menu.MenuGui;
import java.net.URL;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Start {
  public static void start() {
    Platform.startup(
        () -> {
          try {
            FXMLLoader loader = new FXMLLoader();
            URL path = Start.class.getResource("/fxml/menu.fxml");
            loader.setLocation(path);
            loader.load();

            MenuGui menuGui = loader.getController();
            menuGui.start(new Stage());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }
}
