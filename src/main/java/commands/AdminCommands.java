package commands;

import main.Utils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class AdminCommands {
    public static void link(SlashCommandEvent event) {
        Utils.replyEphemeral(event, "Success!");
    }
}
