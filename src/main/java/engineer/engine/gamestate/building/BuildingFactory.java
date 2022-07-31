package engineer.engine.gamestate.building;

public class BuildingFactory {
  private record BuildingImpl(String picture) implements Building {
    @Override
    public String getPicture() {
      return picture;
    }
  }

  public Building produce(String picture) {
    return new BuildingImpl(picture);
  }
}
