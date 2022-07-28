package engineer.engine.gamestate.building;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingFactoryTest {
  @Test
  public void testProduceBuilding() {
    BuildingFactory buildingFactory = new BuildingFactory();
    Building building = buildingFactory.produce("picture");
    assertEquals(building.getPicture(), "picture");
  }
}
