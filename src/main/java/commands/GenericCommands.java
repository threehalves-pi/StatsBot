package commands;

import main.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.time.Instant;

public class GenericCommands {
    public static void statsbot(SlashCommandEvent event) {
        event.reply("Hi!").setEphemeral(true).queue();
    }

    /**
     * Responds with the {@link Setting#SURVEY_LINK} when the <code>/survey</code> command is used
     *
     * @param event The {@link SlashCommandEvent}
     */
    public static void survey(SlashCommandEvent event) {
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
    public static void help(SlashCommandEvent event) {
        EmbedBuilder embed = Utils.buildEmbed(
                "AP Stats Bot - Info",
                "Hi, I'm " + Utils.mentionMe() + ", a custom Discord bot designed to help AP Statistics " +
                "students.",
                Colors.INFO,
                "I'm open source! Type /source for more info.",
                Utils.makeField(
                        "Commands",
                        "Currently, I only support slash commands (that's what you used to see this panel). " +
                        "I'm still under development though, so check back later for new features."
                ),
                Utils.makeField(
                        "Developers",
                        "Making AP Stats Bot is a [collaborative](" + Setting.GITHUB_LINK + ") effort. This " +
                        "project\u2014along with the popular [survey](" + Setting.SURVEY_LINK + ") and " +
                        "[FAQ](" + Setting.FAQ_LINK + ")\u2014was initially created by " + Utils.mention(ID.SIMON) +
                        ", but is now available for public feedback and contributions."),
                Utils.makeField(
                        "Acknowledgements",
                        "Many thanks to " + Utils.mention(ID.NOTSMART) + ", " + Utils.mention(ID.BLU) + ", " +
                        Utils.mention(ID.LANCE) + ", " + Utils.mention(ID.SNOWFLAKE) + ", " +
                        Utils.mention(ID.ZENITH) + ", and " + Utils.mention(ID.SHADOW) +
                        " for their contributions, suggestions, and feedback."
                ),
                Utils.makeField(
                        "Other Info",
                        "For answers to plenty of frequently asked questions, check out [this document](" +
                        Setting.FAQ_LINK + ") from the channel pins. And in case you missed it, don't forget to take " +
                        "[this survey](" + Setting.SURVEY_LINK + ") for AP Statistics students."
                ),
                Utils.makeField(
                        "Prefix",
                        "My prefix is `" + Setting.PREFIX + "`.",
                        true
                ),
                Utils.makeField(
                        "Version",
                        "[Current](" + Setting.GITHUB_LINK + ") running version: `" + Setting.VERSION + "`.",
                        true
                )

        )
                .setTimestamp(Instant.now());

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    public static void source(SlashCommandEvent event) {
        EmbedBuilder embed = Utils.buildEmbed(
                "Source Code",
                Utils.mentionMe() + " is completely open source. We welcome general suggestions, " +
                "simple bug fixes, and significant feature contributions. " +
                "Check out the code [on github](" + Setting.GITHUB_LINK + ").",
                Colors.INFO,
                "Want to see your name on the developer list? Make a pull request on github!",
                Utils.makeField(
                        "Developers",
                        Utils.mention(ID.SIMON) + " - Founder and lead developer\n" +
                        Utils.mention(ID.NOTSMART) + " - Minor feature contributor")
        );

        MessageBuilder message = new MessageBuilder();

        message.setEmbeds(embed.build());
        message.setActionRows(ActionRow.of(Button.link(Setting.GITHUB_LINK, "Stats Bot on Github")));

        event.reply(message.build()).setEphemeral(true).queue();
    }
}
