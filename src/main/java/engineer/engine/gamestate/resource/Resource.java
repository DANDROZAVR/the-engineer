package engineer.engine.gamestate.resource;

public interface Resource {
  String getType();
  String getTexture();
  int getResAmount();
  Resource addResAmount(int amount);
  boolean equals(Resource resource);
}
