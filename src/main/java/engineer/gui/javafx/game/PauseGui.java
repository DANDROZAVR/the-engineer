package engineer.gui.javafx.game;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PauseGui {
  public interface EndGameCallback {
    void endGame(boolean endedNormally);
  }

  @FXML public VBox root;

  private Pane parent;
  private EndGameCallback endGameCallback;

  public void setup(Pane parent, EndGameCallback endGameCallback) {
    this.parent = parent;
    this.endGameCallback = endGameCallback;
  }

  public void pauseGame() {
    parent.getChildren().add(root);
  }

  public void resumeGame() {
    parent.getChildren().remove(root);
  }

  public void endGame() {
    endGameCallback.endGame(false);
  }
}
