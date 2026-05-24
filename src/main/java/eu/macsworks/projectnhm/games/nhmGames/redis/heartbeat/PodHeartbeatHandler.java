package eu.macsworks.projectnhm.games.nhmGames.redis.heartbeat;

import com.google.gson.Gson;
import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameType;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.InProgressState;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.LobbyState;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class PodHeartbeatHandler implements NHMLifecycledObject {

    private final NHMGames mainInstance;

    private JedisPool jedisPool;
    private volatile long lastHeartbeatEpoch = 0L;
    private final AtomicBoolean inFlight = new AtomicBoolean(false);

    @Override
    public void init(){
        jedisPool = mainInstance.getManager(RedisManager.class).getJedisPool();
    }

    public void tick(){
        if(System.currentTimeMillis() - lastHeartbeatEpoch < 5L * 1000) return;
        if(!inFlight.compareAndSet(false, true)) return; //a heartbeat is already in flight

        //gathered here and not in async to gather everything synchronously to avoid concurrency
        String heartbeatPayload = getHeartbeatPayload();

        Bukkit.getScheduler().runTaskAsynchronously(mainInstance, () -> {
            //TTL for every heartbeat is 10s. If it expires, the pod exploded in some way, if it explodes
            //we need this to expire as it certainly won't say "heyo i crashed"
            try(Jedis jedis = jedisPool.getResource()){
                jedis.set(NHMGames.POD_ID, heartbeatPayload, SetParams.setParams().ex(10L));
                lastHeartbeatEpoch = System.currentTimeMillis();
            } catch (Exception e) {
                NHMGames.LOGGER.warn("Heartbeat failed", e);
            } finally {
                inFlight.set(false);
            }
        });
    }

    private String getHeartbeatPayload(){
        return HeartbeatPayload.create(mainInstance).toString();
    }

    record HeartbeatPayload(List<GameSnapshot> games, double tps, long mspt, int totalPlayers){

        @Override
        public @NonNull String toString(){
            return NHMGames.GSON.toJson(this);
        }

        public static HeartbeatPayload create(NHMGames mainInstance){
            List<GameSnapshot> gameSnapshots = mainInstance.getManager(GameManager.class).getGames()
                    .stream().map(game -> new GameSnapshot(game.getGameType().key().toString(),
                            game.getPlayersUUIDs(),
                            game.getMinPlayers(),
                            game.getMaxPlayers(),
                            getRedisGameState(game)))
                    .toList();

            return new HeartbeatPayload(gameSnapshots,
                    Bukkit.getServer().getTPS()[0],
                    Bukkit.getServer().getTickTimes()[0],
                    Bukkit.getOnlinePlayers().size());
        }

        private static RedisGameState getRedisGameState(NHMGame game){
            if(game.getGameState() instanceof LobbyState) return RedisGameState.LOBBY;
            if(game.getGameState() instanceof InProgressState) return RedisGameState.IN_PROGRESS;

            return RedisGameState.ENDED;
        }

        record GameSnapshot(String gameType, List<UUID> players, int minPlayers, int maxPlayers, RedisGameState gameState){}

        enum RedisGameState {
            LOBBY,
            IN_PROGRESS,
            ENDED
        }
    }

}
