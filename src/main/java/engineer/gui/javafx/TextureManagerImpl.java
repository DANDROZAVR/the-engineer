package engineer.gui.javafx;

import engineer.engine.board.exceptions.TextureNotFoundException;
import engineer.gui.TextureManager;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class TextureManagerImpl implements TextureManager {
    private final Map<String, Image> textureMap = new HashMap<>();

    public void loadTexture(String name) {
        if (textureMap.containsKey(name)) return;

        try {
            textureMap.put(name, new Image("file:src/main/resources/images/" + name + ".png"));
        } catch (IllegalArgumentException e) {
            throw new TextureNotFoundException(e);
        }
    }

    public Image getTexture(String name) {
        loadTexture(name);
        return textureMap.get(name);
    }
}
