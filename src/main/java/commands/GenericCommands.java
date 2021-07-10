package commands;

import Data.Colors;
import Data.ID;
import Data.Setting;
import main.Utils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

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
        Utils.replyEphemeral(event,
                Utils.addLinkButton(
                        Utils.buildEmbed(
                                "AP Statistics Survey",
                                "Each year we survey AP Statistics students after they take the AP exam. " +
                                "We ask about study resources, course difficulty, graphing calculators, and much " +
                                "more. The data we collect are used to make " +
                                Utils.link("this FAQ document", Setting.FAQ_LINK) + ", which serves to assist " +
                                "incoming students as they prepare for the exam.\n\n" +
                                "If you've taken AP Statistics, please take some time to help many years of " +
                                "students to come.",
                                Colors.INFO),
                        Setting.SURVEY_LINK,
                        "Take the survey!")
        );
    }

    /**
     * Sends a generic help message containing info about AP Stats Bot.
     *
     * @param event The {@link SlashCommandEvent}
     */
    public static void help(SlashCommandEvent event) {
        Utils.replyEphemeral(event,
                Utils.buildEmbed(
                        "AP Stats Bot - Info",
                        "Hi, I'm " + Utils.mentionMe() + ", a custom Discord bot designed to help AP " +
                        "Statistics students.",
                        Colors.INFO,
                        "I'm open source! Type /source for more info.",
                        Utils.makeField(
                                "Commands",
                                "Currently, I only support slash commands (that's what you used to see this " +
                                "panel). I'm still under development though, so check back later for new features."
                        ),
                        Utils.makeField(
                                "Development",
                                "Making AP Stats Bot is a " +
                                Utils.link("collaborative", Setting.GITHUB_LINK) + " effort. " +
                                "This project\u2014along with the popular " +
                                Utils.link("survey", Setting.SURVEY_LINK) + " and " +
                                Utils.link("FAQ", Setting.FAQ_LINK) + "\u2014was initially created by " +
                                Utils.mention(ID.SIMON) + ", but is now available for public feedback and " +
                                "contributions."),
                        Utils.makeField(
                                "Acknowledgements",
                                "Many thanks to " + Utils.mention(ID.NOTSMART) + ", " + Utils.mention(ID.BLU) +
                                ", " + Utils.mention(ID.LANCE) + ", " + Utils.mention(ID.SNOWFLAKE) + ", " +
                                Utils.mention(ID.ZENITH) + ", and " + Utils.mention(ID.SHADOW) + " for their " +
                                "contributions, suggestions, and feedback."
                        ),
                        Utils.makeField(
                                "Other Info",
                                "For answers to plenty of frequently asked questions, check out " +
                                Utils.link("this document", Setting.FAQ_LINK) + " from the channel pins. " +
                                "And in case you missed it, don't forget to take " +
                                Utils.link("this survey", Setting.SURVEY_LINK) + " for AP Statistics students."
                        ),
                        Utils.makeField(
                                "Prefix",
                                "My prefix is `" + Setting.PREFIX + "`.",
                                true
                        ),
                        Utils.makeField(
                                "Version",
                                "Running version `" + Setting.VERSION + "`.",
                                true
                        )
                ).setTimestamp(Instant.now())
        );
    }

    public static void source(SlashCommandEvent event) {
        Utils.replyEphemeral(event,
                Utils.addLinkButton(
                        Utils.buildEmbed(
                                "Source Code",
                                Utils.mentionMe() + " is completely open source. We welcome general " +
                                "suggestions, simple bug fixes, and significant feature contributions. " +
                                "Check out the code " + Utils.link("on github", Setting.GITHUB_LINK) + ".",
                                Colors.INFO,
                                "Want to see your name on the developer list? Make a pull request on github!",
                                Utils.makeField(
                                        "Developers",
                                        Utils.mention(ID.SIMON) + " - Founder and lead developer\n" +
                                        Utils.mention(ID.NOTSMART) + " - Minor feature contributor")
                        ),
                        Setting.GITHUB_LINK,
                        "Stats Bot on Github"));

    }
}
