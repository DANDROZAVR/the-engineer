package engineer.engine.gamestate.mob;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;

public class FightSystem {
    public interface Observer {
        void onShowFight(Mob attacker, Mob defender, int finalLifeAttacker, int finalLifeDefender);
    }
    private final List<FightSystem.Observer> observerList = new LinkedList<>();

    public void addObserver(FightSystem.Observer observer) {
        observerList.add(observer);
    }

    public void removeObserver(FightSystem.Observer observer) {
        observerList.remove(observer);
    }

    public Pair<Integer, Integer> makeFight(Mob attacker, Mob defender) {
        int lifeAttacker = attacker.getMobsAmount() * attacker.getMobsLife();
        int lifeDefender = defender.getMobsAmount() * defender.getMobsLife();

        while (lifeAttacker > 0 && lifeDefender > 0) {
            int attackValue = (int)Math.ceil((double)lifeAttacker/attacker.getMobsLife()) * attacker.getMobsAttack();
            int defenceValue = (int)Math.ceil((double)lifeDefender/defender.getMobsLife()) * defender.getMobsAttack();
            lifeAttacker -= defenceValue;
            lifeDefender -= attackValue;
        }

        int survivedAttacker = max((int)Math.ceil((double)lifeAttacker/attacker.getMobsLife()), 0);
        int survivedDefender = max((int)Math.ceil((double)lifeDefender/defender.getMobsLife()), 0);
        observerList.forEach(o -> o.onShowFight(attacker, defender, survivedAttacker, survivedDefender));

        return new Pair<>(survivedAttacker, survivedDefender);
    }
}
