package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.resource.Resource;
import engineer.engine.gamestate.turns.Player;

import java.util.List;

public interface Mob {
    String getType();
    String getTexture();

    void addMobs(int value);
    void reduceMobs(int value);
    int getMobsAmount();
    int getMobsAttack();
    int getMobsLife();
    int getRemainingSteps();
    void reduceRemainingSteps(int steps);
    boolean canAttackInThisTurn();
    void makeAttack();
    List<Resource> getResToProduce();
    Player getOwner();
    void reset();
}
