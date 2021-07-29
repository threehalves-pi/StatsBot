package commands.slash;

import data.ID;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandsRegister {
    /**
     * This is the logger instance for slash command registration events.
     */
    private static final Logger LOG = JDALogger.getLog(CommandsRegister.class);

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
        list.add(new CommandData("faq", "Get a link to the AP Stats FAQ document"));

        commands.addCommands(list).queue(
                s -> LOG.info("Pushed global slash commands")
        );
    }

    /**
     * Creates the special private slash commands for the StatsBot Central Discord server. This includes permanent
     * admin-only commands and also commands currently under development that will soon be moved to global commands.
     *
     * @param guild the guild to update private commands for (currently must be {@link ID#STATSBOT_CENTRAL_GUILD})
     */
    public static void registerPrivateSlashCommands(@Nonnull Guild guild) {
        List<CommandData> list = new ArrayList<>();

        if (guild.getIdLong() == ID.STATSBOT_CENTRAL_GUILD) {
            // Public commands

            list.add(new CommandData("panel", "Update the StatsBot control panel"));

            list.add(new CommandData(
                    "diagram",
                    "see one of the preloaded AP Stats diagrams"));

            list.add(new CommandData("testing", "Slash command tester"));

            // Private commands with privileges

            list.add(new CommandData("announcement", "View and trigger announcement messages")
                    .addSubcommands(
                            new SubcommandData(
                                    "list",
                                    "List all announcement message IDs"),
                            new SubcommandData(
                                    "get",
                                    "View a specific announcement")
                                    .addOption(
                                            OptionType.INTEGER,
                                            "id",
                                            "The announcement id--see /announcement list " +
                                            "for a list of ids."),
                            new SubcommandData(
                                    "push",
                                    "Send an announcement to the stats channel")
                                    .addOption(
                                            OptionType.INTEGER,
                                            "id",
                                            "The announcement id--see /announcement " +
                                            "list for a list of ids.")
                    ).setDefaultEnabled(false)
            );

            guild.updateCommands().addCommands(list).queue(
                    commandList -> {
                        LOG.info("Pushed private slash commands");
                        setPrivateCommandPrivileges(commandList, guild);
                    }
            );
        }
    }

    /**
     * This sets the command privileges for private slash commands used in the StatsBot Central guild.
     */
    public static void setPrivateCommandPrivileges(@Nonnull List<Command> commands, @Nonnull Guild guild) {
        for (Command command : commands) {
            if (command.getName().equals("announcement"))
                command.updatePrivileges(guild, CommandPrivilege.enableUser(ID.SIMON)).queue();
        }
        LOG.info("Updated private command privileges");
    }
}
