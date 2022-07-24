package engineer;

import engineer.gui.javafx.Gui;
import javafx.application.Platform;

public class App {
  public static void main(String[] args) {
    Platform.startup(() -> new Gui().start());
  }
}
