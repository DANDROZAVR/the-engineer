package engineer.engine.gamestate;

public class FieldContentImpl implements FieldContent {
  String fileName;

  public FieldContentImpl(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public String getPicture() {
    return fileName;
  }
}
