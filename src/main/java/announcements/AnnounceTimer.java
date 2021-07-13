package announcements;

import data.Colors;
import data.ID;
import data.Setting;
import main.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.TimerTask;

public class AnnounceTimer extends TimerTask {

    @Override
    public void run() {
        try {
            TextChannel channel = Main.JDA.getTextChannelById(Setting.ANNOUNCEMENT_CHANNEL);
            assert channel != null;

            // Validate the channel to confirm that an announcement can be sent right now.
            // If validation fails, disable the timer. (It'll restart when another message is sent in the channel)
            if (!validateChannel(channel)) {
                AnnouncementLoader.LOG.warn("Channel validation failed. Postponing...");
                AnnouncementLoader.clearTimer();
                return;
            }

            channel.sendMessage(AnnouncementLoader.getAnnouncementMessage())
                    .queue(s -> AnnouncementLoader.LOG.info("Posted scheduled announcement."));
        } catch (Exception e) {
            AnnouncementLoader.LOG.error("Failed to send announcement.", e);
        }
    }

    /**
     * This checks to see if the announcement channel is ready to have another announcement sent to it, by attempting to
     * discern if it already has a recently posted announcement, or if a question was asked recently that takes
     * priority.
     * <p>
     * It retrieves the last {@link Setting#ANNOUNCEMENT_MESSAGES_CHECK} number of messages from the announcements
     * channel. The bot will avoid sending an announcement if any of the following are found in the latest messages:
     * <ul>
     * <li>A previous announcement message
     * <li>Any message from AP Bot
     * <li>An image (which indicates that a question was asked)
     * </ul>
     *
     * @param channel the announcements channel to check
     * @return true if the channel passed validation and an announcement should be sent; false otherwise
     */
    private boolean validateChannel(TextChannel channel) {
        try {
            List<Message> messages = channel.getHistory().retrievePast(Setting.ANNOUNCEMENT_MESSAGES_CHECK).complete();

            if (messages.size() != Setting.ANNOUNCEMENT_MESSAGES_CHECK)
                throw new Exception("Failed to retrieve " + Setting.ANNOUNCEMENT_MESSAGES_CHECK + " messages. " +
                                    "Retrieved " + messages.size() + " messages from announcements channel instead.");

            // If any messages fail validation, return false to postpone the announcement message
            for (Message message : messages)
                if (!validateMessage(message))
                    return false;

            return true;

        } catch (Exception e) {
            AnnouncementLoader.LOG.error("Failed to scan messages for announcement.", e);
            return false;
        }
    }

    /**
     * This method is called by {@link #validateChannel(TextChannel)} to confirm that a given message being checked is
     * valid according to the rules outlined in that method's documentation. If the message is acceptable and does not
     * prevent sending an announcement message, true is returned.
     *
     * @param message the message to validate
     * @return true if the message is acceptable and does not prevent sending an announcement; false otherwise
     */
    private boolean validateMessage(Message message) {
        // Check to see if the message is from AP Bot, and if so validation fails.
        if (message.getAuthor().getIdLong() == ID.AP_BOT)
            return false;

        // Check to see if the message is a previous announcement by confirming that it is (a) sent by Stats Bot
        // and (b) uses the announcements color.
        if (message.getAuthor().getIdLong() == ID.SELF) {
            List<MessageEmbed> embeds = message.getEmbeds();
            // If there's at least one embed and it uses the announcement color, return false. Otherwise, return true.
            return embeds.size() != 1 || !Colors.ANNOUNCEMENTS.equals(embeds.get(0).getColor());
        }

        // Check attachments for images. Images indicate questions, and therefore they fail validation.
        // Note that images sent by Stats Bot are ignored as the Stats Bot check occurred first.
        List<Message.Attachment> attachments = message.getAttachments();
        for (Message.Attachment attachment : attachments)
            if (attachment.isImage())
                return false;

        return true;
    }

}
