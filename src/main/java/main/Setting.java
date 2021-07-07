package main;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.io.*;
import java.util.Properties;

public class Setting {
    // Settings imported from config.properties resource
    public static String VERSION = "";

    public static String PREFIX = "";
    public static OnlineStatus STATUS = OnlineStatus.UNKNOWN;
    public static double DAD_BOT_CHANCE = -1;
    public static boolean RELOAD_COMMANDS = true;

    public static int ANNOUNCEMENT_DELAY = -1;
    public static int ANNOUNCEMENT_MESSAGES_CHECK = -1;
    public static String SURVEY_LINK = "";
    public static String FAQ_LINK = "";
    public static String ASKING_QUESTIONS_FAQ_LINK = "";
    public static String SURVEY_TEMPLATE = "";


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

            VERSION = properties.getProperty("version");

            PREFIX = properties.getProperty("prefix");
            STATUS = OnlineStatus.fromKey(properties.getProperty("status"));
            DAD_BOT_CHANCE = Double.parseDouble(properties.getProperty("dad_bot_chance"));
            RELOAD_COMMANDS = Boolean.parseBoolean(properties.getProperty("refresh-commands"));

            ANNOUNCEMENT_DELAY = Integer.parseInt(properties.getProperty("announcement_delay"));
            ANNOUNCEMENT_MESSAGES_CHECK = Integer.parseInt(properties.getProperty("announcement_messages_check"));
            SURVEY_LINK = properties.getProperty("survey_link");
            FAQ_LINK = properties.getProperty("faq_link");
            ASKING_QUESTIONS_FAQ_LINK = properties.getProperty("asking_questions_faq_link");
            SURVEY_TEMPLATE = properties.getProperty("survey-template");

            LOG.info("Loaded settings from config.properties.");

        } catch (Exception e) {
            LOG.error("Failed to import settings from config.properties.", e);
        }
    }

    /**
     * Saves the current settings to the config.properties file.
     */
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
