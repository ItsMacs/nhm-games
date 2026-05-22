package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.markers.SpawnMarker;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

public class LobbyState extends GameState {

    public static long FULL_COUNTDOWN = 5L * 1000L;

    private final GameState inProgressGameState;

    //Holds the spawn marker used and the player(s) spawning there. Will start piling up players on the same markers if there's
    //not enough of them.
    private final Multimap<InstancedGameMap.InstancedMarker<SpawnMarker>, Player> playerSpawnMarkers = MultimapBuilder.hashKeys().arrayListValues().build();

    public LobbyState(NHMGame game, GameState inProgressState) {
        super("Lobby", game, Duration.ofSeconds(60), "Match starting");

        this.inProgressGameState = inProgressState;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTick() {
        if(isStateFinished()) {
            //Not enough players have joined, don't start the gamemode
            if(getGame().getPlayers().size() < getGame().getMinPlayers()){
                setStartEpoch(System.currentTimeMillis());
                return;
            }

            getGame().setGameState(inProgressGameState);
            return;
        }

        //We have a full lobby, if we haven't done so already set the time to the countdown
        if(getGame().getPlayers().size() >= getGame().getMaxPlayers() && System.currentTimeMillis() - getStartEpoch() > FULL_COUNTDOWN){
            setStartEpoch(getStartEpoch() + (getDuration().toMillis() - FULL_COUNTDOWN));
        }
    }

    @Override
    public void onPlayerJoin(Player player){
        spawnPlayer(player);
    }

    private void spawnPlayer(Player player){
        Optional<InstancedGameMap.InstancedMarker<SpawnMarker>> firstEmptyMarker = getGame().getGameMap()
                .getMarkersOfType(SpawnMarker.class)
                .stream()
                .filter(marker -> !playerSpawnMarkers.containsKey(marker))
                .findFirst();

        InstancedGameMap.InstancedMarker<SpawnMarker> playerMarker;

        //Find the first marker in the map that has the lesser amount of players in there and chuck them onto there
        if (firstEmptyMarker.isEmpty()){
            playerMarker = playerSpawnMarkers.keySet()
                    .stream()
                    .min(Comparator.comparingInt(marker -> playerSpawnMarkers.get(marker).size()))
                    .orElseThrow(() -> new IllegalStateException("No spawn markers found on map " + getGame().getGameMap().getId()));

            playerSpawnMarkers.put(playerMarker, player);
        }else{
            //Phew, we have an empty marker. Spawn the bad boy on there. (or bad girl, or bad... person?)
            playerSpawnMarkers.put(firstEmptyMarker.get(), player);

            playerMarker = firstEmptyMarker.get();
        }

        player.teleportAsync(playerMarker.getLocation());
    }

    @Override
    public void onEnd() {

    }
}
