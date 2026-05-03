package eu.macsworks.projectnhm.games.nhmGames.managers;

import eu.macsworks.projectnhm.games.nhmGames.api.NHMLifecycledObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class NHMManager implements NHMLifecycledObject {

    private final ManagerType managerType;

    public enum ManagerType{
        GAME,
        WORLD;
    }
}
