package eu.macsworks.projectnhm.games.nhmGames.games.core;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class GameListener implements Listener {

    private final NHMGame game;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event){
        if(!game.isPlayerInGame(event.getPlayer())) return;

        game.getGameState().onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if(!game.isPlayerInGame(event.getPlayer())) return;

        game.getGameState().onPlayerQuit(event.getPlayer());
    }



}
