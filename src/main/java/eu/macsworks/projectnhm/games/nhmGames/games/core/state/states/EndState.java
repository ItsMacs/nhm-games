package eu.macsworks.projectnhm.games.nhmGames.games.core.state.states;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.games.core.state.GameState;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EndState<T extends NHMGame> extends GameState<T> {

    private static final Color[] FIREWORK_COLORS = {
            Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE,
            Color.PURPLE, Color.ORANGE, Color.AQUA, Color.FUCHSIA
    };

    private long lastFireworkSecond = -1L;

    public EndState(T game) {
        super("End", game, Duration.ofSeconds(10), "Going back to lobby");
    }

    @Override
    public void onStart() {
        List<Player> winners = resolveWinners();
        Component announcement = Component.text(joinNames(winners) + (winners.size() == 1 ? " wins!" : " win!"));

        audience().forEach(p -> p.sendMessage(announcement));
        spawnFireworks(winners);
        lastFireworkSecond = 0L;
    }

    @Override
    public void onTick() {
        long secondsElapsed = elapsedTime().toSeconds();
        if (secondsElapsed == lastFireworkSecond) return;
        lastFireworkSecond = secondsElapsed;

        spawnFireworks(resolveWinners());
    }

    @Override
    public void onEnd() {
        Stream.concat(getGame().getPlayersUUIDs().stream(), getGame().getEliminatedPlayers().stream())
                .distinct()
                .forEach(uuid -> getMainInstance().getManager(RedisManager.class).getPlayerLobbyPubSub().sendToLobby(uuid));
    }

    private List<Player> resolveWinners() {
        return getGame().getWinners().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    private Stream<Player> audience() {
        return Stream.concat(getGame().getPlayersUUIDs().stream(), getGame().getEliminatedPlayers().stream())
                .distinct()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull);
    }

    private void spawnFireworks(List<Player> winners) {
        winners.forEach(p -> {
            Firework fw = p.getWorld().spawn(p.getLocation(), Firework.class);
            FireworkMeta meta = fw.getFireworkMeta();
            FireworkEffect.Type[] types = FireworkEffect.Type.values();
            meta.addEffect(FireworkEffect.builder()
                    .with(types[NHMGames.RANDOM.nextInt(types.length)])
                    .withColor(randomColor(), randomColor())
                    .withFade(Color.WHITE)
                    .withFlicker()
                    .withTrail()
                    .build());
            meta.setPower(1);
            fw.setFireworkMeta(meta);
        });
    }

    private static Color randomColor() {
        return FIREWORK_COLORS[NHMGames.RANDOM.nextInt(FIREWORK_COLORS.length)];
    }

    private static String joinNames(List<Player> winners) {
        return switch (winners.size()) {
            case 0 -> "Nobody";
            case 1 -> winners.get(0).getName();
            case 2 -> winners.get(0).getName() + " and " + winners.get(1).getName();
            default -> {
                String head = winners.subList(0, winners.size() - 1).stream()
                        .map(Player::getName)
                        .collect(Collectors.joining(", "));
                yield head + " and " + winners.getLast().getName();
            }
        };
    }

    @Override
    public boolean onBlockBreak(Player player, Block block) {
        return true;
    }

    @Override
    public boolean onBlockPlace(Player player, Block block) {
        return true;
    }

    @Override
    public boolean onEntityDamaged(Entity entity, EntityDamageEvent.DamageCause cause, double damageAmt){
        return true;
    }

    @Override
    public boolean onPlayerDamaged(Player player, EntityDamageEvent.DamageCause source, double damageAmt){
        return true;
    }
}