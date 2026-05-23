package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;

public class InProgressState extends GameState {

    public InProgressState(String name, NHMGame game) {
        super(name, game, "Match ending");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTick() {

    }

    @Override
    public void onEnd() {

    }
}
