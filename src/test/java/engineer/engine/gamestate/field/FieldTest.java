package engineer.engine.gamestate.field;

import engineer.engine.gamestate.building.Building;
import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.turns.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class FieldTest {
  @Test
  public void testConstructor() {
    FieldFactory fieldFactory = new FieldFactory();
    Field field = fieldFactory.produce(null, null, null, false);
    Mob mob = mock(Mob.class);
    Building building = mock(Building.class);

    assertNull(field.getBackground());
    assertNull(field.getBuilding());
    assertNull(field.getMob());
    assertFalse(field.isFree());

    field = fieldFactory.produce("Background name", building, mob, true);

    assertEquals("Background name", field.getBackground());
    assertEquals(building, field.getBuilding());
    assertEquals(mob, field.getMob());
    assertTrue(field.isFree());
  }

  @Test
  public void testOwner() {
    FieldFactory fieldFactory = new FieldFactory();
    Player player = mock(Player.class);
    Building building = mock(Building.class);
    Mob mob = mock(Mob.class);

    Field field1 = fieldFactory.produce("backgroud", null, null, true);
    Field field2 = fieldFactory.produce("backgroud", building, null, true);
    Field field3 = fieldFactory.produce("backgroud", null, mob, true);

    doReturn(player).when(mob).getOwner();
    doReturn(player).when(building).getOwner();

    assertNull(field1.getOwner());
    assertEquals(player, field2.getOwner());
    assertEquals(player, field3.getOwner());
  }
}
