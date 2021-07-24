package data;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.util.Properties;

public class Setting {
    // Settings imported from config.properties resource

    // Main bot settings
    public static String VERSION = "";
    public static String PREFIX = "";
    public static OnlineStatus STATUS = OnlineStatus.UNKNOWN;

    // Startup settings
    public static boolean LOAD_COMMANDS_GLOBAL = false;
    public static boolean LOAD_COMMANDS_PRIVATE = false;

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
            properties.load(Setting.class.getResourceAsStream("/config.properties"));

            // Main bot settings
            PREFIX = properties.getProperty("prefix");
            STATUS = OnlineStatus.fromKey(properties.getProperty("status"));
            VERSION = properties.getProperty("version");

            // Announcement settings
            ANNOUNCEMENT_DELAY = Integer.parseInt(properties.getProperty("announcement_delay"));
            ANNOUNCEMENT_MESSAGES_CHECK = Integer.parseInt(properties.getProperty("announcement_messages_check"));

            // Startup settings
            LOAD_COMMANDS_GLOBAL = Boolean.parseBoolean(properties.getProperty("load_commands_global"));
            LOAD_COMMANDS_PRIVATE = Boolean.parseBoolean(properties.getProperty("load_commands_private"));

            // Miscellaneous settings
            DAD_BOT_CHANCE = Double.parseDouble(properties.getProperty("dad_bot_chance"));

            LOG.info("Loaded settings from config.properties");

        } catch (Exception e) {
            LOG.error("Failed to import settings from config.properties", e);
        }
    }
}
