package engineer.engine.gamestate.building;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingTest {
  @Test
  public void testLife() {
    BuildingFactory buildingFactory = new BuildingFactory();
    buildingFactory.addBuildingType("type", "texture", null, null, null,10, null);
    Building building = buildingFactory.produce("type", null);

    building.reduceLifeRemaining(3);
    assertEquals(7, building.getLifeRemaining());
  }

  @Test
  public void testLevelUpgrade() {
    BuildingFactory buildingFactory = new BuildingFactory();
    buildingFactory.addBuildingType("type", "texture", null, null, null,10, null);
    Building building = buildingFactory.produce("type", null);

    building.upgrade();
    assertEquals(2, building.getLevel());

    for (int i = 0; i < 10; ++i)
      building.upgrade();
    assertEquals(12, building.getLevel());
  }
}
