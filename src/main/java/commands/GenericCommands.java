package commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class GenericCommands {
    public static void statsbotSlashCommand(SlashCommandEvent event) {
        event.reply("Hi!").setEphemeral(true).queue();
    }
}
