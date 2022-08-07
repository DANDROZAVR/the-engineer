package engineer.engine.gamestate.mob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MobFactoryTest {
    @Test
    public void testProduceMob() {
        MobFactory mobFactory = new MobFactory();
        mobFactory.addMobType("mobType1", "texture1", 12);
        Mob mob = mobFactory.produce("mobType1", 4);

        assertEquals(mob.getType(), "mobType1");
        assertEquals(mob.getTexture(), "texture1");
        assertEquals(mob.getRange(), 12);
        assertEquals(mob.getNumber(), 4);
    }
}
