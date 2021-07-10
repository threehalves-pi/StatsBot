package commands;

import events.Startup;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;

public class CommandsRegister {
    /**
     * Creates the global slash commands available in all servers and DMs
     *
     * @param commands the command list
     */
    public static void registerGlobalSlashCommands(CommandListUpdateAction commands) {
        List<CommandData> list = new ArrayList<>();

        list.add(new CommandData("statsbot", "Say hello to Stats Bot"));
        list.add(new CommandData("survey", "Get the AP Stats survey link"));
        list.add(new CommandData("help", "Get basic info on Stats Bot"));
        list.add(new CommandData("source", "See the bot's source code"));

        commands.addCommands(list).queue(
                s -> Startup.LOG.info("Pushed global slash commands.")
        );
    }

    /**
     * Creates the special private slash commands for the StatsBot Central Discord server. This includes permanent
     * admin-only commands and also commands currently under development that will soon be moved to global commands.
     *
     * @param commands the command list
     */
    public static void registerPrivateSlashCommands(CommandListUpdateAction commands) {
        List<CommandData> list = new ArrayList<>();

        list.add(new CommandData("panel", "Update the StatsBot control panel"));
        list.add(new CommandData("testing", "Slash command tester"));

        commands.addCommands(list).queue(
                s -> Startup.LOG.info("Pushed private slash commands.")
        );
    }
}
