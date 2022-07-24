package engineer.gui.exceptions;

public class TextureNotFoundException extends RuntimeException {
  public TextureNotFoundException(Exception e) {
    super(e);
  }
}
