package miroshka.mountking;

import cn.nukkit.plugin.PluginBase;
import miroshka.mountking.config.MountKingConfig;
import miroshka.mountking.listener.PlayerMoveListener;
import miroshka.mountking.manager.KingManager;

public class MountKingPlugin extends PluginBase {
    private KingManager kingManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        MountKingConfig config = new MountKingConfig(getConfig());
        kingManager = new KingManager(getServer(), config);
        
        PlayerMoveListener moveListener = new PlayerMoveListener(config, kingManager);
        getServer().getPluginManager().registerEvents(moveListener, this);

        getServer().getScheduler().scheduleRepeatingTask(this, 
            () -> kingManager.rewardKing(), 400);
    }
}
