package events;

import commands.AdminCommands;
import commands.GenericCommands;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommand extends ListenerAdapter {
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        // Determine which slash command was called
        switch (event.getName()) {
            case "statsbot" -> GenericCommands.statsbotSlashCommand(event);
            case "panel" -> AdminCommands.runControlPanel(event);
            case "survey" -> GenericCommands.surveySlashCommand(event);
            default -> event
                    .reply("Sorry, I can't handle that command right now. Try again later.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
