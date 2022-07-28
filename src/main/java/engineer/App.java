package engineer;

import engineer.gui.javafx.menu.MenuGui;
import javafx.application.Platform;
import javafx.stage.Stage;

public class App {
  public static void main(String[] args) {
    Platform.startup(() -> MenuGui.start(new Stage()));
  }
}
