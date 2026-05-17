package eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.map.DeathRaceMap;
import org.jetbrains.annotations.NotNull;

public class DeathRaceGame extends NHMGame {

    private final NHMGames mainInstance;

    public DeathRaceGame(NHMGames mainInstance) {
        super(mainInstance, "DeathRace", 2, 2);

        this.mainInstance = mainInstance;
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
        return new DeathRaceMap(this, map);
    }

    @Override
    protected @NotNull GameState createInProgressGameState() {return null; }
}
