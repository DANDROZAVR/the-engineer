package engineer.engine.gamestate.building;

import engineer.engine.gamestate.mob.Mob;
import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BuildingFactoryTest {
  @Test
  public void testProduceBuilding() {
    BuildingFactory buildingFactory = new BuildingFactory();
    List <Resource> listBuild = mock(List.class);
    List <Resource> listUpgrade = mock(List.class);
    List <Resource> listProduce = new ArrayList<>();
    Resource res = mock(Resource.class);
    listProduce.add(res);
    Mob mob = mock(Mob.class);
    Player player = mock(Player.class);

    buildingFactory.addBuildingType("type", "texture", listBuild, listProduce, listUpgrade, 10, mob);
    Building building = buildingFactory.produce("type", player);

    assertEquals("type", building.getType());
    assertEquals("texture", building.getTexture());
    assertEquals(listBuild, building.getResToBuild());
    assertEquals(listUpgrade, building.getResToUpgrade());
    assertEquals(mob, building.getTypeOfProducedMob());
    assertEquals(player, building.getOwner());
    assertEquals(1, building.getLevel());
    assertEquals(listProduce, building.getResProduced());

    building.upgrade();
    assertEquals(2, building.getLevel());
    assertEquals(10, building.getLifeRemaining());
    building.reduceLifeRemaining(2);
    assertEquals(8, building.getLifeRemaining());

    building.produceOnEndOfTurn();
    verify(player).addResource(res);
  }
}
