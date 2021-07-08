package commands;

import main.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class GenericCommands {
    public static void statsbotSlashCommand(SlashCommandEvent event) {
        event.reply("Hi!").setEphemeral(true).queue();
    }

    /**
     * Responds with the {@link Setting#SURVEY_LINK} when the <code>/survey</code> command is used
     *
     * @param event The {@link SlashCommandEvent}
     */
    public static void surveySlashCommand(SlashCommandEvent event) {
        event.reply("Do you want to help out future AP Statistics students? If you answered yes to that " +
                "question you should take the fantastic survey designed to do just that: <" + Setting.SURVEY_LINK + ">. " +
                "Even if you answered no to that question you should still take it anyways."
        ).queue();
    }

    /**
     * Sends a generic help message containing info about AP Stats Bot.
     *
     * @param event The {@link SlashCommandEvent}
     */
    public static void helpGeneric(SlashCommandEvent event) {
        EmbedBuilder embed = Utils.buildEmbed(
                "Filler Title",
                "Hi, I'm **AP Stats Bot**, a bot designed by <@" + ID.SIMON + "> to provide utilities " +
                        "and assistance to AP Statistics students. Want to learn more? Continue reading!",
                Colors.INFO,
                "I'm open source! Click on the blue title of this embed to see the Github Repository to learn more about who helped " +
                        "make me. Furthermore, you could help contribute to improve me and get features you want to see added!",
                Utils.makeField(
                        "Commands",
                        "Currently, the only commands available are accessible using **slash commands**, which is what " +
                                "you used to see this help menu. As development continues, more commands and other useful features " +
                                "will be added so check back later.",
                        false
                ),
                Utils.makeField(
                        "Searching for General AP Statistics Help?",
                        "Want to know the best resources for studying AP Statistics? Or how much time you should spend studying? " +
                                "Those questions, and many more, can all be answered using [this FAQ](https://bit.ly/apstats-faq) created using " +
                                "responses collected from hundreds of past AP Statistics students. Psst, you can contribute by taking " +
                                "[this survey](https://bit.ly/apstat-survey)!",
                        false
                ),
                Utils.makeField(
                        "Prefix",
                        "My prefix for running certain commands is `" + Setting.PREFIX + "`.",
                        true
                ),
                Utils.makeField(
                        "Version",
                        "I am currently running on version `" + Setting.VERSION + "`.",
                        true
                )

        );

        embed.setTitle("AP Stats Bot Help", "https://github.com/threehalves-pi/StatsBot");

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
