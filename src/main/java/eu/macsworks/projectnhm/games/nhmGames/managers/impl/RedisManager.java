package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import eu.macsworks.projectnhm.games.nhmGames.redis.RedisHandler;
import eu.macsworks.projectnhm.games.nhmGames.redis.heartbeat.PodHeartbeatHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisManager extends NHMManager {

    @Getter
    private JedisPool jedisPool;

    private RedisHandler redisHandler;
    private PodHeartbeatHandler podHeartbeatHandler;


    public RedisManager(NHMGames mainInstance) {
        super(mainInstance);
    }

    @Override
    public void onInit() {
        initJedis();

        redisHandler = new RedisHandler(getMainInstance());
        podHeartbeatHandler = new PodHeartbeatHandler(getMainInstance());

        podHeartbeatHandler.init();
        Bukkit.getScheduler().runTaskAsynchronously(getMainInstance(), redisHandler::init);
    }

    @Override
    public void onTick(){
        podHeartbeatHandler.tick();
    }

    @Override
    public void onDestroy() {
        redisHandler.destroy();
        podHeartbeatHandler.destroy();

        if (jedisPool != null) jedisPool.close();
    }

    private void initJedis(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(16);
        jedisPoolConfig.setMaxTotal(24);
        jedisPoolConfig.setMaxWait(Duration.ofSeconds(3));
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);

        jedisPool = new JedisPool(jedisPoolConfig, getMainInstance().getLoadedConfig().getRedisConfig().host(),
                getMainInstance().getLoadedConfig().getRedisConfig().port());
    }
}
