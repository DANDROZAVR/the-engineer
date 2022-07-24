package engineer.engine.exceptions;

public abstract class BoardException extends RuntimeException {
  public BoardException() {
    super();
  }

  public BoardException(Exception cause) {
    super(cause);
  }
}
