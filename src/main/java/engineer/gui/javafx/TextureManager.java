package engineer.gui.javafx;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

public class TextureManager {
  private final Map<String, Image> textureMap = new HashMap<>();

  public void loadTexture(String name) {
    if (textureMap.containsKey(name)) return;
    textureMap.put(name, new Image("file:src/main/resources/images/" + name + ".png"));
  }

  public Image getTexture(String name) {
    loadTexture(name);
    return textureMap.get(name);
  }
}
