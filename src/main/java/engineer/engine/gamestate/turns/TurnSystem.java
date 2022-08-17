package engineer.engine.gamestate.turns;

import java.util.LinkedList;
import java.util.List;

public class TurnSystem {
  private final List<Player> players;
  private int currentPlayer = -1;

  public TurnSystem(List<Player> players) {
    this.players = new LinkedList<>(players);
  }

  public void nextTurn() {
    currentPlayer = (currentPlayer + 1) % players.size();
    players.get(currentPlayer).onTurnStart();
  }

  public Player getCurrentPlayer() {
    return players.get(currentPlayer);
  }
}
