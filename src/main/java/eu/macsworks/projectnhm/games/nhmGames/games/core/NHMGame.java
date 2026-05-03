package eu.macsworks.projectnhm.games.nhmGames.games.core;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.utils.SchematicLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public abstract class NHMGame implements NHMLifecycledObject {

    private final NHMGames mainInstance;
    private final String gameID;
    private final int maxPlayers;

    @Getter(AccessLevel.PRIVATE)
    private final List<LoadedGameMap> gameMaps = new ArrayList<>();

    private InstancedGameMap gameMap;

    @Override
    public void onInit(){
        loadMaps();

        //TODO: Make selection a VIP perk
        chooseMap();
    }

    @Override
    public void onDestroy(){

    }

    private void chooseMap(){
        LoadedGameMap randomMap = gameMaps.get(NHMGames.RANDOM.nextInt(gameMaps.size()));
        gameMap = createGameMap(randomMap);
    }

    protected abstract InstancedGameMap createGameMap(LoadedGameMap map);

    @SneakyThrows
    public void loadMaps(){
        File mapFolder = new File(String.format("maps/%s", gameID));
        if(!mapFolder.exists() || !mapFolder.isDirectory()){
            throw new IOException("Maps folder does not exist or is not a directory");
        }

        for(File mapFile : Objects.requireNonNull(mapFolder.listFiles())){
            //.map files are renamed schem files, but safeguard against my own laziness
            if(!mapFile.getName().endsWith(".map") && !mapFile.getName().endsWith(".schem")) continue;

            try{
                gameMaps.add(new LoadedGameMap(this, mapFile.getName().split("\\.")[0], SchematicLoader.readSchematic(mapFile)));
            }catch (Exception e){
                mainInstance.getLogger().warning("Error loading Map: " + mapFile.getAbsolutePath());
                continue;
            }

            mainInstance.getLogger().info("Loaded Map: " + mapFile.getAbsolutePath());
        }
    }

}
