package eu.macsworks.projectnhm.games.nhmGames.games.core.state;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.utils.DurationUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;

@Getter
public abstract class GameState {

    private final String name;
    private final NHMGame game;
    private final Duration duration;

    //String that's broadcasted as part of the states automatic "XXXX in Y seconds!" broadcast
    private final String broadcastAction;

    public GameState(String name, NHMGame game, String broadcastAction) {
        this(name, game, Duration.ofDays(Long.MAX_VALUE), broadcastAction);
    }

    public GameState(String name, NHMGame game, Duration duration, String broadcastAction) {
        this.name = name;
        this.game = game;
        this.duration = duration;
        this.broadcastAction = broadcastAction;
    }

    //Setter here to allow pushback/pushforwards of the builtin timer
    @Setter
    private long startEpoch = System.currentTimeMillis();

    public void start(){

    }

    public void tick(){
        //Get the current countdown from the start (on 0s from the start = duration, on duration = 0)
        long countdownCurrentTime = duration.toMillis() - remainingTime().toMillis();

        if(countdownCurrentTime > 3 && countdownCurrentTime != duration.toMillis() / 2) return;
        getGame().broadcast(Component.text(String.format("%s in %s!", broadcastAction, DurationUtils.format(countdownCurrentTime))));
    }

    public void end(){

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
        return elapsedTime().compareTo(remainingTime()) > 0;
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

}
