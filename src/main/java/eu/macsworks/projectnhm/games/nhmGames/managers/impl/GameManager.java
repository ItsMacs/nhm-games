package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameRegistry;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameType;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.DeathRaceGame;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt.ManhuntGame;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameManager extends NHMManager {

    @Getter
    private final GameRegistry gameRegistry = new GameRegistry();
    private final Multimap<NamespacedKey, NHMGame> games = HashMultimap.create();

    public GameManager(NHMGames mainInstance) {
        super(mainInstance);
    }

    @Override
    public void onInit() {
        registerGameTypes();
    }

    @Override
    public void onTick() {
        games.values().forEach(NHMGame::tick);
    }

    private void registerGameTypes() {
        NHMGames plugin = getMainInstance();
        gameRegistry.register(new GameType(new NamespacedKey(plugin, "death_race"), DeathRaceGame::new));
        gameRegistry.register(new GameType(new NamespacedKey(plugin, "manhunt"), ManhuntGame::new));
    }

    public NHMGame createGame(GameType gameType) {
        NHMGame game = gameType.create(getMainInstance());
        games.put(gameType.key(), game);

        game.init();
        return game;
    }

    public boolean destroyGame(NHMGame game) {
        boolean removed = games.remove(game.getGameType().key(), game);
        if (removed) game.destroy();
        return removed;
    }

    public void joinGame(UUID uuid, String gameID){
        Optional<NHMGame> game = getGameFromID(gameID);
        if(game.isEmpty()) return;

        game.get().addPlayer(uuid);
    }

    public List<NHMGame> getGames(){
        return new ArrayList<>(games.values());
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

    public Optional<NHMGame> getGameForPlayer(UUID uuid) {
        return games.values().stream()
                .filter(game -> game.isPlayerInGame(uuid))
                .findFirst();
    }
}