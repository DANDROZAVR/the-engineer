package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
  @Test
  public void testConstructor() {
    FieldFactory fieldFactory = new FieldFactory();
    BuildingFactory buildingFactory = new BuildingFactory();
    Field field = fieldFactory.produce(null, null, false);
    assertNull(field.getBackground());
    assertNull(field.getBuilding());
    assertFalse(field.isFree());

    Building building = buildingFactory.produce("building name");
    field = fieldFactory.produce("Background name", building, true);

    assertEquals("Background name", field.getBackground());
    assertEquals(building, field.getBuilding());
    assertTrue(field.isFree());
  }
}
