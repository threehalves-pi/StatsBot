package data;

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


    // Custom emojis

    public static final String CHECK = "<:check:" + ID.CHECK + ">";
    public static final String RED_X = "<:red_x:" + ID.RED_X + ">";

    // Unicode emojis
    // For a five character unicode emoji converter, see
    // http://www.russellcottrell.com/greek/utilities/SurrogatePairCalculator.htm

    public static final String EMOJI_MEGA = "\uD83D\uDCE3";
    public static final String EMOJI_GREY_QUESTION = "\u2754";
    public static final String EMOJI_PENCIL = "\uD83D\uDCDD";
    public static final String EMOJI_BOOKS = "\uD83D\uDCDA";
}
