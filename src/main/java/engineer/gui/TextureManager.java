package engineer.gui;

import javafx.scene.image.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// TODO Implement this class
public class TextureManager {
    // Provides textures
    // Loading textures from files and freeing memory them on demand
    // It is part of a sketch so might be not required
    Map<String, Image> imageCacher;
    public TextureManager() {
        imageCacher = new HashMap<>();
    }
    public Image loadBackgroundImage(String description) {
        if (imageCacher.containsKey(description)) {
            return imageCacher.get(description);
        }
        // use cash with pre-loading instead)
        URL url = getClass().getClassLoader().getResource("images/" + description + ".png");
        Image image = new Image(String.valueOf(url));
        imageCacher.put(description, image);
        return image;
    }
}
