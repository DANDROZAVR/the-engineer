package engineer.engine.board.logic;

import engineer.engine.board.exceptions.TextureNotKnownException;

import java.util.HashMap;
import java.util.Map;

public class FieldFactoryImpl implements FieldFactory {
    //probably some file with setup needed
    Map<String, Integer> NumberOfMoves = new HashMap<>(){{
        put("tile", 1);
        put("wood", 2);
    }};

    Map<String, Boolean> BuildingsEnabled = new HashMap<>(){{
        put("tile", true);
        put("wood", false);
    }};
    @Override
    public Field produce(String background) {
        Field newField = new Field(background);
        if(!NumberOfMoves.containsKey(background) || !BuildingsEnabled.containsKey(background)){
            throw new TextureNotKnownException();
        }
        newField.setBuildingEnabled(BuildingsEnabled.get(background));
        newField.setNumberOfMovesNeeded(NumberOfMoves.get(background));
        return newField;
    }
}
