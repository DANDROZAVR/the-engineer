package engineer.gui.javafx.game;

import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.presenters.game.FightPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class FightGui implements FightPresenter.View{

  @FXML public VBox root;
  @FXML public Label fightDescription;
  private Pane parent;
  private FightPresenter presenter;

  public void setup(Pane parent, FightSystem fightSystem) {
    this.parent = parent;
    this.presenter = new FightPresenter(fightSystem, this);
  }
  @Override
  public void showFight(String attacker, String defender, int numberAttacker, int numberDefender,
                        int survivedAttacker, int survivedDefender, String nicknameAttacker, String nicknameDefender) {
    fightDescription.setText("fight between: \n" + numberAttacker + " " + attacker + "(" + nicknameAttacker + ")"
            + " and " + numberDefender + " " + defender + "(" + nicknameDefender + ")"
            + "\nsurvived: "  + survivedAttacker + " " + attacker + "(" + nicknameAttacker + ")"
            + " and " + survivedDefender + " " + defender + "(" + nicknameDefender + ")");
    parent.getChildren().add(root);
  }

  public void resumeGame() {
    parent.getChildren().remove(root);
  }

  public void start() {
    presenter.start();
  }

  public void close() {
    presenter.close();
  }
}
