package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;

import java.time.Duration;

public class EndState<T extends NHMGame> extends GameState<T> {

    public EndState(T game) {
        super("End", game, Duration.ofSeconds(10), "Going back to lobby");
    }

    @Override
    public void onStart() {
        getGame().getPlayersUUIDs().forEach(uuid ->
                getMainInstance().getManager(RedisManager.class).getPlayerLobbyPubSub().sendToLobby(uuid));
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onEnd() {

    }
}
