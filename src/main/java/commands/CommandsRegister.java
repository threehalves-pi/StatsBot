package commands;

import events.Startup;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class CommandsRegister {
    /**
     * Creates the global slash commands available in all servers and DMs
     *
     * @param commands the command list
     */
    public static void registerGlobalSlashCommands(CommandListUpdateAction commands) {
        commands.addCommands(
                new CommandData("statsbot", "Say hello to Stats Bot"),
                new CommandData("survey", "Get the AP Stats survey link"),
                new CommandData("help", "Get basic info on Stats Bot"),
                new CommandData("source", "See the bot's source code")
        ).queue(
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
        commands.addCommands(
                new CommandData("panel", "Update the StatsBot control panel")
                //new CommandData("help", "The generic help command for Stats Bot"),
                //new CommandData("source", "See the source code for Stats Bot")
        ).queue(
                s -> Startup.LOG.info("Pushed private slash commands.")
        );
    }
}
