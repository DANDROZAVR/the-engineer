package engineer.gui;

import javafx.scene.image.Image;

public interface TextureManager {
  void loadTexture(String name);

  Image getTexture(String name);
}
