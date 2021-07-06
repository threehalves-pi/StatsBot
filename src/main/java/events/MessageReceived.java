package events;

import announcements.Announcements;
import main.ID;
import main.Main;
import main.Setting;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MessageReceived extends ListenerAdapter {

    /**
     * This method is called whenever a message in sent in any Discord channel (whether a server or direct message).
     *
     * @param event the event object with the message data
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Ignore messages from myself
        if (event.getAuthor().getIdLong() == ID.SELF)
            return;

        // Whenever a message is sent in the announcements channel, the timer must be reset
        if (event.getChannel().getIdLong() == Setting.ANNOUNCEMENT_CHANNEL)
            Announcements.resetTimer();

        if (isMentioned(event))
            return;

        if (EventUtils.ignoreChannel(event.getChannel()))
            return;

        if (checkDadBot(event))
            return;

        if(checkSurveyLink(event))
            return;

        if (event.getMessage().getContentRaw().equalsIgnoreCase(Setting.PREFIX + "help"))
            event.getMessage().reply("Hi, I'm Stats Bot. I'm currently under development. " +
                    "Check back another time for a full command list and useful features.").queue();
    }

    /**
     * Determine if the message mentions StatsBot (without saying anything else). If so, the user is most likely looking
     * for the bot's prefix, so send that.
     *
     * @param event the message data
     * @return true if the bot was mentioned and sent a response; false otherwise
     */
    private boolean isMentioned(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getContentRaw().equals("<@!" + ID.SELF + ">")) {
            message.reply("Hi, my prefix is `" + Setting.PREFIX + "`.").queue();
            return true;
        }

        return false;
    }

    /**
     * Checks to see if someone sent a message in the form "I'm [x]". If they did, there's a 1% chance StatsBot will
     * respond with "hi [x], I'm StatsBot!"
     *
     * @param event the message received event
     * @return true if the bot responded; false if nothing happened
     */
    private boolean checkDadBot(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();

        // Ignore messages that are too short or long to be funny
        if (message.length() < 5 || message.length() > 40)
            return false;

        String name = null;

        if (message.toLowerCase(Locale.ROOT).startsWith("i'm "))
            name = message.substring(4);
        else if (message.toLowerCase(Locale.ROOT).startsWith("im "))
            name = message.substring(3);

        if (name != null && Math.random() < Setting.DAD_BOT_CHANCE) {
            event.getMessage().reply("Hi " + name + ", I'm StatsBot!").queue();
            return true;
        }

        return false;
    }

    /**
     * Checks if a message contains the {@link Setting#SURVEY_LINK}. If it does, a reply will be sent stating that Stats Bot
     * endorses this message. The bot will proceed to send the link as well because spamming the survey link is fun
     *
     * @param event The {@link MessageReceivedEvent}
     * @return If a survey link was indeed found, true will be returned
     */
    private boolean checkSurveyLink(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if(message.getContentRaw().contains(Setting.SURVEY_LINK)) {
            event.getMessage().reply(
                    "This message is endorsed by me, " + Main.jda.getSelfUser().getAsMention() + ". " +
                    "Oh also if you didn't already you should take this fantastic survey: " + Setting.SURVEY_LINK
            ).mentionRepliedUser(false).queue();
            return true;
        }

        return false;
    }
}
