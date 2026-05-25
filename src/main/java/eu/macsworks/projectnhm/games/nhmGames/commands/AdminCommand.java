package eu.macsworks.projectnhm.games.nhmGames.commands;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.games.core.GameType;
import eu.macsworks.projectnhm.games.nhmGames.games.core.NHMGame;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.NamespacedKey;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.Optional;

@RequiredArgsConstructor
public class AdminCommand {

    private final NHMGames mainInstance;

    @Command("nhm create <gameType>")
    @Permission("nhm.admin.create")
    public void createGame(CommandSourceStack sender, @Argument("gameType") String gameTypeKey) {
        GameManager gameManager = mainInstance.getManager(GameManager.class);
        NamespacedKey key = new NamespacedKey(mainInstance, gameTypeKey);

        Optional<GameType> type = gameManager.getGameRegistry().get(key);
        if (type.isEmpty()) {
            sender.sendFailure(Component.literal("Unknown game type: " + gameTypeKey));
            return;
        }

        NHMGame game = gameManager.createGame(type.get());
        sender.sendSuccess(() -> Component.literal("Created game " + game.getGameID() + " (" + type.get().key() + ")"), false);
    }

    @Command("nhm destroy <gameId>")
    @Permission("nhm.admin.destroy")
    public void destroyGame(CommandSourceStack sender, @Argument("gameId") String gameId) {
        GameManager gameManager = mainInstance.getManager(GameManager.class);

        Optional<NHMGame> game = gameManager.getGameFromID(gameId);
        if (game.isEmpty()) {
            sender.sendFailure(Component.literal("No game with ID: " + gameId));
            return;
        }

        gameManager.destroyGame(game.get());
        sender.sendSuccess(() -> Component.literal("Destroyed game " + gameId), false);
    }
}