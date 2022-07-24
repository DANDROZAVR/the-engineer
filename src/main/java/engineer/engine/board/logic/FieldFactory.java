package engineer.engine.board.logic;

public interface FieldFactory {
  Field produce(String background, boolean free);
}
