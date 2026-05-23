package eu.macsworks.projectnhm.games.nhmGames.games.core;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import org.bukkit.NamespacedKey;

import java.util.function.BiFunction;

public record GameType(NamespacedKey key, BiFunction<NHMGames, GameType, NHMGame> factory) {

    public NHMGame create(NHMGames plugin) {
        return factory.apply(plugin, this);
    }
}