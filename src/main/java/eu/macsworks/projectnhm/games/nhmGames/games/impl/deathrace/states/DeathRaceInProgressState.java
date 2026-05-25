package eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.InProgressState;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.DeathRaceGame;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.List;

public class DeathRaceInProgressState extends InProgressState<DeathRaceGame> {

    public DeathRaceInProgressState(DeathRaceGame game) {
        super("DeathRace-InProgressState", game);
    }

    //The whole premise of the game lies here, behold!
    //A boolean value.
    @Override
    public boolean onPlayerDamaged(Player player, EntityDamageEvent.DamageCause source, double damageAmt){
        return source != getGame().getDamageType();
    }

    @Override
    public boolean onPlayerDeath(Player player){
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        getGame().win(List.of(player));
        return true;
    }

    @Override
    public void onPlayerQuit(Player player){
        getGame().eliminate(List.of(player));
    }
}
