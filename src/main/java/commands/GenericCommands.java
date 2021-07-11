package commands;

import data.Colors;
import data.ID;
import data.Link;
import data.Setting;
import events.Startup;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GenericCommands {
    /**
     * This is the message sent in response to the <code>/faq</code> command. It is created by {@link
     * #loadFAQTableOfContents()} on startup.
     */
    private static Message faqMessage;

    public static void statsbot(SlashCommandEvent event) {
        event.reply("Hi!").setEphemeral(true).queue();
    }

    /**
     * Responds with the {@link Link#SURVEY} when the <code>/survey</code> command is used
     *
     * @param event The {@link SlashCommandEvent}
     */
    public static void survey(SlashCommandEvent event) {
        Utils.replyEphemeral(event,
                Utils.addLinkButton(
                        Utils.makeEmbed(
                                "AP Statistics Survey",
                                "Each year we survey AP Statistics students after they take the AP exam. " +
                                "We ask about study resources, course difficulty, graphing calculators, and much " +
                                "more. The data we collect are used to make " +
                                Utils.link("this FAQ document", Link.FAQ) + ", which serves to assist " +
                                "incoming students as they prepare for the exam.\n\n" +
                                "If you've taken AP Statistics, please take some time to help many years of " +
                                "students to come.",
                                Colors.INFO),
                        Link.SURVEY,
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
                Utils.makeEmbed(
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
                                Utils.link("collaborative", Link.GITHUB) + " effort. " +
                                "This project\u2014along with the popular " +
                                Utils.link("survey", Link.SURVEY) + " and " +
                                Utils.link("FAQ", Link.FAQ) + "\u2014was initially created by " +
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
                                Utils.link("this document", Link.FAQ) + " from the channel pins. " +
                                "And in case you missed it, don't forget to take " +
                                Utils.link("this survey", Link.SURVEY) + " for AP Statistics students."
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
                        Utils.makeEmbed(
                                "Source Code",
                                Utils.mentionMe() + " is completely open source. We welcome general " +
                                "suggestions, simple bug fixes, and significant feature contributions. " +
                                "Check out the code " + Utils.link("on github", Link.GITHUB) + ".",
                                Colors.INFO,
                                "Want to see your name on the developer list? Make a pull request on github!",
                                Utils.makeField(
                                        "Developers",
                                        Utils.mention(ID.SIMON) + " - Founder and lead developer\n" +
                                        Utils.mention(ID.NOTSMART) + " - Minor feature contributor")
                        ),
                        Link.GITHUB,
                        "Stats Bot on Github"));

    }

    public static void faq(SlashCommandEvent event) {
        Utils.replyEphemeral(event, faqMessage);
    }

    /**
     * This loads the table of contents for the AP Statistics FAQ from the <code>faq.csv</code> resource. Each line in
     * that file corresponds to a header or question in the document and contains a link to that section. This method
     * reads the table of contents data into a list of {@link FAQEntry} records and uses those to construct the {@link
     * EmbedBuilder} sent in response to the <code>/faq</code> slash command.
     */
    public static void loadFAQTableOfContents() {
        try {
            List<MessageEmbed.Field> fields = new ArrayList<>();
            Scanner in = new Scanner(Utils.getResourceFile("/faq.csv"));
            // Omit the header line
            in.nextLine();

            // Counters for the current MessageEmbed.Field and question heading number
            int field = -1;
            int item = 1;

            while (in.hasNextLine()) {
                FAQEntry entry = FAQEntry.of(in.nextLine());
                FAQEntry.entries.add(entry);

                // If the next entry is a category, make a new field. If it's a question, add it to the previous field.
                if (entry.type().equals("category")) {
                    fields.add(Utils.makeField(entry.text(), ""));
                    field++;
                } else {
                    FAQEntry.questions.add(entry);

                    String header = fields.get(field).getName();
                    String text = fields.get(field).getValue();
                    fields.set(
                            field,
                            Utils.makeField(
                                    header,
                                    text + (EmbedBuilder.ZERO_WIDTH_SPACE.equals(text) ? "" : "\n") +
                                    item + ". " + entry.getHyperlink())
                    );
                    item++;
                }
            }

            // Build the actual embed
            faqMessage = Utils.addLinkButton(
                    Utils.makeEmbed(
                            "Frequently Asked Questions",
                            "Looking for answers to common questions? Check out this " +
                            "handy AP Stats " + Utils.link("FAQ", Link.FAQ) + ". It's based on data " +
                            "from a " + Utils.link("survey", Link.SURVEY) + " of over 100 past " +
                            "students.\n\n**__Table of Contents__**",
                            Colors.INFO,
                            fields.toArray(new MessageEmbed.Field[0])),
                    Link.FAQ,
                    "Open the FAQ"
            );

            Startup.LOG.info("Initialized /faq response message");

        } catch (FileNotFoundException e) {
            Startup.LOG.error("Failed to load faq.csv data into /faq response message", e);
            e.printStackTrace();
        }

    }
}
