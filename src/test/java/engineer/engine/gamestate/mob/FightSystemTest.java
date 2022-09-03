package engineer.engine.gamestate.mob;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class FightSystemTest {
    FightSystem fightSystem = new FightSystem();
    @Mock Mob mob1;
    @Mock Mob mob2;
    @Mock FightSystem.Observer observer;
    private AutoCloseable closeable;

    @Test
    void testMakeFight(){
        when(mob1.getMobsAmount()).thenReturn(45);
        when(mob2.getMobsAmount()).thenReturn(50);
        when(mob1.getMobsAttack()).thenReturn(4);
        when(mob2.getMobsAttack()).thenReturn(2);
        when(mob1.getMobsLife()).thenReturn(12);
        when(mob2.getMobsLife()).thenReturn(10);

        fightSystem.addObserver(observer);

        Pair<Integer, Integer> result = fightSystem.makeFight(mob1, mob2);
/*
        verify(observer).onFightStart(mob1, mob2);
        verify(observer).onFightTurn(540, 500);
        verify(observer).onFightTurn(390, 275);
        verify(observer).onFightTurn(306, 110);
        verify(observer).onFightTurn(273, -20);
*/
        assertEquals(result.getKey(), 28);
        assertEquals(result.getValue(), 0);

        fightSystem.removeObserver(observer);
    }

    @BeforeEach
    public void setUp(){
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }
}
