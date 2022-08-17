package engineer.engine.gamestate.turns;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TurnSystemTest {
  AutoCloseable closeable;

  private List<Player> players;
  @Mock private Player player1;
  @Mock private Player player2;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    players = List.of(player1, player2);
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void nextTurn() {
    TurnSystem turnSystem = new TurnSystem(players);
    turnSystem.nextTurn();
    verify(player1).onTurnStart();
    verify(player2, never()).onTurnStart();

    turnSystem.nextTurn();
    verify(player2).onTurnStart();
    verifyNoMoreInteractions(player1);

    turnSystem.nextTurn();
    verify(player1, times(2)).onTurnStart();
    verifyNoMoreInteractions(player2);
  }

  @Test
  void getCurrentPlayer() {
    TurnSystem turnSystem = new TurnSystem(players);

    turnSystem.nextTurn();
    assertEquals(player1, turnSystem.getCurrentPlayer());

    turnSystem.nextTurn();
    assertEquals(player2, turnSystem.getCurrentPlayer());

    turnSystem.nextTurn();
    assertEquals(player1, turnSystem.getCurrentPlayer());
  }
}
