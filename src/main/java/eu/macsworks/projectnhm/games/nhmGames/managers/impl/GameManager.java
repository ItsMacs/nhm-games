package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.GameType;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import org.bukkit.Location;

import java.util.Optional;

public class GameManager extends NHMManager {

    private final Multimap<GameType, NHMGame> games = HashMultimap.create();

    public GameManager() {
        super(ManagerType.GAME);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onDestroy() {

    }

    public Optional<NHMGame> getGameAt(Location location) {
        return games.values().stream()
                .filter(game -> game.getGameMap().contains(location))
                .findFirst();
    }
}
