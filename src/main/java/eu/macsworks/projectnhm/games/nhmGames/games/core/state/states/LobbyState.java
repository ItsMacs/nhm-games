package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;

public class LobbyState extends GameState {

    public static long DURATION = 60L * 1000L;
    private final GameState inProgressGameState;

    public LobbyState(NHMGame game, GameState inProgressState) {
        super("Lobby", game, DURATION);

        this.inProgressGameState = inProgressState;
    }

    @Override
    public void onStart() {
        //TODO: Teleport players to lobby in some way.
        //TODO: There aren't gonna be any players online when this fires.
    }

    @Override
    public void onTick() {
        //TODO: Automatically push back the 60s time if it fails, 10s on full lobbys
        if(elapsedTime() >= DURATION){
            //Not enough players have joined, don't start the gamemode
            if(getGame().getPlayers().size() < getGame().getMinPlayers()) return;

            getGame().setGameState(inProgressGameState);
            return;
        }
    }

    @Override
    public void onEnd() {

    }
}
