package eu.macsworks.projectnhm.games.nhmGames.games.core;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public class GameListener implements Listener {

    private final NHMGame game;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!game.isPlayerInGame(event.getPlayer())) return;

        game.getGameState().onPlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        if(!game.isPlayerInGame(event.getPlayer())) return;

        game.getGameState().onPlayerQuit(event.getPlayer());
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(!game.isPlayerInGame(event.getPlayer())) return;

        game.getGameState().onPlayerRespawn(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player player)) return;

        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        event.setCancelled(game.getGameState().onPlayerDamaged(player, event.getCause(), event.getFinalDamage()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player player)) return;
        if(!(event.getDamager() instanceof Player attacker)){
            event.setCancelled(game.getGameState().onPlayerDamagedByEntity(player, event.getDamager(), event.getFinalDamage()));
            return;
        }

        event.setCancelled(game.getGameState().onPlayerDamagedByPlayer(player, attacker, event.getFinalDamage()));
    }

}
