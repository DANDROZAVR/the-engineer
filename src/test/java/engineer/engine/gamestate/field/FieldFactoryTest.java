package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldFactoryTest {
  @Test
  public void testProduceField() {
    FieldFactory fieldFactory = new FieldFactory();
    BuildingFactory buildingFactory = new BuildingFactory();
    Building building = buildingFactory.produce("building");
    Field field = fieldFactory.produce("picture", building, true);

    assertEquals(field.getBackground(), "picture");
    assertEquals(field.getBuilding(), building);
    assertTrue(field.isFree());
  }
}
