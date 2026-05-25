package eu.macsworks.projectnhm.games.nhmGames.games.core;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.LobbyState;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import eu.macsworks.projectnhm.games.nhmGames.utils.SchematicLoader;
import lombok.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
@ToString
public abstract class NHMGame implements NHMLifecycledObject {

    private final GameType gameType;

    private final NHMGames mainInstance;
    private final String gameID;
    private final int minPlayers, maxPlayers;

    @Getter(AccessLevel.PRIVATE)
    private final List<LoadedGameMap> gameMaps = new ArrayList<>();

    private InstancedGameMap gameMap;
    private GameState gameState;

    private final List<UUID> players = new ArrayList<>();

    public NHMGame(GameType type, NHMGames mainInstance, String gameID, int minPlayers, int maxPlayers) {
        this.gameType = type;
        this.mainInstance = mainInstance;
        this.gameID = gameID;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void onInit(){
        Bukkit.getPluginManager().registerEvents(new GameListener(this), mainInstance);

        loadMaps();

        //TODO: Make selection a VIP perk
        chooseMap();

        setGameState(new LobbyState(this, createInProgressGameState()));
    }

    @Override
    public void onDestroy(){
        getPlayersUUIDs().forEach(uuid -> getMainInstance().getManager(RedisManager.class).getPlayerLobbyPubSub().sendToLobby(uuid));
    }

    private void chooseMap(){
        LoadedGameMap randomMap = gameMaps.get(NHMGames.RANDOM.nextInt(gameMaps.size()));
        gameMap = createGameMap(randomMap);
    }

    protected abstract InstancedGameMap createGameMap(LoadedGameMap map);
    protected abstract @NotNull GameState createInProgressGameState();

    public void win(List<Player> winners){

    }

    public void eliminate(List<Player> eliminated){
        players.removeAll(eliminated.stream().map(Player::getUniqueId).toList());

        eliminated.forEach(p -> broadcast(Component.text("Player " + p.getName() + " has been eliminated!")));
    }

    public void tick(){
        if(gameState == null) return;

        gameState.tick();
    }

    public void setGameState(GameState newState){
        if(gameState != null){
            gameState.end();
        }

        gameState = newState;
        gameState.start();
    }

    public void broadcast(Component component){
        getPlayers().forEach(player -> player.sendMessage(component));
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

    public List<UUID> getPlayersUUIDs(){
        return Collections.unmodifiableList(players);
    }

    public void addPlayer(UUID uuid){
        players.add(uuid);
    }

    public void removePlayer(UUID uuid){
        players.remove(uuid);
    }

    public boolean isPlayerInGame(Player player){
        return players.contains(player.getUniqueId());
    }

    public boolean isPlayerInGame(UUID uuid){
        return players.contains(uuid);
    }
}
