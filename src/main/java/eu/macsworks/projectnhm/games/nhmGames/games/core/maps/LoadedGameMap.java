package eu.macsworks.projectnhm.games.nhmGames.games.core.maps;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.WorldManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadedGameMap {

    private final NHMGames mainInstance;
    private final WorldManager worldManager;

    private final String mapID;

    private final Map<Vector, BlockData> blocks;

    public LoadedGameMap(NHMGame ownerGame, String mapID, Map<Vector, BlockData> blocks){
        this.mapID = mapID;

        this.blocks = blocks;

        this.mainInstance = ownerGame.getMainInstance();
        this.worldManager = mainInstance.getManager(WorldManager.class);
    }

    public InstancedGameMap.InstancedMapData placeMap(Map<Material, InstancedGameMap.Marker> registeredMarkers){
        World world = worldManager.getGameWorld();

        WorldManager.MapOffset offset = worldManager.getNextChunkCoordinates();

        List<InstancedGameMap.InstancedMarker<? extends InstancedGameMap.Marker>> markers = new ArrayList<>();

        //Group blocks by chunk, storing world-space coords + their blockdata;
        //track bounds simultaneously to avoid a second pass
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        Map<Long, List<Object[]>> byChunk = new HashMap<>();
        for (Map.Entry<Vector, BlockData> entry : blocks.entrySet()) {
            Vector pos = entry.getKey();

            int wx = pos.getBlockX() + offset.chunkX() * 16;
            int wy = pos.getBlockY();
            int wz = pos.getBlockZ() + offset.chunkZ() * 16;

            if (wx < minX) minX = wx; if (wx > maxX) maxX = wx;
            if (wy < minY) minY = wy; if (wy > maxY) maxY = wy;
            if (wz < minZ) minZ = wz; if (wz > maxZ) maxZ = wz;

            Material blockMaterial = entry.getValue().getMaterial();

            if(registeredMarkers.containsKey(blockMaterial)){
                InstancedGameMap.Marker marker = registeredMarkers.get(blockMaterial);
                InstancedGameMap.InstancedMarker<?> instancedMarker = new InstancedGameMap.InstancedMarker<>(marker, pos, world);

                markers.add(instancedMarker);
            }

            long key = ((long) (wx >> 4) << 32) | ((wz >> 4) & 0xFFFFFFFFL);
            byChunk.computeIfAbsent(key, _ -> new ArrayList<>()).add(new Object[]{wx, wy, wz, entry.getValue()});
        }

        for (Map.Entry<Long, List<Object[]>> entry : byChunk.entrySet()) {
            long key = entry.getKey();
            int cx = (int) (key >> 32);
            int cz = (int) (key & 0xFFFFFFFFL);
            List<Object[]> chunkBlocks = entry.getValue();

            world.getChunkAtAsync(cx, cz).thenAccept(chunk -> {
                for (Object[] b : chunkBlocks) {
                    chunk.getBlock((int) b[0] & 15, (int) b[1], (int) b[2] & 15)
                         .setBlockData((BlockData) b[3], false);
                }
            });
        }

        Pair<Vector, Vector> mapBounds = new Pair<>(new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
        return new InstancedGameMap.InstancedMapData(mapBounds, markers);
    }
}
