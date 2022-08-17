package engineer.engine.gamestate.mob;

public interface Mob {
    String getType();
    String getTexture();

    void addMobs(int value);
    int getMobsAmount();

    int getRemainingSteps();
    void reduceRemainingSteps(int steps);
    void reset();
}
