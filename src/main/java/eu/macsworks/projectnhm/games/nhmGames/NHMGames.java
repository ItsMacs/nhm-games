package eu.macsworks.projectnhm.games.nhmGames;

import com.google.gson.Gson;
import eu.macsworks.projectnhm.games.nhmGames.config.LoadedConfig;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.CommandsManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.WorldManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

@Getter
public final class NHMGames extends JavaPlugin {

    public static final String POD_ID = String.format("nhm-games:pods:%s", System.getenv("POD_ID"));
    public static final Gson GSON = new Gson();
    public static final SplittableRandom RANDOM = new SplittableRandom();
    public static final Logger LOGGER = LoggerFactory.getLogger(NHMGames.class);

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private static NHMGames instance = null;

    private LoadedConfig loadedConfig;

    @Getter(AccessLevel.PRIVATE)
    private final Map<Class<? extends NHMManager>, NHMManager> managers = new HashMap<>();

    @Override
    public void onEnable() {
        if(System.getenv("POD_ID") == null){
            crash("Pod ID isn't set in env variables.", new IllegalStateException("Pod ID isn't set in env variables."));
            return;
        }

        setInstance(this);
        long timeInitStart = System.currentTimeMillis();

        loadedConfig = new LoadedConfig(this);
        loadedConfig.init();

        loadManagers();
        loadTasks();

        getLogger().info(String.format("Initialization done (%sms)",  System.currentTimeMillis() - timeInitStart));
    }

    private void loadTasks(){
        Bukkit.getScheduler().runTaskTimer(this, () -> managers.values().forEach(NHMManager::onTick), 0L, 10L);
    }

    private void loadManagers(){
        addManager(new GameManager(this));
        addManager(new WorldManager(this));
        addManager(new RedisManager(this));
        addManager(new CommandsManager(this));
    }

    private void addManager(NHMManager manager){
        managers.put(manager.getClass(), manager);

        manager.init();
    }

    private void removeManager(NHMManager manager){
        managers.remove(manager.getClass());

        manager.destroy();
    }

    /**
     * Returns the (casted) requested manager. Will throw if requesting a mismatched manager between ManagerType and the actual class type.
     * @param managerType Type of manager requested
     * @return Already casted manager
     * @param <T> Typed manager
     */
    @SuppressWarnings("unchecked")
    public <T extends NHMManager> T getManager(Class<T> managerType) {
        if(!managers.containsKey(managerType)){
            throw new NullPointerException(String.format("Manager %s not found", managerType));
        }

        return (T) managers.get(managerType);
    }

    @Override
    public void onDisable() {
        long timeDisStart = System.currentTimeMillis();

        getLogger().info(String.format("Disabling done (%sms)",  System.currentTimeMillis() - timeDisStart));
    }

    /**
     * E-stop in case anything goes south and we need to destroy the pod quickly
     * Knowing myself this will be the most used call of the project
     */
    public static void crash(String message, Throwable cause) {
        LOGGER.error("FATAL: {} — terminating pod", message, cause);
        Runtime.getRuntime().halt(1);
    }
}
