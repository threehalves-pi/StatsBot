package events;

import commands.slash.PrivateCommands;
import commands.slash.GlobalCommands;
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
            case "statsbot" -> GlobalCommands.statsbot(event);
            case "survey" -> GlobalCommands.survey(event);
            case "help" -> GlobalCommands.help(event);
            case "source" -> GlobalCommands.source(event);
            case "faq" -> GlobalCommands.faq(event);

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
