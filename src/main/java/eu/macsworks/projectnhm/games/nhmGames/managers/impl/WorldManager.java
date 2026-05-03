package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.util.UUID;

@Getter
public class WorldManager extends NHMManager {

    private World gameWorld;

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

    /**
     * Returns a new chunk x, and automatically shifts chunkZ (and resets itself to 0) if more than 10 chunkX on the same Z have happened
     * @return the chunkX coordinate for the next game map
     */
    public int getNextChunkX() {
        int chunkX = nextChunkX;

        nextChunkX += 512;
        if(nextChunkX / 512 >= 10){
            nextChunkX = 0;
            nextChunkZ += 512;
        }

        return chunkX;
    }

    @Override
    public void onDestroy() {

    }
}
