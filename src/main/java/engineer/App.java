package engineer;

import engineer.gui.javafx.menu.MenuGui;
import javafx.application.Platform;

public class App {
  public static void main(String[] args) {
    Platform.startup(() -> new MenuGui().start());
  }
}
