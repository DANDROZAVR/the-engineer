package engineer.gui.javafx.game;

import engineer.engine.gamestate.board.Board;
import engineer.engine.gamestate.turns.TurnSystem;
import engineer.engine.presenters.game.EndGamePresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class EndGameGui implements EndGamePresenter.View {
  public interface EndGameCallback {
    void endGame(boolean endedNormally);
  }

  @FXML public VBox root;
  @FXML public Label descriptionEndGame;

  private Pane parent;
  private EndGameCallback endGameCallback;
  private EndGamePresenter presenter;

  public void setup(Pane parent, Board board, TurnSystem turnSystem, EndGameCallback endGameCallback) {
    this.parent = parent;
    this.endGameCallback = endGameCallback;
    this.presenter = new EndGamePresenter(board, turnSystem, this);
  }

  public void start() {
    presenter.start();
  }

  public void close() {
    presenter.close();
  }

  @Override
  public void showEndGame(String nickname) {
    descriptionEndGame.setText("Game over \n" + nickname + " is the winner!");
    parent.getChildren().add(root);
  }

  public void endGame() {
    endGameCallback.endGame(true);
  }
}
