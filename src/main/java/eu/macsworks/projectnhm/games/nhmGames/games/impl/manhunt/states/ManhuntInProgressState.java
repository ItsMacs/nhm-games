package eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt.states;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.markers.DefaultSpawnMarker;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.states.InProgressState;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt.ManhuntGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.List;

public class ManhuntInProgressState extends InProgressState<ManhuntGame> {
    public ManhuntInProgressState(ManhuntGame game) {
        super("Manhunt-InProgressState", game, Duration.ofMinutes(5), "Hunted will win");
    }

    @Override
    public void onStart(){
        Player hunted = getGame().getPlayers().get(NHMGames.RANDOM.nextInt(getGame().getPlayers().size()));
        hunted.getPersistentDataContainer().set(getGame().getHuntedGameKey(), PersistentDataType.BOOLEAN, true);

        Title hunterTitle = Title.title(Component.text("HUNTER"), Component.text("Kill the hunted!"));
        Title huntedTitle = Title.title(Component.text("HUNTED"), Component.text("Survive the hunters!"));
        getGame().getPlayers().forEach(p -> {
            spawnPlayer(p, DefaultSpawnMarker.class);

            p.showTitle(hunted.getUniqueId().equals(p.getUniqueId()) ? huntedTitle : hunterTitle);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        });
    }

    @Override
    public void onTick(){
        if(remainingTime().isPositive()) return;

        getGame().win(getGame().getPlayers().stream().filter(p -> p.getPersistentDataContainer().has(getGame().getHuntedGameKey())).toList());
    }

    @Override
    public void onEnd(){
        getGame().getPlayers().forEach(p -> {
            p.getPersistentDataContainer().remove(getGame().getHuntedGameKey());
        });
    }

    @Override
    public void onPlayerQuit(Player player) {
        if(!player.getPersistentDataContainer().has(getGame().getHuntedGameKey())) return;

        getGame().eliminate(List.of(player));
        getGame().win(getGame().getPlayers());
    }


    @Override
    public boolean onPlayerDeath(Player player){
        if(!player.getPersistentDataContainer().has(getGame().getHuntedGameKey())){
            player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
            spawnPlayer(player, DefaultSpawnMarker.class);
            return true;
        }
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        getGame().eliminate(List.of(player));
        getGame().win(getGame().getPlayers());
        return true;
    }
}
