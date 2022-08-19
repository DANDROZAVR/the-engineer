package engineer.engine.gamestate.mob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MobFactoryTest {
    @Test
    public void testProduceMob() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5, 3, 2);
        Mob mob = mobFactory.produce( "mobType1", 4, null);

        assertEquals("mobType1", mob.getType());
        assertEquals("texture1", mob.getTexture());
        assertEquals(4, mob.getMobsAmount());
        assertEquals(3, mob.getMobsAttack());
        assertEquals(2, mob.getMobsLife());

        mob.reset();
        assertEquals(5, mob.getRemainingSteps());

        mob.reduceRemainingSteps(1);
        assertEquals(4, mob.getRemainingSteps());
    }

    @Test
    public void testAddMobs() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 5, 3, 2);
        Mob mob = mobFactory.produce( "mobType1", 4, null);

        mob.addMobs(-2);
        assertEquals(2, mob.getMobsAmount());
    }
}
