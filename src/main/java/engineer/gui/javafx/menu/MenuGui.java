package engineer.gui.javafx.menu;

import engineer.gui.javafx.game.GameGui;
import engineer.gui.javafx.GuiLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuGui {
  private static final String TITLE = "The Engineer";

  public static void start(Stage window) {
    MenuGui menuGui = GuiLoader.loadGui("/fxml/menu.fxml");
    menuGui.setup(window);
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
