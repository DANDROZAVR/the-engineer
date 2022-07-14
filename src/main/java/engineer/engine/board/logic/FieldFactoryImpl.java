package engineer.engine.board.logic;

import engineer.gui.TextureManager;

public class FieldFactoryImpl implements FieldFactory {
    private final TextureManager textureManager;

    public FieldFactoryImpl(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    @Override
    public Field produce(String background, boolean free) {
        Field newField = new Field(background, free);
        textureManager.loadTexture(background);
        return newField;
    }
}
