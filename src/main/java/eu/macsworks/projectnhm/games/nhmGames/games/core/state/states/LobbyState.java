package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.markers.LobbyMarker;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;

public class LobbyState<T extends NHMGame> extends GameState<T> {

    public static long FULL_COUNTDOWN = 5L * 1000L;

    private final GameState<T> inProgressGameState;

    public LobbyState(T game, GameState<T> inProgressState) {
        super("Lobby", game, Duration.ofSeconds(60), "Match starting");

        this.inProgressGameState = inProgressState;
    }

    @Override
    public void onStart() {
        getGame().getPlayers().forEach(this::givePreMatchLoadout);
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

    private void givePreMatchLoadout(Player player) {
        //TODO: Give the player the necessary items
    }

    @Override
    public void onPlayerJoin(Player player){
        spawnPlayer(player, LobbyMarker.class);
    }

    @Override
    public void onEnd() {

    }

    @Override
    public boolean onBlockBreak(Player player, Block block) {
        return true;
    }

    @Override
    public boolean onBlockPlace(Player player, Block block) {
        return true;
    }

    @Override
    public boolean onEntityDamaged(Entity entity, EntityDamageEvent.DamageCause cause, double damageAmt){
        return true;
    }

    @Override
    public boolean onPlayerDamaged(Player player, EntityDamageEvent.DamageCause source, double damageAmt){
        return true;
    }
}
