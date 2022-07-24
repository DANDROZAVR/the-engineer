package engineer.engine.exceptions;

public class IndexOutOfBoardException extends BoardException {
  public IndexOutOfBoardException(Exception cause) {
    super(cause);
  }
}
