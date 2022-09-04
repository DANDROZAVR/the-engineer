package engineer.engine.presenters.game;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.turns.TurnSystem;

public class EndGamePresenter {
  public interface View {
    void showEndGame(String nickname);
  }

  private final Board board;
  private final View view;
  private final TurnSystem turnSystem;

  public EndGamePresenter(Board board, TurnSystem turnSystem, View view) {
    this.board = board;
    this.view = view;
    this.turnSystem = turnSystem;
  }

  public final Board.Observer boardObserver = new Board.Observer() {
    @Override
    public void onGameEnded() {
      view.showEndGame(turnSystem.getCurrentPlayer().getNickname());
    }
  };

  public void start() {
    board.addObserver(boardObserver);
  }

  public void close() {
    board.removeObserver(boardObserver);
  }
}
