package events;

import commands.GenericCommands;
import data.*;
import announcements.AnnouncementLoader;
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

import java.util.Objects;

public class Startup extends ListenerAdapter {
    public static final Logger LOG = JDALogger.getLog(Startup.class);

    /**
     * When bot starts up, locate necessary Discord entities
     *
     * @param event ready event data
     */
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println();
        LOG.info("Processing startup tasks...");

        // Import config settings
        Setting.importSettings();

        // Set status
        Main.JDA.getPresence().setStatus(Setting.STATUS);

        // Locate necessary Discord entities and print startup log
        printStartupLog();

        // Register global slash commands if enabled in settings (and bot mode includes global slash commands)
        if (Main.MODE.allows(BotMode.Mode.GLOBAL_SLASH_COMMANDS) && Setting.LOAD_COMMANDS_GLOBAL)
            CommandsRegister.registerGlobalSlashCommands(Main.JDA.updateCommands());

        // Register private and testing slash commands if enabled in settings (and bot mode includes private commands)
        if (Main.MODE.allows(BotMode.Mode.PRIVATE_SLASH_COMMANDS) && Setting.LOAD_COMMANDS_TESTING)
            CommandsRegister.registerPrivateSlashCommands(Discord.STATSBOT_CENTRAL.updateCommands());

        // Set global slash command permissions if enabled in settings (and bot mode includes global slash commands)
        if (Main.MODE.allows(BotMode.Mode.GLOBAL_SLASH_COMMANDS) && Setting.LOAD_GLOBAL_PRIVILEGES)
            CommandsRegister.setCommandPrivileges();

        // Load FAQ table of contents data and construct /faq response message
        GenericCommands.loadFAQTableOfContents();

        // Load all the announcement messages
        AnnouncementLoader.loadAnnouncements();

        // Initiate announcement timer if this bot instance is for AP Students
        if (Main.MODE.allows(BotMode.Mode.SERVER_MESSAGES))
            AnnouncementLoader.initiateTimer();

        LOG.info("Finished startup!");
        System.out.println();
    }

    private void printStartupLog() {
        Discord.AP_STUDENTS = Main.JDA.getGuildById(ID.AP_STUDENTS_GUILD);
        Discord.STATSBOT_CENTRAL = Main.JDA.getGuildById(ID.STATSBOT_CENTRAL_GUILD);

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

        Discord.SIMON = Main.JDA.retrieveUserById(ID.SIMON).complete();

        EmbedBuilder embed = Utils.makeEmbed(
                "Startup Log",
                "Starting bot in `" + Main.MODE.getModeName() + "` mode.",
                Colors.ADMIN,
                new MessageEmbed.Field("AP Students",
                        getCheckLine("Server", Discord.AP_STUDENTS != null) + "\n" +
                        getCheckLine("#apstats channel", APStatsChannel) + "\n" +
                        getCheckLine("#bot-commands channel", botCommandsChannel),
                        false),
                new MessageEmbed.Field("StatsBot Central",
                        getCheckLine("Server", Discord.STATSBOT_CENTRAL != null) + "\n" +
                        getCheckLine("#ap-stats channel", APStatsStatsBotChannel) + "\n" +
                        getCheckLine("#startup-log", Discord.STARTUP_LOG != null) + "\n" +
                        getCheckLine("#private-testing channel", privateTestingChannel),
                        false),
                new MessageEmbed.Field("Users",
                        getCheckLine("Myself", true) + "\n" +
                        getCheckLine("Simo\u03c0", Discord.SIMON != null),
                        false),
                new MessageEmbed.Field("Settings",
                        getCheckLine("Prefix", !Setting.PREFIX.equals("")) + "\n" +
                        getCheckLine("Status", Setting.STATUS != OnlineStatus.UNKNOWN) + "\n" +
                        getCheckLine("Dad bot", Setting.DAD_BOT_CHANCE != -1) + "\n" +
                        getCheckLine("Timer delay", Setting.ANNOUNCEMENT_DELAY != -1) + "\n" +
                        getCheckLine("Messages check", Setting.ANNOUNCEMENT_MESSAGES_CHECK != -1),
                        false)
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
