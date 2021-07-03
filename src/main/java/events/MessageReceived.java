package events;

import main.ID;
import main.SETTING;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageReceived extends ListenerAdapter {

    /**
     * This method is called whenever a message in sent in any Discord channel (whether a server or direct message).
     *
     * @param event the event object with the message data
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isMentioned(event))
            return;
        if (EventUtils.ignoreChannel(event.getChannel()))
            return;

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!help"))
            event.getMessage().reply("Hi, I'm StatsBot. I'm currently under development. " +
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
            message.reply("Hi, my prefix is `" + SETTING.PREFIX + "`.").queue();
            return true;
        }
        return false;
    }
}
