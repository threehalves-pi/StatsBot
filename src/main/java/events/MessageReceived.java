package events;

import announcements.Announcements;
import Data.ID;
import Data.Setting;
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
            event.getMessage().reply("This command is deprecated in favor of the new `/help` command. " +
                    "Please use that instead.").queue();
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
            message.reply("Hi, my prefix is `" + Setting.PREFIX + "`. You can also use `/help` for more info.").queue();
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
     * Checks if a message equals the {@link Setting#SURVEY_TEMPLATE}. If it does, a "thumbs up" reaction will be added
     * indicating endorsement for the message
     *
     * @param event The {@link MessageReceivedEvent}
     * @return If a survey link was indeed found, true will be returned
     */
    private boolean checkSurveyLink(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if(message.getContentStripped().equals(Setting.SURVEY_TEMPLATE)) {
            event.getMessage().addReaction("\uD83D\uDC4D").queue();
            return true;
        }

        return false;
    }
}
