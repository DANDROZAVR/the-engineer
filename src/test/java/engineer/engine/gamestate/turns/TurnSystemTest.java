package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.MobsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnSystemTest {

  List<Player> players = new LinkedList<>();
  @Mock Player player1;
  @Mock Player player2;
  @Mock MobsController mobsController;

  @BeforeEach
  public void setup() {
    player1 = mock(Player.class);
    player2 = mock(Player.class);
    mobsController = mock(MobsController.class);

    players.add(player1);
    players.add(player2);
  }

  @Test
  void nextTurn() {
    TurnSystem turnSystem = new TurnSystem(players, mobsController);
    turnSystem.nextTurn();
    verify(player1).onTurnStart();
    verify(mobsController).onTurnStart();
    verify(player2, never()).onTurnStart();

    turnSystem.nextTurn();
    verify(player2).onTurnStart();
    verifyNoMoreInteractions(player1);

    turnSystem.nextTurn();
    verify(player1, times(2)).onTurnStart();
    verifyNoMoreInteractions(player2);

    verify(mobsController, times(3)).onTurnStart();
  }

  @Test
  void getCurrentPlayer() {
    TurnSystem turnSystem = new TurnSystem(players, mobsController);

    turnSystem.nextTurn();
    assertEquals(player1, turnSystem.getCurrentPlayer());

    turnSystem.nextTurn();
    assertEquals(player2, turnSystem.getCurrentPlayer());

    turnSystem.nextTurn();
    assertEquals(player1, turnSystem.getCurrentPlayer());
  }
}
