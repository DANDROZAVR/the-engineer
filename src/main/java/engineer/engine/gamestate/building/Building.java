package engineer.engine.gamestate.building;

import engineer.engine.gamestate.resource.Resource;

import java.util.List;

public interface Building {
  String getTexture();
  String getType();
  List<Resource> getResToBuild();
}
