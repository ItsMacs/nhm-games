package eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameType;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt.map.ManhuntMap;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt.states.ManhuntInProgressState;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class ManhuntGame extends NHMGame {

    @Getter
    private final NamespacedKey huntedGameKey;

    public ManhuntGame(NHMGames mainInstance, GameType gameType) {
        super(gameType, mainInstance, "Manhunt", 2, 10);

        huntedGameKey = new NamespacedKey(mainInstance, "manhunt-" + getGameID() + "-hunted");
    }

    @Override
    protected InstancedGameMap createGameMap(LoadedGameMap map) {
        return new ManhuntMap(this, map);
    }

    @Override
    protected @NotNull GameState<ManhuntGame> createInProgressGameState() {
        return new ManhuntInProgressState(this);
    }
}
