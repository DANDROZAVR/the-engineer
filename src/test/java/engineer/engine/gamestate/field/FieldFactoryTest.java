package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.building.BuildingFactory;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.mob.MobFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FieldFactoryTest {
  @Test
  public void testProduceField() {
    FieldFactory fieldFactory = new FieldFactory();
    BuildingFactory buildingFactory = new BuildingFactory();
    MobFactory mobFactory = new MobFactory();
    Building building = buildingFactory.produce("building");
    Mob mob = mobFactory.produce("mob name", 1);
    Field field = fieldFactory.produce("picture", building, mob, true);

    assertEquals(field.getBackground(), "picture");
    assertEquals(field.getBuilding(), building);
    assertEquals(field.getMob(), mob);
    assertTrue(field.isFree());
  }
}
