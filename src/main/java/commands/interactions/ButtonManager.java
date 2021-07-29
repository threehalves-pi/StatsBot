package commands.interactions;

import commands.slash.Diagram;
import data.Colors;
import data.Setting;
import events.EventUtils;
import events.OnInteraction;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.Presence;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * This class contains the methods for processing all {@link ButtonClickEvent ButtonClickEvents}. Those methods are
 * called by {@link OnInteraction#onButtonClick(ButtonClickEvent)}. If a number of button responses are implemented,
 * this class may be separated into public and private events (at minimum).
 */
public class ButtonManager {
    public static void diagram(@Nonnull ButtonClickEvent event) {
        Diagram diagram;
        String value = event.getComponentId().substring(8);

        try {
            diagram = Diagram.getDiagram(value);
            assert diagram != null;
        } catch (Exception ignore) {
            event.reply("Error. Failed to identify desired diagram. Please try again later.")
                    .setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(Utils.makeEmbed(
                diagram.name(),
                "",
                Colors.INFO,
                "Sent by " + Objects.requireNonNull(event.getMember()).getEffectiveName() + " | /diagram")
                .setImage(diagram.getFullLink())
                .build()
        ).queue();
    }

    public static void runPanelButtons(@Nonnull ButtonClickEvent event) {
        // User must be an admin to use panel controls
        if (!EventUtils.isAdmin(event.getUser())) {
            OnInteraction.buttonError(event, "Error. You must be an administrator to use the control panel.");
            return;
        }

        String[] id = event.getComponentId().split(":");

        // All panel buttons have 3 section ids.
        if (id.length != 3) {
            OnInteraction.buttonError(event);
            return;
        }

        if (id[1].equals("status")) {
            updateStatus(event, id[2]);
        }
    }

    public static void updateStatus(@Nonnull ButtonClickEvent event, String id) {
        // Get current presence and status
        Presence presence = Main.JDA.getPresence();
        OnlineStatus oldStatus = presence.getStatus();

        // Determine the desired status
        OnlineStatus newStatus = OnlineStatus.fromKey(id);

        // If the status is already set to the new status, there was an error. Otherwise, update it.
        if (oldStatus == newStatus)
            OnInteraction.buttonError(event, "Error. I am already set to " + newStatus.getKey() + ".");
        else {
            //event.deferReply().queue();

            // Attempt to update status, modify buttons, and send reply
            try {
                presence.setStatus(newStatus);
                Setting.STATUS = newStatus;

                Message oldMessage = event.getMessage();
                assert oldMessage != null;
                List<Button> buttons = oldMessage.getActionRows().get(0).getButtons();

                MessageBuilder message = new MessageBuilder(oldMessage).setActionRows(ActionRow.of(
                        buttons.get(0).withDisabled(newStatus == OnlineStatus.ONLINE),
                        buttons.get(1).withDisabled(newStatus == OnlineStatus.IDLE),
                        buttons.get(2).withDisabled(newStatus == OnlineStatus.DO_NOT_DISTURB),
                        buttons.get(3).withDisabled(newStatus == OnlineStatus.INVISIBLE)
                ));

                // Update the message with the new buttons
                oldMessage.editMessage(message.build()).queue();
                OnInteraction.buttonSuccess(event, "Status updated successfully.");

            } catch (Exception e) {
                e.printStackTrace();
                OnInteraction.buttonError(event);
            }
        }
    }
}
