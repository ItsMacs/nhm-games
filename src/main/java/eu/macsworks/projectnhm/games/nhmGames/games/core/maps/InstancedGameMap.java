package eu.macsworks.projectnhm.games.nhmGames.games.core.maps;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
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

    private final Map<Material, Marker> registeredMarkers = new HashMap<>();
    private List<InstancedMarker<? extends Marker>> mapMarkers;

    public InstancedGameMap(NHMGame game, LoadedGameMap loadedGameMap){
        this.game = game;
        this.loadedGameMap = loadedGameMap;
        this.mainInstance = game.getMainInstance();
        this.gameWorld = mainInstance.getManager(WorldManager.class).getGameWorld();
    }

    protected abstract void registerMarkers();

    @Override
    public void onInit() {
        registerMarkers();

        InstancedMapData data = loadedGameMap.placeMap(registeredMarkers);

        mapMarkers = Collections.unmodifiableList(data.mapMarkers);
        this.minBound = data.bounds().getA();
        this.maxBound = data.bounds().getB();
    }

    @Override
    public void onDestroy() {

    }

    @SuppressWarnings("unchecked")
    public <T extends Marker> List<InstancedMarker<T>> getMarkersOfType(Class<T> markerType){
        return mapMarkers.stream()
                .filter(marker -> markerType.isInstance(marker.marker()))
                .map(marker -> (InstancedMarker<T>) marker)
                .toList();
    }

    public void registerMarker(Marker marker){
        registeredMarkers.put(marker.getMaterial(), marker);
    }

    public boolean contains(Location location){
        if(!location.getWorld().equals(gameWorld)) return false;

        return location.getBlockX() >= minBound.getBlockX() && location.getBlockX() <= maxBound.getBlockX()
                && location.getBlockY() >= minBound.getBlockY() && location.getBlockY() <= maxBound.getBlockY()
                && location.getBlockZ() >= minBound.getBlockZ() && location.getBlockZ() <= maxBound.getBlockZ();
    }

    public Location getCenter(){
        return minBound.clone().add(maxBound).multiply(0.5).toLocation(gameWorld);
    }


    @Getter
    public static abstract class Marker {
        private final Material material;

        public Marker(Material material) {
            this.material = material;
        }
    }

    public record InstancedMarker<T extends Marker>(T marker, Vector position, World world) {
            public InstancedMarker(T marker, Vector position, World world) {
                this.marker = marker;
                this.position = position;
                this.world = world;

                world.getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ()).setType(Material.AIR);
            }

            public Location getLocation() {
                return position.toLocation(world);
            }
        }

    public record InstancedMapData(Pair<Vector, Vector> bounds, List<InstancedMarker<? extends Marker>> mapMarkers){};
}
