package engineer.engine.gamestate.turns;

import java.util.LinkedList;
import java.util.List;

public class TurnSystem {
  public interface Observer {
    default void onTurnChange() {}
  }
  private final List<TurnSystem.Observer> observerList = new LinkedList<>();

  public void addObserver(TurnSystem.Observer observer) {
    observerList.add(observer);
  }

  public void removeObserver(TurnSystem.Observer observer) {
    observerList.remove(observer);
  }
  private final List<Player> players;
  private int currentPlayer = -1;

  public TurnSystem(List<Player> players) {
    this.players = new LinkedList<>(players);
  }

  public void nextTurn() {
    currentPlayer = (currentPlayer + 1) % players.size();
    observerList.forEach(Observer::onTurnChange);
  }

  public Player getCurrentPlayer() {
    return players.get(currentPlayer);
  }
}
