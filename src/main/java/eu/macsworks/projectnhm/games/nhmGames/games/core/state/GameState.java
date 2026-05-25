package eu.macsworks.projectnhm.games.nhmGames.games.core.state;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.utils.DurationUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

@Getter
public abstract class GameState<T extends NHMGame> {

    private final NHMGames mainInstance;

    private final String name;
    private final T game;
    private final Duration duration;

    //String that's broadcasted as part of the states automatic "XXXX in Y seconds!" broadcast
    private final String broadcastAction;

    //Holds the spawn marker used and the player(s) spawning there. Will start piling up players on the same markers if there's
    //not enough of them.
    private final Multimap<InstancedGameMap.InstancedMarker<? extends InstancedGameMap.Marker>, Player> playerSpawnMarkers = MultimapBuilder.hashKeys().arrayListValues().build();

    public GameState(String name, T game, String broadcastAction) {
        this(name, game, Duration.ofDays(100L), broadcastAction);
    }

    public GameState(String name, T game, Duration duration, String broadcastAction) {
        this.name = name;
        this.game = game;
        this.duration = duration;
        this.broadcastAction = broadcastAction;

        mainInstance = NHMGames.getInstance();
    }

    //Setter here to allow pushback/pushforwards of the builtin timer
    @Setter
    private long startEpoch = System.currentTimeMillis();

    public void start(){
        onStart();
    }

    public void tick(){
        onTick();

        if(broadcastAction == null) return;

        //Get the current countdown from the start (on 0s from the start = duration, on duration = 0)
        long remainingMs = remainingTime().toMillis();

        boolean inLastThreeSeconds = remainingMs <= 3000L;
        boolean atHalfway = Math.abs(remainingMs - duration.toMillis() / 2) < 1000L;

        if (!inLastThreeSeconds && !atHalfway) return;

        getGame().broadcast(Component.text(String.format("%s in %s!", broadcastAction, DurationUtils.format(remainingTime()))));
    }

    public void end(){
        onEnd();
    }


    public abstract void onStart();
    public abstract void onTick();
    public abstract void onEnd();

    /**
     * @return Returns the elapsed time, in milliseconds, since the state start.
     */
    public Duration elapsedTime(){
        return Duration.ofMillis(System.currentTimeMillis() - startEpoch);
    }
    public Duration remainingTime() { return Duration.ofMillis(duration.toMillis() - elapsedTime().toMillis()); }

    public boolean isStateFinished() {
        return !remainingTime().isPositive();
    }

    protected <J extends InstancedGameMap.Marker> void spawnPlayer(Player player, Class<J> spawnMarkerClass){
        Optional<InstancedGameMap.InstancedMarker<J>> firstEmptyMarker = getGame().getGameMap()
                .getMarkersOfType(spawnMarkerClass)
                .stream()
                .filter(marker -> !playerSpawnMarkers.containsKey(marker))
                .findFirst();

        InstancedGameMap.InstancedMarker<? extends InstancedGameMap.Marker> playerMarker;

        //Find the first marker in the map that has the lesser amount of players in there and chuck them onto there
        if (firstEmptyMarker.isEmpty()){
            playerMarker = playerSpawnMarkers.keySet()
                    .stream()
                    .filter(marker -> spawnMarkerClass.isInstance(marker.marker()))
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

    //Default impl, place the sucker at the center of the map in spectator mode
    public void onPlayerJoin(Player player){
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(game.getGameMap().getCenter());
    }

    public void onPlayerQuit(Player player){}
    public void onPlayerRespawn(Player player){}

    //Boolean-based hooks: return value is whether the event shall be canceled or not
    public boolean onPlayerDamaged(Player player, EntityDamageEvent.DamageCause source, double damageAmt){ return false; }
    public boolean onPlayerDamagedByEntity(Player player, Entity entity, double damageAmt){ return false; }
    public boolean onPlayerDamagedByPlayer(Player player, Player damager, double damageAmt){ return false; }

    public boolean onPlayerDeath(Player player){ return false; }

}
