package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.Mob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {
  private AutoCloseable closeable;

  @Mock private Mob mob;
  @Mock private Mob mob2;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void basicMobs() {
    Player player = new Player();
    player.addMob(mob);

    assertTrue(player.isMobOwner(mob));
    assertFalse(player.isMobOwner(mob2));

    player.onTurnStart();
    verify(mob).reset();

    player.removeMob(mob);
    assertFalse(player.isMobOwner(mob));

    player.onTurnStart();
    verifyNoMoreInteractions(mob);
  }

}
