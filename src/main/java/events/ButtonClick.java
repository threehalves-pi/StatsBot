package events;

import main.Discord;
import main.Main;
import main.Setting;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ButtonClick extends ListenerAdapter {
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        // If the button ID doesn't contain at least one colon, it's invalid and should be rejected
        if (!event.getComponentId().contains(":")) {
            buttonError(event);
            return;
        }

        if (event.getComponentId().startsWith("panel"))
            runPanelButtons(event);
        else
            buttonError(event);
    }

    public void runPanelButtons(@NotNull ButtonClickEvent event) {
        // User must be an admin to use panel controls
        if (!EventUtils.isAdmin(event.getUser())) {
            buttonError(event, "Error. You must be an administrator to use the control panel.");
            return;
        }

        String[] id = event.getComponentId().split(":");

        // All panel buttons have 3 section ids.
        if (id.length != 3) {
            buttonError(event);
            return;
        }

        if (id[1].equals("status")) {
            updateStatus(event, id[2]);
        }
    }

    public void updateStatus(@NotNull ButtonClickEvent event, String id) {
        // Get current presence and status
        Presence presence = Main.jda.getPresence();
        OnlineStatus oldStatus = presence.getStatus();

        // Determine the desired status
        OnlineStatus newStatus = OnlineStatus.fromKey(id);

        // If the status is already set to the new status, there was an error. Otherwise, update it.
        if (oldStatus == newStatus)
            buttonError(event, "Error. I am already set to " + newStatus.getKey() + ".");
        else {
            //event.deferReply().queue();

            // Attempt to update status, modify buttons, and send reply
            try {
                presence.setStatus(newStatus);
                Setting.STATUS = newStatus;
                Setting.saveSettings();

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
                buttonSuccess(event, "Status updated successfully.");

            } catch (Exception e) {
                e.printStackTrace();
                buttonError(event);
            }
        }
    }

    public void buttonError(@NotNull ButtonClickEvent event) {
        buttonError(event, "An unknown error occurred. Try again later.");
    }

    public void buttonError(@NotNull ButtonClickEvent event, @NotNull String error) {
        event.reply(Discord.RED_X + " " + error).setEphemeral(true).queue();
    }

    public void buttonSuccess(@NotNull ButtonClickEvent event, @NotNull String message) {
        event.reply(Discord.CHECK + " " + message).setEphemeral(true).queue();
    }
}
