package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.Mob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
  void testGetName() {
    Player player = new Player("name");
    assertEquals("name", player.getNickname());
  }

}
