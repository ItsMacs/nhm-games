package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;

import java.time.Duration;

public class EndState extends GameState {

    public EndState(NHMGame game) {
        super("End", game, Duration.ofSeconds(10), "Going back to lobby");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTick() {
        if(!isStateFinished()) return;

        //TODO: Send players back to lobby
    }

    @Override
    public void onEnd() {

    }
}
