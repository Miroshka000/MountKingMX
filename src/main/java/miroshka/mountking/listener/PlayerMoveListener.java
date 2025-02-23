package miroshka.mountking.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import miroshka.mountking.config.MountKingConfig;
import miroshka.mountking.manager.KingManager;

public class PlayerMoveListener implements Listener {
    private final MountKingConfig config;
    private final KingManager kingManager;

    public PlayerMoveListener(MountKingConfig config, KingManager kingManager) {
        this.config = config;
        this.kingManager = kingManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        int x = player.getFloorX();
        int z = player.getFloorZ();

        if (isOnHillTop(x, z)) {
            kingManager.processNewKing(player);
        } else if (isInRadius(x, z) && player.getFloorY() >= config.getDisableHeight()) {
            player.setGamemode(0);
            player.setAllowFlight(false);
        }
    }

    private boolean isOnHillTop(int x, int z) {
        return Math.abs(x - config.getHillCenterX()) <= 1 && 
               Math.abs(z - config.getHillCenterZ()) <= 1;
    }

    private boolean isInRadius(int x, int z) {
        return Math.abs(x - config.getHillCenterX()) <= config.getRadius() 
            && Math.abs(z - config.getHillCenterZ()) <= config.getRadius();
    }
}
