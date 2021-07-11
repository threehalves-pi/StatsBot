package events;

import commands.AdminCommands;
import commands.PrivateCommands;
import commands.GenericCommands;
import main.Main;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommand extends ListenerAdapter {
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (Main.MODE.ignoreEvent(event))
            return;

        switch (event.getName()) {

            // Global generic commands
            case "statsbot" -> GenericCommands.statsbot(event);
            case "survey" -> GenericCommands.survey(event);
            case "help" -> GenericCommands.help(event);
            case "source" -> GenericCommands.source(event);
            case "faq" -> GenericCommands.faq(event);

            // Global admin commands
            case "link" -> AdminCommands.link(event);

            // Private commands
            case "panel" -> PrivateCommands.panel(event);
            case "announcement" -> PrivateCommands.announcement(event);
            case "testing" -> PrivateCommands.testing(event);

            // Unknown command
            default -> event
                    .reply("Sorry, I can't handle that command right now. Try again later.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
