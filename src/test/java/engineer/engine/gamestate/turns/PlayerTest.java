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

  @Mock Mob mob;
  @Mock Mob mob2;
  private AutoCloseable closeable;

  @BeforeEach
  public void setup() {
    closeable = MockitoAnnotations.openMocks(this);
    @SuppressWarnings("unused")
    AutoCloseable closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void afterTesting() {
    try {
      closeable.close();
    } catch (Exception ignored) {}
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
