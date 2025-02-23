package miroshka.mountking.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import me.onebone.economyapi.EconomyAPI;
import miroshka.mountking.config.MountKingConfig;
import miroshka.mountking.title.KingTitle;
import miroshka.mountking.title.TitleManager;

import java.util.*;

public class KingManager {
    private Player currentKing;
    private final MountKingConfig config;
    private final Server server;
    private final Map<String, Double> hourlyEarnings;
    private final Map<String, Long> kingStartTimes;
    private final Map<String, Set<Integer>> claimedRewards;
    private long lastRewardTime;
    private long goldenHourEndTime;
    private final TitleManager titleManager;
    private final Random random;

    public KingManager(Server server, MountKingConfig config) {
        this.server = server;
        this.config = config;
        this.hourlyEarnings = new HashMap<>();
        this.kingStartTimes = new HashMap<>();
        this.claimedRewards = new HashMap<>();
        this.lastRewardTime = System.currentTimeMillis();
        this.goldenHourEndTime = 0;
        this.titleManager = new TitleManager(config.getRawConfig());
        this.random = new Random();
    }

    public void processNewKing(Player player) {
        if (currentKing == null || !currentKing.equals(player)) {
            if (currentKing != null) {
                currentKing.sendMessage(config.getLostMessage());
                kingStartTimes.remove(currentKing.getName());
            }
            setNewKing(player);
        }
    }

    private void setNewKing(Player player) {
        currentKing = player;
        kingStartTimes.put(player.getName(), System.currentTimeMillis());
        claimedRewards.putIfAbsent(player.getName(), new HashSet<>());
        
        player.sendMessage(config.getCaptureMessage());
        
        if (config.isMessagesEnabled()) {
            server.broadcastMessage(player.getName() + " has captured the hill!");
        }
    }

    public void rewardKing() {
        if (currentKing != null && currentKing.isOnline()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRewardTime < config.getRewardInterval() * 1000) {
                return;
            }
            lastRewardTime = currentTime;

            Long startTime = kingStartTimes.get(currentKing.getName());
            if (startTime == null) {
                startTime = currentTime;
                kingStartTimes.put(currentKing.getName(), startTime);
            }
            int kingDurationMinutes = (int) ((currentTime - startTime) / (1000 * 60));

            double totalMultiplier = 1.0;
            List<String> multiplierSources = new ArrayList<>();
            
            KingTitle currentTitle = titleManager.getTitleForTime(kingDurationMinutes);
            if (currentTitle != null) {
                double titleMultiplier = currentTitle.getMoneyMultiplier();
                if (titleMultiplier > 1.0) {
                    totalMultiplier *= titleMultiplier;
                    multiplierSources.add(currentTitle.getName() + " title");
                }
                
                titleManager.applyTitleEffects(currentKing, currentTitle);
                currentKing.sendMessage(config.getEffectsMessage()
                    .replace("%title%", currentTitle.getName()));
            }

            if (isGoldenHourActive(currentTime)) {
                totalMultiplier *= config.getGoldenHourMultiplier();
                multiplierSources.add("Golden Hour");
            } else if (shouldStartGoldenHour()) {
                startGoldenHour();
                totalMultiplier *= config.getGoldenHourMultiplier();
                multiplierSources.add("Golden Hour");
            }

            if (totalMultiplier > 1.0) {
                String multiplierSource = String.join(" + ", multiplierSources);
                currentKing.sendMessage(config.getMultiplierMessage()
                    .replace("%multiplier%", String.format("%.1f", totalMultiplier))
                    .replace("%source%", multiplierSource));
            }

            double reward = config.getRewardAmount() * totalMultiplier;

            if (config.getHourlyLimit() > 0) {
                String playerName = currentKing.getName();
                double currentHourlyEarnings = hourlyEarnings.getOrDefault(playerName, 0.0);
                
                if (currentTime - lastRewardTime > 3600000) {
                    currentHourlyEarnings = 0.0;
                }

                if (currentHourlyEarnings >= config.getHourlyLimit()) {
                    return;
                }

                hourlyEarnings.put(playerName, currentHourlyEarnings + reward);
            }

            EconomyAPI.getInstance().addMoney(currentKing, (int)reward);
            currentKing.sendMessage(config.getRewardMessage()
                .replace("%money%", String.valueOf((int)reward)));

            if (config.isLongTermRewardsEnabled()) {
                checkLongTermRewards(kingDurationMinutes);
            }
        }
    }

    private boolean isGoldenHourActive(long currentTime) {
        return config.isEventsEnabled() && 
               config.isGoldenHourEnabled() && 
               currentTime < goldenHourEndTime;
    }

    private boolean shouldStartGoldenHour() {
        return config.isEventsEnabled() && 
               config.isGoldenHourEnabled() && 
               random.nextInt(100) < config.getGoldenHourChance();
    }

    private void startGoldenHour() {
        goldenHourEndTime = System.currentTimeMillis() + (config.getGoldenHourDuration() * 1000L);
        server.broadcastMessage(config.getGoldenHourMessage());
    }

    private void checkLongTermRewards(int kingDurationMinutes) {
        List<?> rewardsList = config.getRawConfig().getList("long-term-rewards.rewards");
        String playerName = currentKing.getName();
        Set<Integer> claimed = claimedRewards.get(playerName);

        for (Object rewardObj : rewardsList) {
            if (rewardObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> reward = (Map<String, Object>) rewardObj;
                int time = ((Number) reward.get("time")).intValue();
                if (kingDurationMinutes >= time && !claimed.contains(time)) {
                    int money = ((Number) reward.get("money")).intValue();
                    String message = ((String) reward.get("message")).replace("&", "§");
                    
                    EconomyAPI.getInstance().addMoney(currentKing, money);
                    currentKing.sendMessage(message);
                    claimed.add(time);
                }
            }
        }
    }

    public Player getCurrentKing() {
        return currentKing;
    }
}
