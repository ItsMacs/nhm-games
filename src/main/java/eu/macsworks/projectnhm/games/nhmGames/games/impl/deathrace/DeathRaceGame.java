package eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameType;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.InstancedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.maps.LoadedGameMap;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.map.DeathRaceMap;
import eu.macsworks.projectnhm.games.nhmGames.games.impl.deathrace.states.DeathRaceSelectingCauseState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class DeathRaceGame extends NHMGame {

    @Getter @Setter
    private EntityDamageEvent.DamageCause damageType;

    public DeathRaceGame(NHMGames mainInstance, GameType gameType) {
        super(gameType, mainInstance, "DeathRace", 2, 2);
    }

    @Override
    protected InstancedGameMap createGameMap(LoadedGameMap map) {
        return new DeathRaceMap(this, map);
    }

    @Override
    protected @NotNull GameState<DeathRaceGame> createInProgressGameState() {return new DeathRaceSelectingCauseState(this); }
}
