package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {
  @Test
  public void testConstructor() {
    FieldFactory fieldFactory = new FieldFactory();
    BuildingFactory buildingFactory = new BuildingFactory();
    MobFactory mobFactory = new MobFactory();
    Field field = fieldFactory.produce(null, null, null, false);

    mobFactory.addMobType("mob name", "texture", 1, 1, 1);

    assertNull(field.getBackground());
    assertNull(field.getBuilding());
    assertNull(field.getMob());
    assertFalse(field.isFree());

    Building building = buildingFactory.produce("building name");
    Mob mob = mobFactory.produce("mob name", 1, null);
    field = fieldFactory.produce("Background name", building, mob, true);

    assertEquals("Background name", field.getBackground());
    assertEquals(building, field.getBuilding());
    assertEquals(mob, field.getMob());
    assertTrue(field.isFree());
  }
}
