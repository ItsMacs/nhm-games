package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;

import java.time.Duration;

public class InProgressState<T extends NHMGame> extends GameState<T> {

    public InProgressState(String name, T game) {
        super(name, game, "Match ending");
    }

    public InProgressState(String name, T game, String endingBC) {
        super(name, game, endingBC);
    }

    public InProgressState(String name, T game, Duration timeout, String endingBC) {
        super(name, game, timeout, endingBC);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTick() {
        if(getGame().getPlayersUUIDs().isEmpty()){
            getMainInstance().getManager(GameManager.class).destroyGame(getGame());
            return;
        }

        if(getGame().getPlayersUUIDs().size() == 1){
            getGame().win(getGame().getPlayers());
            return;
        }
    }

    @Override
    public void onEnd() {

    }
}
