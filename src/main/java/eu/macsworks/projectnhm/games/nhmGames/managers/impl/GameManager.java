package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.GameType;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public class GameManager extends NHMManager {

    private final Multimap<GameType, NHMGame> games = HashMultimap.create();

    public GameManager(NHMGames mainInstance) {
        super(mainInstance);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onDestroy() {

    }

    public void joinGame(UUID uuid, String gameID){
        Optional<NHMGame> game = getGameFromID(gameID);
        if(game.isEmpty()) return;

        game.get().addPlayer(uuid);
    }

    public Optional<NHMGame> getGameAt(Location location) {
        return games.values().stream()
                .filter(game -> game.getGameMap().contains(location))
                .findFirst();
    }

    public Optional<NHMGame> getGameFromID(String id) {
        return games.values().stream()
                .filter(game -> game.getGameID().equals(id))
                .findFirst();
    }
}
