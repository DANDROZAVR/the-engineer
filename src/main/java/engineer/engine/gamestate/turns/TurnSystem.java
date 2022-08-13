package engineer.engine.gamestate.turns;

import engineer.engine.gamestate.mob.MobsController;

import java.util.LinkedList;
import java.util.List;

public class TurnSystem {
  private final List<Player> players;
  private int currentPlayer = -1;

  private final MobsController mobsController;

  public TurnSystem(List<Player> players, MobsController mobsController) {
    this.players = new LinkedList<>(players);
    this.mobsController = mobsController;
  }

  public void nextTurn() {
    currentPlayer = (currentPlayer + 1) % players.size();
    players.get(currentPlayer).onTurnStart();
    mobsController.onTurnStart();
  }

  public Player getCurrentPlayer() {
    return players.get(currentPlayer);
  }
}
