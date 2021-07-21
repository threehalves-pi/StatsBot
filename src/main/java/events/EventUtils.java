package events;

import data.ID;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class EventUtils {
    /**
     * Determines whether an event occurred in a channel that the bot should ignore. The bot has access to many of the
     * channels in the AP Students Discord server, but it should only send messages in some of them.
     *
     * @param channel the channel in which an event was triggered. (May be a server or direct message channel).
     * @return true if the event should be ignored; false otherwise.
     */
    public static boolean ignoreChannel(MessageChannel channel) {
        // If the event was triggered in a DM, do NOT ignore it.
        if (channel instanceof PrivateChannel)
            return false;

        long guildId = ((TextChannel) channel).getGuild().getIdLong();
        long channelId = channel.getIdLong();

        // If the event was triggered in AP Students, only allow certain whitelisted channels
        if (guildId == ID.AP_STUDENTS_GUILD) {
            // Only allow #apstats and #bot-commands
            return channelId != ID.AP_STATS_CHANNEL && channelId != ID.BOT_COMMANDS_CHANNEL;
        }

        // If channel is unknown, do NOT ignore it
        return false;
    }

    /**
     * Determines if the given user has authority to perform administrative actions. An administrator is defined as a
     * Discord user whose unique ID is listed in {@link ID#ADMINS}.
     *
     * @param user the given user to test
     * @return true if the user is an admin; false otherwise
     */
    public static boolean isAdmin(User user) {
        for (long l : ID.ADMINS)
            if (user.getIdLong() == l)
                return true;
        return false;
    }
}
