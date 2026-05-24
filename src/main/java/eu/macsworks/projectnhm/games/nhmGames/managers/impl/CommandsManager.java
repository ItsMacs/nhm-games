package eu.macsworks.projectnhm.games.nhmGames.managers.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.commands.AdminCommand;
import eu.macsworks.projectnhm.games.nhmGames.managers.NHMManager;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class CommandsManager extends NHMManager {

    @Getter
    private LegacyPaperCommandManager<CommandSender> commandManager;
    private AnnotationParser<CommandSender> annotationParser;

    public CommandsManager(NHMGames mainInstance) {
        super(mainInstance);
    }

    @Override
    public void onInit() {
        commandManager = LegacyPaperCommandManager.createNative(
                getMainInstance(),
                ExecutionCoordinator.simpleCoordinator()
        );

        annotationParser = new AnnotationParser<>(commandManager, CommandSender.class);

        annotationParser.parse(new AdminCommand(getMainInstance()));
    }
}