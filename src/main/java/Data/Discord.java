package Data;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * Contains JDA objects for Discord things referenced in {@link ID}.
 */
public class Discord {
    // Guilds
    public static Guild AP_STUDENTS;
    public static Guild STATSBOT_CENTRAL;


    // Channels
    public static TextChannel STARTUP_LOG;


    // Discord users
    public static User SELF;
    public static User SIMON;


    // Emojis
    public static final String CHECK = "<:check:" + ID.CHECK + ">";
    public static final String RED_X = "<:red_x:" + ID.RED_X + ">";
}
