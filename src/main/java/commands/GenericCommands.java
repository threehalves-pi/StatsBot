package commands;

import main.Setting;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class GenericCommands {
    public static void statsbotSlashCommand(SlashCommandEvent event) {
        event.reply("Hi!").setEphemeral(true).queue();
    }

    /**
     * Responds with the {@link Setting#SURVEY_LINK} when the <b>/survey</b> command is used
     *
     * @param event The {@link SlashCommandEvent}
     */
    public static void surveySlashCommand(SlashCommandEvent event) {
        event.reply("Do you want to help out future AP Statistics students? If you answered yes to that " +
                "question you should take the fantastic survey designed to do just that: <" + Setting.SURVEY_LINK + ">. " +
                "Even if you answered no to that question you should still take it anyways."
        ).queue();
    }
}
