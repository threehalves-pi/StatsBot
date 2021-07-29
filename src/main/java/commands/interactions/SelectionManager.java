package commands.interactions;

import commands.slash.Diagram;
import data.Colors;
import main.Utils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.annotation.Nonnull;
import java.util.List;

public class SelectionManager {
    public static void diagram(@Nonnull SelectionMenuEvent event) {
        List<String> values = event.getValues();
        Diagram selection;

        if (values.size() != 1 || values.get(0) == null || (selection = Diagram.getDiagram(values.get(0))) == null) {
            event.reply("Error: failed to load diagram. Please try again later.")
                    .setEphemeral(true).queue();
            return;
        }

        // Find the matching diagram response message and update it
        InteractionHook hook = Diagram.diagramResponses.get(event.getMessageIdLong());
        if (hook == null) {
            event.reply("Error: failed to locate diagram selection message. Please try again later.")
                    .setEphemeral(true).queue();
            return;
        }

        // Update the original message to replace the dropdown menu with the selected diagram. If the message was
        // sent in a server, include a button to send it to the channel. Otherwise, if it was in a DM, don't include
        // the button.
        hook.editOriginal(new MessageBuilder()
                .setEmbeds(
                        Utils.makeEmbed(
                                "Diagram Loader",
                                "",
                                Colors.INFO,
                                event.isFromGuild() ?
                                        "Click 'send' to send this diagram to #" + event.getChannel().getName() :
                                        "")
                                .setImage(selection.getFullLink())
                                .build())
                .setActionRows(event.isFromGuild() ?
                        ActionRow.of(Button.primary("diagram:" + selection.value(), "Send")) :
                        null)
                .build()
        ).queue();

        event.reply("Loaded diagram." +
                    (event.isFromGuild() ? " Click 'send' to release the diagram publicly." : ""))
                .setEphemeral(true).queue();

        // Remove the hook as it is no longer needed
        Diagram.diagramResponses.remove(event.getMessageIdLong());
    }
}
