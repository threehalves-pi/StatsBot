package main;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

import java.io.*;
import java.util.Properties;

public class Setting {
    // Settings imported from config.properties resource
    public static String PREFIX = "";
    public static OnlineStatus STATUS = OnlineStatus.UNKNOWN;
    private final static Properties properties = new Properties();

    private static final Logger LOG = JDALogger.getLog(Setting.class);

    /**
     * This loads settings from the config.properties file into global settings variables here.
     */
    public static void importSettings() {
        try {
            InputStream in = new FileInputStream("resources/config.properties");
            properties.load(in);

            PREFIX = properties.getProperty("prefix");
            STATUS = OnlineStatus.fromKey(properties.getProperty("status"));

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
