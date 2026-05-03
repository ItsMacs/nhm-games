package eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.map;

import eu.macsworks.projectnhm.games.nhmGames.games.core.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.map.markers.TestMarker;

public class DeathRaceMap extends InstancedGameMap {

    public DeathRaceMap(NHMGame game, LoadedGameMap loadedGameMap) {
        super(game, loadedGameMap);
    }

    @Override
    protected void registerMarkers() {
        registerMarker(new TestMarker());
    }
}
