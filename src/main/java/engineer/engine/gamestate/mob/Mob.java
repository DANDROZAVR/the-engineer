package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.turns.Player;

public interface Mob {
    String getType();
    String getTexture();

    void addMobs(int value);
    int getMobsAmount();
    int getMobsAttack();
    int getMobsLife();
    int getRemainingSteps();
    void reduceRemainingSteps(int steps);
    boolean canAttackInThisTurn();
    void makeAttack();
    Player getOwner();
    void reset();
}
