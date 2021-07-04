package events;

import commands.CommandsRegister;
import main.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class Startup extends ListenerAdapter {
    private static final Logger LOG = JDALogger.getLog(Startup.class);
    /**
     * When bot starts up, locate necessary Discord entities
     *
     * @param event ready event data
     */
    public void onReady(@NotNull ReadyEvent event) {
        LOG.info("Processing startup tasks...");

        // Import config settings
        Setting.importSettings();

        // Set status
        Main.jda.getPresence().setStatus(Setting.STATUS);

        // Locate necessary Discord entities and print startup log
        printStartupLog();

        // Register Guild-specific slash commands
        CommandsRegister.registerAdminSlashCommands(Discord.STATSBOT_CENTRAL.updateCommands());

        LOG.info("Finished startup!");
    }

    private void printStartupLog() {
        Discord.AP_STUDENTS = Main.jda.getGuildById(ID.AP_STUDENTS_GUILD);
        Discord.STATSBOT_CENTRAL = Main.jda.getGuildById(ID.STATSBOT_CENTRAL_GUILD);

        // Channels
        boolean APStatsChannel = false;
        boolean botCommandsChannel = false;
        boolean APStatsStatsBotChannel = false;
        boolean privateTestingChannel = false;


        if (Discord.AP_STUDENTS != null) {
            APStatsChannel = Discord.AP_STUDENTS.getTextChannelById(ID.AP_STATS_CHANNEL) != null;
            botCommandsChannel = Discord.AP_STUDENTS.getTextChannelById(ID.BOT_COMMANDS_CHANNEL) != null;
        }

        if (Discord.STATSBOT_CENTRAL != null) {
            APStatsStatsBotChannel = Discord.STATSBOT_CENTRAL.getTextChannelById(ID.AP_STATS_STATSBOT_CHANNEL) != null;
            privateTestingChannel = Discord.STATSBOT_CENTRAL.getTextChannelById(ID.PRIVATE_TESTING_CHANNEL) != null;
            Discord.STARTUP_LOG = Discord.STATSBOT_CENTRAL.getTextChannelById(ID.STARTUP_LOG_CHANNEL);
        }

        Discord.SIMON = Main.jda.retrieveUserById(ID.SIMON).complete();

        Main.jda.getSelfUser();
        EmbedBuilder embed = Utils.buildEmbed(
                "Startup Log",
                "",
                Colors.ADMIN,
                new MessageEmbed.Field("AP Students",
                        getCheckLine("Server", Discord.AP_STUDENTS != null) + "\n" +
                                getCheckLine("#apstats channel", APStatsChannel) + "\n" +
                                getCheckLine("#bot-commands channel", botCommandsChannel), false),
                new MessageEmbed.Field("StatsBot Central",
                        getCheckLine("Server", Discord.STATSBOT_CENTRAL != null) + "\n" +
                                getCheckLine("#ap-stats channel", APStatsStatsBotChannel) + "\n" +
                                getCheckLine("#startup-log", Discord.STARTUP_LOG != null) + "\n" +
                                getCheckLine("#private-testing channel", privateTestingChannel), false),
                new MessageEmbed.Field("Users",
                        getCheckLine("Myself", true) + "\n" +
                                getCheckLine("Simo\u03c0", Discord.SIMON != null),
                        false),
                new MessageEmbed.Field("Settings",
                        getCheckLine("Prefix", !Setting.PREFIX.equals("")) + "\n" +
                        getCheckLine("Status", Setting.STATUS != OnlineStatus.UNKNOWN), false)
        );

        if (Discord.STARTUP_LOG == null)
            throw new NullPointerException("Unable to locate #startup-log to send startup message.");

        Discord.STARTUP_LOG.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Returns a String with the name of a Discord entity and an emoji signifying whether it is working. This is used to
     * build the startup log.
     *
     * @param entity    the name of the Discord entity
     * @param isWorking true if the entity is working; false otherwise
     * @return a line with the entity name and the associated emoji
     */
    private String getCheckLine(String entity, boolean isWorking) {
        return (isWorking ? Discord.CHECK : Discord.RED_X) + " " + entity;
    }
}
