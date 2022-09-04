package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MobFactoryTest {
    @Mock Player player;
    @Mock List<Resource> resourceList;

    @Test
    public void testProduceMob() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5, 3, 2, resourceList);
        Mob mob = mobFactory.produce( "mobType1", 4, player);

        assertEquals("mobType1", mob.getType());
        assertEquals("texture1", mob.getTexture());
        assertEquals(4, mob.getMobsAmount());
        assertEquals(3, mob.getMobsAttack());
        assertEquals(2, mob.getMobsLife());
        assertEquals(player, mob.getOwner());
        assertEquals(resourceList, mob.getResToProduce());

        mob.reset();
        assertEquals(5, mob.getRemainingSteps());

        mob.reduceRemainingSteps(1);
        assertEquals(4, mob.getRemainingSteps());
    }

    @Test
    public void testAddMobs() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5, 3, 2, resourceList);
        Mob mob = mobFactory.produce( "mobType1", 4, null);

        mob.addMobs(2);
        assertEquals(6, mob.getMobsAmount());

        mob.reduceMobs(3);
        assertEquals(3, mob.getMobsAmount());
    }

    @Test
    public void testAttack() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5, 3, 2, resourceList);
        Mob mob = mobFactory.produce( "mobType1", 4, null);

        assertTrue(mob.canAttackInThisTurn());
        mob.makeAttack();
        assertFalse(mob.canAttackInThisTurn());
    }
}
