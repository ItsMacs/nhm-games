package eu.macsworks.projectnhm.games.nhmGames.games.core.state;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
public abstract class GameState {

    private final String name;
    private final NHMGame game;
    private final long duration;

    public GameState(String name, NHMGame game) {
        this(name, game, Long.MAX_VALUE);
    }

    public GameState(String name, NHMGame game, long duration) {
        this.name = name;
        this.game = game;
        this.duration = duration;
    }

    private final long startEpoch = System.currentTimeMillis();

    public abstract void onStart();
    public abstract void onTick();
    public abstract void onEnd();

    /**
     * @return Returns the elapsed time, in milliseconds, since the state start.
     */
    public long elapsedTime(){
        return System.currentTimeMillis() - startEpoch;
    }

    public void onPlayerJoin(Player player){}
    public void onPlayerQuit(Player player){}

}
