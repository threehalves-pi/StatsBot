package events;

import data.Discord;
import data.ID;
import main.Main;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnReaction extends ListenerAdapter {
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Main.MODE.ignoreEvent(event);

        // Ignore reactions from non-admins
        if (!EventUtils.isAdmin(event.getUser()))
            return;

        // If reaction is :x: and the message was sent by Stats Bot, delete it
        if (event.getReaction().getReactionEmote().getAsReactionCode().equals(Discord.EMOJI_X)) {
            event.retrieveMessage().queue(
                    m -> {
                        if (m.getAuthor().getIdLong() == ID.SELF)
                            m.delete().queue();
                    }
            );
        }
    }
}
