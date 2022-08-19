package engineer.engine.gamestate.mob;

import engineer.engine.gamestate.turns.Player;

import java.util.HashMap;
import java.util.Map;

public class MobFactory {
    private record MobTraits(String texture, int stepsPerTurn, int attack, int life) {}

    private final Map<String, MobTraits> descriptionMap = new HashMap<>();

    public void addMobType(String type, String texture, int stepsPerTurn, int attack, int life) {
        descriptionMap.put(type, new MobTraits(texture, stepsPerTurn, attack, life));
    }

    private class MobImpl implements Mob {
        private final String type;
        private final Player owner;
        private int number;
        private int remainingSteps;

        public MobImpl(String type, int mobsAmount, Player owner) {
            this.type = type;
            this.number = mobsAmount;
            this.owner = owner;
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
        public void addMobs(int value) {
            number += value;
        }

        @Override
        public int getMobsAmount() {
            return number;
        }

        @Override
        public int getMobsAttack() {
            return descriptionMap.get(type).attack();
        }

        @Override
        public int getMobsLife() {
            return descriptionMap.get(type).life();
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
        public Player getOwner() {
            return owner;
        }

        @Override
        public void reset() {
            remainingSteps = descriptionMap.get(type).stepsPerTurn;
        }
    }

    public Mob produce(String type, int number, Player owner) {
        return new MobImpl(type, number, owner);
    }
}
