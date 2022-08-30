package engineer.engine.gamestate.building;

import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class BuildingFactoryTest {
  @Test
  public void testProduceBuilding() {
    BuildingFactory buildingFactory = new BuildingFactory();
    List <Resource> list = Collections.emptyList();
    Player player = mock(Player.class);

    buildingFactory.addBuildingType("type", "texture", list, 1);
    Building building = buildingFactory.produce("type", player);

    assertEquals("type", building.getType());
    assertEquals("texture", building.getTexture());
    assertEquals(1, building.getLifeRemaining());
    assertEquals(1, building.getLevel());
    assertEquals(list, building.getResToBuild());
    assertEquals(player, building.getOwner());
  }
}
