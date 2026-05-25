package eu.macsworks.projectnhm.games.nhmGames.commands;

import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.RedisManager;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Commands;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class LeaveCommand {

    private final GameManager gameManager;
    private final RedisManager redisManager;

    @Commands({@Command("hub"), @Command("leave"), @Command("h"), @Command("lobby"), @Command("forfeit"), @Command("igiveup")})
    public void leave(CommandSourceStack source) {
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("Only players can run /leave."), true);
            return;
        }

        assert source.getPlayer() != null;

        UUID playerUUID = source.getPlayer().getUUID();
        Optional<NHMGame> game = gameManager.getGameForPlayer(playerUUID);
        game.ifPresent(nhmGame -> nhmGame.removePlayer(playerUUID));

        redisManager.getPlayerLobbyPubSub().sendToLobby(playerUUID);

        source.sendSuccess(() -> Component.literal("Leaving game..."), false);
    }
}