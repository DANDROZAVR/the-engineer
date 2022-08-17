package engineer.engine.gamestate.mob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MobFactoryTest {
    @Test
    public void testProduceMob() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5);
        Mob mob = mobFactory.produce( "mobType1", 4);

        assertEquals("mobType1", mob.getType());
        assertEquals("texture1", mob.getTexture());
        assertEquals(4, mob.getMobsAmount());

        mob.reset();
        assertEquals(5, mob.getRemainingSteps());

        mob.reduceRemainingSteps(1);
        assertEquals(4, mob.getRemainingSteps());
    }

    @Test
    public void testAddMobs() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5);
        Mob mob = mobFactory.produce( "mobType1", 4);

        mob.addMobs(-2);
        assertEquals(2, mob.getMobsAmount());
    }
}
