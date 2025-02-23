package miroshka.mountking.title;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.util.*;

public class TitleManager {
    private final Map<String, KingTitle> titles;
    private final boolean enabled;

    public TitleManager(Config config) {
        this.enabled = config.getBoolean("titles.enabled", true);
        this.titles = new TreeMap<>();
        
        if (enabled) {
            loadTitles(config.getSection("titles.titles"));
        }
    }

    private void loadTitles(ConfigSection section) {
        for (String titleName : section.getKeys(false)) {
            ConfigSection titleSection = section.getSection(titleName);
            int time = titleSection.getInt("time", 0);
            String prefix = titleSection.getString("prefix", "").replace("&", "§");
            double multiplier = titleSection.getSection("rewards").getDouble("money-multiplier", 1.0);
            
            List<Effect> effects = new ArrayList<>();
            List<String> effectsList = titleSection.getSection("rewards").getStringList("effects");
            for (String effectStr : effectsList) {
                String[] parts = effectStr.split(":");
                if (parts.length == 2) {
                    try {
                        Effect effect = Effect.getEffectByName(parts[0]);
                        if (effect != null) {
                            effect.setAmplifier(Integer.parseInt(parts[1]) - 1);
                            effect.setDuration(20 * 5);
                            effects.add(effect);
                        }
                    } catch (Exception ignored) {}
                }
            }
            
            titles.put(titleName, new KingTitle(titleName, time, prefix, multiplier, effects));
        }
    }

    public KingTitle getTitleForTime(int minutes) {
        if (!enabled || titles.isEmpty()) {
            return null;
        }

        KingTitle bestTitle = null;
        for (KingTitle title : titles.values()) {
            if (minutes >= title.getRequiredTime() && 
                (bestTitle == null || title.getRequiredTime() > bestTitle.getRequiredTime())) {
                bestTitle = title;
            }
        }
        return bestTitle;
    }

    public void applyTitleEffects(Player player, KingTitle title) {
        if (enabled && title != null) {
            for (Effect effect : title.getEffects()) {
                player.addEffect(effect);
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
