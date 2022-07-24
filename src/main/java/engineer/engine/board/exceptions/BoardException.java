package engineer.engine.board.exceptions;

public class BoardException extends RuntimeException {
  public BoardException() {
    super();
  }

  public BoardException(Exception cause) {
    super(cause);
  }
}
