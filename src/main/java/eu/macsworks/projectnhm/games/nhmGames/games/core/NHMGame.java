package eu.macsworks.projectnhm.games.nhmGames.games.core;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.LobbyState;
import eu.macsworks.projectnhm.games.nhmGames.utils.SchematicLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public abstract class NHMGame implements NHMLifecycledObject {

    private final NHMGames mainInstance;
    private final String gameID;
    private final int minPlayers, maxPlayers;

    @Getter(AccessLevel.PRIVATE)
    private final List<LoadedGameMap> gameMaps = new ArrayList<>();

    private InstancedGameMap gameMap;
    private GameState gameState;

    private final List<UUID> players = new ArrayList<>();

    @Override
    public void onInit(){
        loadMaps();

        //TODO: Make selection a VIP perk
        chooseMap();

        setGameState(new LobbyState(this, createInProgressGameState()));
    }

    @Override
    public void onDestroy(){

    }

    private void chooseMap(){
        LoadedGameMap randomMap = gameMaps.get(NHMGames.RANDOM.nextInt(gameMaps.size()));
        gameMap = createGameMap(randomMap);
    }

    protected abstract InstancedGameMap createGameMap(LoadedGameMap map);
    protected abstract @NotNull GameState createInProgressGameState();

    public void tick(){
        if(gameState == null) return;

        gameState.onTick();
    }

    public void setGameState(GameState newState){
        if(gameState != null){
            gameState.onEnd();
        }

        gameState = newState;
        gameState.onStart();
    }

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

    /**
     * @return Returns a list of the currently online players that are in the game.
     */
    public List<Player> getPlayers(){
        return players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).toList();
    }
}
