package eu.macsworks.projectnhm.games.nhmGames.redis;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import eu.macsworks.projectnhm.games.nhmGames.redis.pubsub.NHMJedisPubSub;
import eu.macsworks.projectnhm.games.nhmGames.redis.pubsub.impl.PlayerServersPubSub;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RedisHandler implements NHMLifecycledObject {

    private static final long INITIAL_BACKOFF_MILLIS = 1_000L;
    private static final long MAX_BACKOFF_MILLIS = 30_000L;

    private final NHMGames mainInstance;

    private JedisPool jedisPool;
    private final List<NHMJedisPubSub> subscribers = new ArrayList<>();
    private final List<Thread> subscriberThreads = new ArrayList<>();
    private volatile boolean shuttingDown = false;

    @SneakyThrows
    @Override
    public void onInit() {
        jedisPool = mainInstance.getManager(RedisManager.class).getJedisPool();
        addSubscriber(new PlayerServersPubSub(mainInstance));
    }

    private void addSubscriber(NHMJedisPubSub jedisPubSub) {
        if(jedisPool == null || jedisPool.isClosed()) throw new IllegalStateException();

        subscribers.add(jedisPubSub);

        Thread t = new Thread(() -> runSubscribeLoop(jedisPubSub), "nhm-redis-sub-" + jedisPubSub.getChannel());
        t.setDaemon(true);
        t.start();
        subscriberThreads.add(t);
    }

    private void runSubscribeLoop(NHMJedisPubSub jedisPubSub) {
        long backoff = INITIAL_BACKOFF_MILLIS;

        while (!shuttingDown) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(jedisPubSub, jedisPubSub.getChannel());
            } catch (JedisException e) {
                if (shuttingDown) return;

                NHMGames.LOGGER.warn("Redis subscribe to '{}' failed, reconnecting in {}ms", jedisPubSub.getChannel(), backoff, e);

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }

                backoff = Math.min(backoff * 2, MAX_BACKOFF_MILLIS);
            }
        }
    }

    @Override
    public void onDestroy() {
        shuttingDown = true;

        subscribers.forEach(s -> {
            try { s.unsubscribe(); } catch (Exception ignored) {}
        });

        subscriberThreads.forEach(Thread::interrupt);
    }
}
