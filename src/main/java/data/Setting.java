package data;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.io.*;
import java.util.Properties;

public class Setting {
    // Settings imported from config.properties resource

    // Main bot settings
    public static String VERSION = "";
    public static String PREFIX = "";
    public static OnlineStatus STATUS = OnlineStatus.UNKNOWN;

    // Startup settings
    public static boolean LOAD_COMMANDS_GLOBAL = false;
    public static boolean LOAD_COMMANDS_TESTING = false;
    public static boolean LOAD_GLOBAL_PRIVILEGES = false;

    // Announcement settings
    public static int ANNOUNCEMENT_DELAY = -1;
    public static int ANNOUNCEMENT_MESSAGES_CHECK = -1;

    // Miscellaneous settings
    public static double DAD_BOT_CHANCE = -1;


    // Manually adjusted settings within code

    public static final long ANNOUNCEMENT_CHANNEL = ID.AP_STATS_CHANNEL;


    // Setting control variables
    private final static Properties properties = new Properties();
    private static final Logger LOG = JDALogger.getLog(Setting.class);

    /**
     * This loads settings from the config.properties file into global settings variables here.
     */
    public static void importSettings() {
        try {
            InputStream in = new FileInputStream("resources/config.properties");
            properties.load(in);

            // Main bot settings
            PREFIX = properties.getProperty("prefix");
            STATUS = OnlineStatus.fromKey(properties.getProperty("status"));
            VERSION = properties.getProperty("version");

            // Announcement settings
            ANNOUNCEMENT_DELAY = Integer.parseInt(properties.getProperty("announcement_delay"));
            ANNOUNCEMENT_MESSAGES_CHECK = Integer.parseInt(properties.getProperty("announcement_messages_check"));

            // Startup settings
            LOAD_COMMANDS_GLOBAL = Boolean.parseBoolean(properties.getProperty("load_commands_global"));
            LOAD_COMMANDS_TESTING = Boolean.parseBoolean(properties.getProperty("load_commands_testing"));
            LOAD_GLOBAL_PRIVILEGES = Boolean.parseBoolean(properties.getProperty("load_global_privileges"));

            // Miscellaneous settings
            DAD_BOT_CHANCE = Double.parseDouble(properties.getProperty("dad_bot_chance"));

            LOG.info("Loaded settings from config.properties");

        } catch (Exception e) {
            LOG.error("Failed to import settings from config.properties", e);
        }
    }

    /**
     * Saves the current settings to the config.properties file.
     *
     * Deprecated due to annoyingly overwriting comments in config.properties on save. Scheduled for eventual
     * removal and transfer of config.properties to standard resources directory for main module.
     */
    @Deprecated
    public static void saveSettings() {
        try {
            OutputStream out = new FileOutputStream("resources/config.properties");
            properties.store(out, "Properties updated");
            LOG.info("Saved config.properties.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
