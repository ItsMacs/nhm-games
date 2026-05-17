package eu.macsworks.projectnhm.games.nhmGames.games.core.maps;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.WorldManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;
import oshi.util.tuples.Pair;

import java.util.*;

@Getter
public abstract class InstancedGameMap implements NHMLifecycledObject {

    private final NHMGames mainInstance;

    private final NHMGame game;
    private final LoadedGameMap loadedGameMap;

    private final World gameWorld;

    private Vector minBound;
    private Vector maxBound;

    private final Map<Material, Marker> mapMarkers = new HashMap<>();

    public InstancedGameMap(NHMGame game, LoadedGameMap loadedGameMap){
        this.game = game;
        this.loadedGameMap = loadedGameMap;
        this.mainInstance = game.getMainInstance();
        this.gameWorld = mainInstance.<WorldManager>getManager(NHMManager.ManagerType.WORLD).getGameWorld();
    }

    protected abstract void registerMarkers();

    @Override
    public void onInit() {
        registerMarkers();

        Pair<Vector, Vector> bounds = loadedGameMap.placeMap(mapMarkers);
        this.minBound = bounds.getA();
        this.maxBound = bounds.getB();
    }

    @Override
    public void onDestroy() {

    }

    public Optional<Marker> getMarkerOfType(Class<? extends Marker> markerType){
        return mapMarkers.values().stream().filter(marker -> marker.getClass().equals(markerType)).findFirst();
    }

    public void registerMarker(Marker marker){
        marker.world = gameWorld;
        mapMarkers.put(marker.getMaterial(), marker);
    }

    public boolean contains(Location location){
        if(!location.getWorld().equals(gameWorld)) return false;

        return location.getBlockX() >= minBound.getBlockX() && location.getBlockX() <= maxBound.getBlockX()
                && location.getBlockY() >= minBound.getBlockY() && location.getBlockY() <= maxBound.getBlockY()
                && location.getBlockZ() >= minBound.getBlockZ() && location.getBlockZ() <= maxBound.getBlockZ();
    }


    @Getter
    public static abstract class Marker {
        private World world;
        private final Material material;
        private final List<Vector> markers = new ArrayList<>();

        public Marker(Material material){
            this.material = material;
        }

        public void addMarker(Vector marker){
            markers.add(marker);
            world.getBlockAt(marker.getBlockX(), marker.getBlockY(), marker.getBlockZ()).setType(Material.AIR);
        }

        public List<Vector> getMarkers() {
            return Collections.unmodifiableList(markers);
        }
    }
}
