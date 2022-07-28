package engineer.gui.javafx.menu;

import engineer.gui.javafx.game.GameGui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MenuGui {
  private static final String TITLE = "The Engineer";

  public static void start(Stage window) {
    try {
      FXMLLoader loader = new FXMLLoader();
      URL path = MenuGui.class.getResource("/fxml/menu.fxml");
      loader.setLocation(path);
      loader.load();

      MenuGui menuGui = loader.getController();
      menuGui.setup(window);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Stage window;

  @FXML private Parent root;
  private Scene scene;

  private void setup(Stage window) {
    this.window = window;
    scene = new Scene(root);

    showMenu();
  }

  private void showMenu() {
    window.setTitle(TITLE);
    window.setResizable(false);
    window.setScene(scene);
    window.show();
  }

  public void startGame() {
    GameGui.start(window, this::showMenu);
  }

  public void exitGame() {
    window.close();
  }
}
