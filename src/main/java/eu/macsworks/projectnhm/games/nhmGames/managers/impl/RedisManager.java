package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import eu.macsworks.projectnhm.games.nhmGames.redis.RedisHandler;
import org.bukkit.Bukkit;

public class RedisManager extends NHMManager {

    private RedisHandler redisHandler;

    public RedisManager(NHMGames mainInstance) {
        super(mainInstance);
    }

    @Override
    public void onInit() {
        redisHandler = new RedisHandler(getMainInstance());
        Bukkit.getScheduler().runTaskAsynchronously(getMainInstance(), redisHandler::init);
    }

    @Override
    public void onDestroy() {
        redisHandler.destroy();
    }
}
