package engineer.engine.gamestate;

public interface FieldFactory {
  Field produce(String background, boolean free);
}
