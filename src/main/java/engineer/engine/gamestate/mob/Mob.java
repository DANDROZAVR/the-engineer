package engineer.engine.gamestate.mob;

public interface Mob {
    String getType();
    String getTexture();
    int getMobsAmount();
    int getRemainingSteps();
    void reduceRemainingSteps(int steps);
    void reset();
}
