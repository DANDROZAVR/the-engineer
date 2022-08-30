package engineer.engine.gamestate.mob;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;

public class FightSystem {
    public interface Observer {
        void onFightStart(Mob attacker, Mob defender);
        void onFightTurn(Integer attack, Integer defence);
    }
    private final List<FightSystem.Observer> observerList = new LinkedList<>();

    public void addObserver(FightSystem.Observer observer) {
        observerList.add(observer);
    }

    public void removeObserver(FightSystem.Observer observer) {
        observerList.remove(observer);
    }

    private final Random rng;

    public FightSystem(Random rng){
        this.rng = rng;
    }

    public Pair<Integer, Integer> makeFight(Mob attacker, Mob defender) {
        observerList.forEach(o -> o.onFightStart(attacker, defender));

        int lifeAttacker = attacker.getMobsAmount() * attacker.getMobsLife();
        int lifeDefender = defender.getMobsAmount() * defender.getMobsLife();

        while(lifeAttacker > 0 && lifeDefender > 0)                     {
            int attackValue = 0;
            for(int i = 0; i < Math.ceil((double)lifeAttacker/attacker.getMobsLife()); i++)
                attackValue += (int)rng.nextGaussian() + attacker.getMobsAttack();

            int defenceValue = 0;
            for(int i = 0; i < Math.ceil((double)lifeDefender/defender.getMobsLife()); i++)
                defenceValue += (int)rng.nextGaussian() + defender.getMobsAttack();

            int finalLifeAttacker = lifeAttacker;
            int finalLifeDefender = lifeDefender;

            observerList.forEach(o -> o.onFightTurn(finalLifeAttacker, finalLifeDefender));

            lifeAttacker -= defenceValue;
            lifeDefender -= attackValue;
        }

        int finalLifeDefender1 = lifeDefender;
        int finalLifeAttacker1 = lifeAttacker;

        observerList.forEach(o -> o.onFightTurn(finalLifeAttacker1, finalLifeDefender1));

        return new Pair<>(max((int)Math.ceil((double)lifeAttacker/attacker.getMobsLife()), 0), max((int)Math.ceil((double)lifeDefender/defender.getMobsLife()), 0));
    }
}
