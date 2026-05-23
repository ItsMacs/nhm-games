package eu.macsworks.projectnhm.games.nhmGames.games.core;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class GameRegistry implements Iterable<GameType> {

    private final Map<NamespacedKey, GameType> entries = new LinkedHashMap<>();

    public void register(GameType type) {
        if (entries.containsKey(type.key())) throw new IllegalStateException("Game type already registered: " + type.key());

        entries.put(type.key(), type);
    }

    public Optional<GameType> get(NamespacedKey key) {
        return Optional.ofNullable(entries.get(key));
    }

    public GameType getOrThrow(NamespacedKey key) {
        return get(key).orElseThrow(() -> new NoSuchElementException("No registered game type: " + key));
    }

    public Collection<GameType> values() {
        return Collections.unmodifiableCollection(entries.values());
    }

    @Override
    public @NonNull Iterator<GameType> iterator() {
        return values().iterator();
    }
}