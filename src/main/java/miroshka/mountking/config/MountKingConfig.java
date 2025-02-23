package miroshka.mountking.config;

import cn.nukkit.utils.Config;

public class MountKingConfig {
    private final String world;
    private final int hillCenterX;
    private final int hillCenterY;
    private final int hillCenterZ;
    private final int rewardAmount;
    private final int rewardInterval;
    private final int hourlyLimit;
    private final boolean enableMessages;
    private final String rewardMessage;
    private final String titleUpMessage;
    private final String captureMessage;
    private final String lostMessage;
    private final String effectsMessage;
    private final String multiplierMessage;
    private final int radius;
    private final int disableRadius;
    private final int disableHeight;

    private final boolean eventsEnabled;
    private final boolean goldenHourEnabled;
    private final int goldenHourChance;
    private final int goldenHourDuration;
    private final double goldenHourMultiplier;
    private final String goldenHourMessage;

    private final boolean longTermRewardsEnabled;
    private final Config rawConfig;

    public MountKingConfig(Config config) {
        this.rawConfig = config;
        
        this.world = config.getString("king-zone.world", "world");
        this.hillCenterX = config.getInt("king-zone.x", 0);
        this.hillCenterY = config.getInt("king-zone.y", 64);
        this.hillCenterZ = config.getInt("king-zone.z", 0);
        this.radius = config.getInt("king-zone.radius", 5);
        this.disableRadius = config.getInt("king-zone.disable-radius", 10);
        this.disableHeight = config.getInt("king-zone.disable-height", 70);
        
        this.rewardAmount = config.getInt("rewards.amount", 100);
        this.rewardInterval = config.getInt("rewards.interval", 20);
        this.hourlyLimit = config.getInt("rewards.hourly-limit", 0);
        this.enableMessages = config.getBoolean("rewards.enable-messages", false);
        
        this.rewardMessage = config.getString("messages.reward", "&aYou received %money% coins!")
            .replace("&", "§");
        this.titleUpMessage = config.getString("messages.title-up", "&6You have earned the title: %title%")
            .replace("&", "§");
        this.captureMessage = config.getString("messages.capture", "&aYou have captured the hill!")
            .replace("&", "§");
        this.lostMessage = config.getString("messages.lost", "&cYou have lost control of the hill!")
            .replace("&", "§");
        this.effectsMessage = config.getString("messages.effects", "&bYou received special effects for your %title% title!")
            .replace("&", "§");
        this.multiplierMessage = config.getString("messages.multiplier", "&eYour current reward multiplier is x%multiplier% (%source%)")
            .replace("&", "§");

        this.eventsEnabled = config.getBoolean("events.enabled", true);
        this.goldenHourEnabled = config.getBoolean("events.golden-hour.enabled", true);
        this.goldenHourChance = config.getInt("events.golden-hour.chance", 15);
        this.goldenHourDuration = config.getInt("events.golden-hour.duration", 300);
        this.goldenHourMultiplier = config.getDouble("events.golden-hour.multiplier", 2.0);
        this.goldenHourMessage = config.getString("events.golden-hour.message", "&6⚡ Golden Hour has started!")
            .replace("&", "§");
            
        this.longTermRewardsEnabled = config.getBoolean("long-term-rewards.enabled", true);
    }

    public String getWorld() { return world; }
    public int getHillCenterX() { return hillCenterX; }
    public int getHillCenterY() { return hillCenterY; }
    public int getHillCenterZ() { return hillCenterZ; }
    public int getRewardAmount() { return rewardAmount; }
    public int getRewardInterval() { return rewardInterval; }
    public int getHourlyLimit() { return hourlyLimit; }
    public boolean isMessagesEnabled() { return enableMessages; }
    public String getRewardMessage() { return rewardMessage; }
    public String getTitleUpMessage() { return titleUpMessage; }
    public String getCaptureMessage() { return captureMessage; }
    public String getLostMessage() { return lostMessage; }
    public String getEffectsMessage() { return effectsMessage; }
    public String getMultiplierMessage() { return multiplierMessage; }
    public int getRadius() { return radius; }
    public int getDisableRadius() { return disableRadius; }
    public int getDisableHeight() { return disableHeight; }
    
    public boolean isEventsEnabled() { return eventsEnabled; }
    public boolean isGoldenHourEnabled() { return goldenHourEnabled; }
    public int getGoldenHourChance() { return goldenHourChance; }
    public int getGoldenHourDuration() { return goldenHourDuration; }
    public double getGoldenHourMultiplier() { return goldenHourMultiplier; }
    public String getGoldenHourMessage() { return goldenHourMessage; }
    
    public boolean isLongTermRewardsEnabled() { return longTermRewardsEnabled; }
    public Config getRawConfig() { return rawConfig; }
}
