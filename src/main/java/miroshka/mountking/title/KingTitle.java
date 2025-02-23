package miroshka.mountking.title;

import cn.nukkit.potion.Effect;
import java.util.List;

public class KingTitle {
    private final String name;
    private final int requiredTime;
    private final String prefix;
    private final double moneyMultiplier;
    private final List<Effect> effects;

    public KingTitle(String name, int requiredTime, String prefix, double moneyMultiplier, List<Effect> effects) {
        this.name = name;
        this.requiredTime = requiredTime;
        this.prefix = prefix;
        this.moneyMultiplier = moneyMultiplier;
        this.effects = effects;
    }

    public String getName() {
        return name;
    }

    public int getRequiredTime() {
        return requiredTime;
    }

    public String getPrefix() {
        return prefix;
    }

    public double getMoneyMultiplier() {
        return moneyMultiplier;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
