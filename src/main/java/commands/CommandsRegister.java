package commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

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
     * @param commands the command list
     */
    public static void registerPrivateSlashCommands(CommandListUpdateAction commands) {
        List<CommandData> list = new ArrayList<>();

        list.add(new CommandData("panel", "Update the StatsBot control panel"));

        list.add(new CommandData("announcement", "View and trigger announcement messages")
                .addSubcommands(
                        new SubcommandData("list", "List all announcement message IDs"),
                        new SubcommandData("get", "View a specific announcement")
                                .addOption(
                                        OptionType.INTEGER,
                                        "id",
                                        "The announcement id--see /announcement list for a list of ids."),
                        new SubcommandData("push", "Send an announcement to the stats channel")
                                .addOption(
                                        OptionType.INTEGER,
                                        "id",
                                        "The announcement id--see /announcement list for a list of ids.")
                )
        );

        list.add(new CommandData("testing", "Slash command tester"));

        commands.addCommands(list).queue(
                s -> LOG.info("Pushed private slash commands")
        );
    }

    /**
     * This sets the command privileges for global slash commands used in the AP Students Discord server.
     */
    public static void setCommandPrivileges() {
        /*Guild guild = Main.JDA.getGuildById(ID.AP_STUDENTS_GUILD);

        if (guild == null) {
            LOG.error("Failed to get AP Students Guild for setting slash command privileges");
            return;
        }


        Map<String, List<CommandPrivilege>> map = guild.retrieveCommandPrivileges().complete();

        System.out.println(Arrays.toString(map.keySet().toArray()));*/
    }
}
