package MK;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import me.onebone.economyapi.EconomyAPI;

public class MountKing extends PluginBase implements Listener {
    private Player king;
    private int hillCenterX;
    private int hillCenterZ;
    private int rewardAmount;
    private String captureMessage;
    private String rewardMessage;
    private TextFormat messageColor;
    private boolean kingAnnounced = false;
    private int radius;
    private int disableRadius;
    private int disableHeight;

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        this.loadConfigValues();
        this.getLogger().info("Плагин Царь горы был включен!");
        this.getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            this.rewardKing(this.king);
        }, 400);
    }

    public void onDisable() {
        this.getLogger().info("Плагин Царь горы был выключен!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int x = player.getFloorX();
        int z = player.getFloorZ();
        if (this.isOnHillTop(x, z)) {
            if (this.king == null) {
                this.king = player;
                if (!this.kingAnnounced) {
                    this.kingAnnounced = true;
                    this.getServer().broadcastMessage(this.messageColor + String.format(this.captureMessage, player.getName()));
                    this.rewardKing(player);
                }
            } else if (!this.king.equals(player)) {
                player.sendMessage(TextFormat.RED + String.format(this.captureMessage, this.king.getName()));
                this.king = player;
                if (!this.kingAnnounced) {
                    this.kingAnnounced = true;
                    this.getServer().broadcastMessage(this.messageColor + String.format(this.captureMessage, player.getName()));
                    this.rewardKing(player);
                }
            }
        } else {
            this.kingAnnounced = false;
            if (this.isInRadius(x, z) && player.getFloorY() >= this.disableHeight) {
                player.setGamemode(0);
                player.setAllowFlight(false);
            }
        }

    }

    private boolean isOnHillTop(int x, int z) {
        return x == this.hillCenterX && z == this.hillCenterZ;
    }

    private boolean isInRadius(int x, int z) {
        return Math.abs(x - this.hillCenterX) <= this.radius && Math.abs(z - this.hillCenterZ) <= this.radius;
    }

    private void rewardKing(Player player) {
        if (player != null) {
            EconomyAPI.getInstance().addMoney(player, (double)this.rewardAmount);
            String message = this.rewardMessage.replace("{amount}", String.valueOf(this.rewardAmount));
            player.sendPopup(message);
        }

    }

    private void loadConfigValues() {
        this.hillCenterX = this.getConfig().getInt("hillCenterX", 0);
        this.hillCenterZ = this.getConfig().getInt("hillCenterZ", 0);
        this.rewardAmount = this.getConfig().getInt("rewardAmount", 0);
        this.captureMessage = this.getConfig().getString("captureMessage", "Вы захватили гору у %s!");
        this.rewardMessage = this.getConfig().getString("rewardMessage", "Вы получили {amount}$ за контроль над горой!");
        String colorString = this.getConfig().getString("messageColor", "GREEN");
        this.messageColor = TextFormat.valueOf(colorString);
        this.radius = this.getConfig().getInt("radius", 3);
        this.disableRadius = this.getConfig().getInt("disableRadius", 5);
        this.disableHeight = this.getConfig().getInt("disableHeight", 70);
    }
}
