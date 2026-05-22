package eu.macsworks.projectnhm.games.nhmGames.config;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import lombok.Getter;

@Getter
public class LoadedConfig implements NHMLifecycledObject {

    private final NHMGames mainInstance;
    private String serverName;
    private LoadedRedisConfig redisConfig;

    public LoadedConfig(NHMGames mainInstance) {
        this.mainInstance = mainInstance;
    }

    @Override
    public void onInit() {
        mainInstance.saveDefaultConfig();

        serverName = mainInstance.getConfig().getString("server-name");
        redisConfig = new LoadedRedisConfig(mainInstance.getConfig().getString("redis.host"), mainInstance.getConfig().getInt("redis.port"));
    }


    public record LoadedRedisConfig(String host, int port){}

}
