package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.commands.AdminCommand;
import eu.macsworks.projectnhm.games.nhmGames.commands.LeaveCommand;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;

public class CommandsManager extends NHMManager {

    @Getter
    private PaperCommandManager<CommandSourceStack> commandManager;
    private AnnotationParser<CommandSourceStack> annotationParser;

    public CommandsManager(NHMGames mainInstance) {
        super(mainInstance);
    }

    @Override
    public void onInit() {
        commandManager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.<CommandSourceStack>builder().build())
                .buildOnEnable(getMainInstance());

        annotationParser = new AnnotationParser<>(commandManager, CommandSourceStack.class);

        annotationParser.parse(new AdminCommand(getMainInstance()));
        annotationParser.parse(new LeaveCommand(getMainInstance().getManager(GameManager.class),
                getMainInstance().getManager(RedisManager.class)));
    }
}