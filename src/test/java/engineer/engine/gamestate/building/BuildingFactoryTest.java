package engineer.engine.gamestate.building;

import engineer.engine.gamestate.resource.Resource;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingFactoryTest {
  @Test
  public void testProduceBuilding() {
    BuildingFactory buildingFactory = new BuildingFactory();
    List <Resource> list = Collections.emptyList();

    buildingFactory.addMobType("type", "texture", list);
    Building building = buildingFactory.produce("type");

    assertEquals("type", building.getType());
    assertEquals("texture", building.getTexture());
    assertEquals(list, building.getResToBuild());
  }
}
