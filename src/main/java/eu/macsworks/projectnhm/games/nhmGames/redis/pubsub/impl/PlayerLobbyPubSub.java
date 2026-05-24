package eu.macsworks.projectnhm.games.nhmGames.redis.pubsub.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import eu.macsworks.projectnhm.games.nhmGames.redis.pubsub.NHMJedisPubSub;
import eu.macsworks.projectnhm.games.nhmGames.utils.SignatureUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class PlayerLobbyPubSub extends NHMJedisPubSub {

    private static final long MAX_MESSAGE_AGE_MILLIS = 30_000L;

    private JedisPool jedisPool;

    private final NHMGames mainInstance;
    private final GameManager gameManager;

    public PlayerLobbyPubSub(NHMGames mainInstance) {
        super("nhm-games:player-lobbys");

        this.jedisPool = mainInstance.getManager(RedisManager.class).getJedisPool();

        this.mainInstance = mainInstance;
        this.gameManager = mainInstance.getManager(GameManager.class);
    }

    public void sendToLobby(UUID uuid){
        String originServerName = mainInstance.getLoadedConfig().getServerName();
        long epoch = System.currentTimeMillis();
        String payload = String.format("%s:%s:%d", uuid, originServerName, epoch);
        payload = payload + ":" + SignatureUtils.sign(payload);

        String finalPayload = payload;

        Bukkit.getScheduler().runTaskAsynchronously(mainInstance, () -> {
            JedisPool pool = mainInstance.getManager(RedisManager.class).getJedisPool();
            try (Jedis jedis = pool.getResource()) {
                jedis.publish(getChannel(), finalPayload);
            } catch (Exception e) {
                NHMGames.LOGGER.warn("Failed to publish send-to-lobby for {}", uuid, e);
            }
        });
    }
}
