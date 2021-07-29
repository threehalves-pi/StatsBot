package events;

import commands.interactions.ButtonManager;
import commands.interactions.SelectionManager;
import data.Discord;
import main.Main;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnInteraction extends ListenerAdapter {
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (Main.MODE.ignoreEvent(event))
            return;

        String[] id = event.getComponentId().split(":");

        switch (id[0]) {
            case "panel" -> ButtonManager.runPanelButtons(event);
            case "diagram" -> ButtonManager.diagram(event);
                default -> buttonError(event);
        }
    }

    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        if (Main.MODE.ignoreEvent(event))
            return;

        String[] id = event.getComponentId().split(":");

        switch (id[0]) {
            case "diagram" -> SelectionManager.diagram(event);
            default -> event.reply("Error: unrecognized selection. Please try again later.")
                    .setEphemeral(true).queue();
        }

    }

    public static void buttonError(@NotNull ButtonClickEvent event) {
        buttonError(event, "An unknown error occurred. Try again later.");
    }

    public static void buttonError(@NotNull ButtonClickEvent event, @NotNull String error) {
        event.reply(Discord.RED_X + " " + error).setEphemeral(true).queue();
    }

    public static void buttonSuccess(@NotNull ButtonClickEvent event, @NotNull String message) {
        event.reply(Discord.CHECK + " " + message).setEphemeral(true).queue();
    }

}
