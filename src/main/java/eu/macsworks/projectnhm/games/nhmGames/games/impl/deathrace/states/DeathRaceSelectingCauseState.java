package eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.states;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.markers.DefaultSpawnMarker;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.InProgressState;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.DeathRaceGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;

public class DeathRaceSelectingCauseState extends InProgressState<DeathRaceGame> {

    public DeathRaceSelectingCauseState(DeathRaceGame game) {
        super("DeathRace-DecidingSourceState", game, Duration.ofSeconds(5), null);
    }

    @Override
    public void onStart() {
        getGame().setDamageType(EntityDamageEvent.DamageCause.values()[NHMGames.RANDOM.nextInt(EntityDamageEvent.DamageCause.values().length)]);
    }

    @Override
    public void onTick() {
        //Roll animation
        EntityDamageEvent.DamageCause randomCause = EntityDamageEvent.DamageCause.values()[NHMGames.RANDOM.nextInt(EntityDamageEvent.DamageCause.values().length)];

        Title title = Title.title(Component.text(randomCause.name()), Component.text(
                "Choosing death type..."),
                Title.Times.times(Duration.ofSeconds(0), Duration.ofMillis(500), Duration.ofMillis(0)));

        getGame().getPlayers().forEach(player -> {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
        });

        if(remainingTime().isNegative()){
            getGame().setGameState(new DeathRaceInProgressState(getGame()));
        }
    }

    @Override
    public void onEnd() {
        //drop le players
        //Display le chosen damagetype
        Title title = Title.title(Component.text(getGame().getDamageType().name()), Component.text(
                "Die for " + getGame().getDamageType().name()),
                        Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofMillis(500)));

        getGame().getPlayers().forEach(player -> {
            spawnPlayer(player, DefaultSpawnMarker.class);

            player.showTitle(title);
        });
    }
}
