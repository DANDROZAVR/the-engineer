package engineer.engine.gamestate.mob;

import java.util.HashMap;
import java.util.Map;

public class MobFactory {
    private record MobTraits(String texture, int range) {}

    private final Map<String, MobTraits> descriptionMap = new HashMap<>();

    public void addMobType(String type, String texture, int range) {
        descriptionMap.put(type, new MobTraits(texture, range));
    }

    private class MobImpl implements Mob {
        private final String type;
        private final int number;

        public MobImpl(String type, int number) {
            this.type = type;
            this.number = number;
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
        public int getNumber() {
            return number;
        }

        @Override
        public int getRange() {
            return descriptionMap.get(type).range();
        }
    }

    public Mob produce(String type, int number) {
        return new MobImpl(type, number);
    }
}
