package eu.macsworks.projectnhm.games.nhmGames;

import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.WorldManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

@Getter
public final class NHMGames extends JavaPlugin {

    public static final SplittableRandom RANDOM = new SplittableRandom();

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private static NHMGames instance = null;

    @Getter(AccessLevel.PRIVATE)
    private final Map<NHMManager.ManagerType, NHMManager> managers = new HashMap<>();

    @Override
    public void onEnable() {
        setInstance(this);
        long timeInitStart = System.currentTimeMillis();

        loadManagers();

        getLogger().info(String.format("Initialization done (%sms)",  System.currentTimeMillis() - timeInitStart));
    }

    private void loadManagers(){
        addManager(new GameManager());
        addManager(new WorldManager());
    }

    public void addManager(NHMManager manager){
        managers.put(manager.getManagerType(), manager);

        manager.init();
    }

    public void removeManager(NHMManager manager){
        managers.remove(manager.getManagerType());

        manager.destroy();
    }

    /**
     * Returns the (casted) requested manager. Will throw if requesting a mismatched manager between ManagerType and the actual class type.
     * @param managerType Type of manager requested
     * @return Already casted manager
     * @param <T> Typed manager
     */
    @SuppressWarnings("unchecked")
    public <T extends NHMManager> T getManager(NHMManager.ManagerType managerType) {
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
}
