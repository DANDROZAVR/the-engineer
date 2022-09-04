package engineer.engine.presenters.game;

import engineer.engine.gamestate.mob.FightSystem;
import engineer.engine.gamestate.mob.Mob;

public class FightPresenter {
  public interface View {
    void showFight(String attacker, String defender, int numberAttacker, int numberDefender, int survivedAttacker, int survivedDefender, String nicknameAttacker, String nicknameDefender);
  }

  private final FightSystem fightSystem;
  private final View view;

  public FightPresenter(FightSystem fightSystem, View view) {
    this.fightSystem = fightSystem;
    this.view = view;
  }

  public final FightSystem.Observer fightSystemObserver = new FightSystem.Observer() {
    @Override
    public void onShowFight(Mob attacker, Mob defender, int survivedAttacker, int survivedDefender) {
      view.showFight(attacker.getType(), defender.getType(), attacker.getMobsAmount(), defender.getMobsAmount(),
              survivedAttacker, survivedDefender, attacker.getOwner().getNickname(), defender.getOwner().getNickname());
    }
  };

  public void start() {
    fightSystem.addObserver(fightSystemObserver);
  }

  public void close() {
    fightSystem.removeObserver(fightSystemObserver);
  }
}
