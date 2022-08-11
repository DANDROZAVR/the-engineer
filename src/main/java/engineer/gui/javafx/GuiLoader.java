package engineer.gui.javafx;

import javafx.fxml.FXMLLoader;

import java.net.URL;

public class GuiLoader {
  public static <T> T loadGui(String path) {
    try {
      FXMLLoader loader = new FXMLLoader();
      URL url = GuiLoader.class.getResource(path);

      loader.setLocation(url);
      loader.load();

      return loader.getController();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
