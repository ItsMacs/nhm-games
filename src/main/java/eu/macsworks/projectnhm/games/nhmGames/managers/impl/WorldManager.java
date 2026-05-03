package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

@Getter
public class WorldManager extends NHMManager {

    private World gameWorld;

    @Getter(AccessLevel.NONE)
    private int nextChunkX = 0, nextChunkZ = 0;

    public WorldManager() {
        super(ManagerType.WORLD);
    }

    @Override
    public void onInit() {
        //Create an empty world for this lifecycle, the pod starts without a gameworld
        WorldCreator creator = WorldCreator.name("game-world").generator(new ChunkGenerator() {});

        gameWorld = creator.createWorld();
    }

    public MapOffset getNextChunkCoordinates() {
        nextChunkX += 512;
        if(nextChunkX / 512 >= 10){
            nextChunkX = 0;
            nextChunkZ += 512;
        }

        return new MapOffset(nextChunkX, nextChunkZ);
    }

    @Override
    public void onDestroy() {

    }

    public record MapOffset(int chunkX, int chunkZ) {}
}
