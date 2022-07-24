package engineer.engine.gamestate;

public class FieldFactoryImpl implements FieldFactory {

  @Override
  public Field produce(String background, boolean free) {
    return new Field(background, free);
  }
}
