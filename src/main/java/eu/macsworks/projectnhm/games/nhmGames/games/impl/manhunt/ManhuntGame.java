package eu.macsworks.projectnhm.games.nhmGames.games.impl.manhunt;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameType;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import org.jetbrains.annotations.NotNull;

public class ManhuntGame extends NHMGame {

    public ManhuntGame(NHMGames mainInstance, GameType gameType) {
        super(gameType, mainInstance, "Manhunt", 2, 10);
    }

    @Override
    public void onInit(){
        super.onInit();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();


    }

    @Override
    protected InstancedGameMap createGameMap(LoadedGameMap map) {
        return null;
    }

    @Override
    protected @NotNull GameState createInProgressGameState() {
        return null;
    }
}
