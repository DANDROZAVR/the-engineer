package engineer.engine.board.logic;

public class FieldFactoryImpl implements FieldFactory {
    @Override
    public Field produce(String background) { return new Field(background); }
}
