package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;

public class EndState extends GameState {

    public static long DURATION = 10 * 1000L;

    public EndState(NHMGame game) {
        super("End", game);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTick() {
        if(elapsedTime() < DURATION) return;

        //TODO: Send players back to lobby

    }

    @Override
    public void onEnd() {

    }
}
