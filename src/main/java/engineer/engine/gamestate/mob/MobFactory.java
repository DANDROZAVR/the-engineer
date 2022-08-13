package engineer.engine.gamestate.mob;

import java.util.HashMap;
import java.util.Map;

public class MobFactory {
    private record MobTraits(String texture, int stepsPerTurn) {}

    private final Map<String, MobTraits> descriptionMap = new HashMap<>();

    public void addMobType(String type, String texture, int stepsPerTurn) {
        descriptionMap.put(type, new MobTraits(texture, stepsPerTurn));
    }

    private class MobImpl implements Mob {
        private final String type;
        private final int number;
        private int remainingSteps;

        public MobImpl(String type, int mobsAmount) {
            this.type = type;
            this.number = mobsAmount;
            reset();
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getTexture() {
            return descriptionMap.get(type).texture();
        }

        @Override
        public int getMobsAmount() {
            return number;
        }

        @Override
        public int getRemainingSteps() {
            return remainingSteps;
        }

        @Override
        public void reduceRemainingSteps(int steps) {
            steps = Math.min(steps, remainingSteps);
            remainingSteps -= steps;
        }

        @Override
        public void reset() {
            remainingSteps = descriptionMap.get(type).stepsPerTurn;
        }
    }

    public Mob produce(String type, int number) {
        return new MobImpl(type, number);
    }

}
