package eu.macsworks.projectnhm.games.nhmGames.managers;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class NHMManager implements NHMLifecycledObject {

    private final NHMGames mainInstance;

    public void onTick(){}

}
