package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.mob.Mob;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class FieldFactoryTest {
  @Test
  public void testProduceField() {
    FieldFactory fieldFactory = new FieldFactory();
    Building building = mock(Building.class);
    Mob mob = mock(Mob.class);
    Field field = fieldFactory.produce("picture", building, mob);

    assertEquals(field.getBackground(), "picture");
    assertEquals(field.getBuilding(), building);
    assertEquals(field.getMob(), mob);
  }
}
