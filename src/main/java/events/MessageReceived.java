package events;

import announcements.Announcements;
import commands.CommandsRegister;
import commands.FAQEntry;
import commands.GenericCommands;
import data.Colors;
import data.ID;
import data.Link;
import data.Setting;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MessageReceived extends ListenerAdapter {

    /**
     * This method is called whenever a message in sent in any Discord channel (whether a server or direct message).
     *
     * @param event the event object with the message data
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Ignore messages from myself
        if (event.getAuthor().getIdLong() == ID.SELF)
            return;

        // Ignore messages based on their origin and the current BotMode
        if (Main.MODE.ignoreEvent(event))
            return;

        // Whenever a message is sent in the announcements channel, the timer must be reset
        if (event.getChannel().getIdLong() == Setting.ANNOUNCEMENT_CHANNEL)
            Announcements.resetTimer();

        if (isMentioned(event))
            return;

        // Check a specific list of channels that should ignore common messages
        if (EventUtils.ignoreChannel(event.getChannel()))
            return;

        if (checkDadBot(event))
            return;

        if (checkSurveyLink(event))
            return;

        // If the message uses the bot's prefix, check for recognized commands
        processPrefixedCommands(event);
    }

    private void processPrefixedCommands(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String contents = message.getContentRaw();

        // If there is no prefix or the message is too short, ignore it
        if (!contents.startsWith(Setting.PREFIX) || contents.length() < 2)
            return;

        contents = contents.substring(1).trim();
        String[] args = contents.toLowerCase(Locale.ROOT).split("\\s");

        switch (args[0]) {
            case "help" -> Utils.reply(message, "This command is deprecated in favor of the new `/help` command. " +
                                                "Please use that instead.");

            case "faq" -> faqCommand(message, args);

            default -> Utils.reply(message, "Sorry, I don't recognize that command. Type `/` for a list " +
                                            "of supported slash commands.");
        }

    }

    /**
     * This method processes the standard prefixed command <code>faq</code>. The command allows users to forward someone
     * to the FAQ in the channel pins.
     *
     * @param message the message requesting the faq
     * @param args    the user's message in all lowercase, separated by spaces. <code>args[0]</code> is guaranteed to be
     *                "<code>faq</code>".
     */
    private void faqCommand(Message message, String[] args) {
        // First, determine if the user is simply requesting help
        if (args.length > 1 && args[1].equals("help")) {
            Utils.reply(message, "Type `" + Setting.PREFIX + "faq [header #]` to get a link to a specific question " +
                                 "in the FAQ. For a table of contents, type `/faq`.");
            return;
        }

        String user = message.getMember() == null ?
                message.getAuthor().getName() : message.getMember().getEffectiveName();

        // Now determine which FAQ header the user is requesting and who should receive the link

        // This is the message that the bot should reply to when it sends the FAQ link. By default, it's just the
        // message that requested it with the `faq` command. But if that message replied to someone else, use the
        // referenced message instead.
        Message replyTarget = message;

        if (message.getType() == MessageType.INLINE_REPLY && message.getReferencedMessage() != null)
            replyTarget = message.getReferencedMessage();

        int id = -1;

        // If the user gave another argument (and it wasn't "help"), assume it is the id of the desired header
        if (args.length > 1) {
            // Validate the id parameter
            try {
                id = Integer.parseInt(args[1]);
                if (id <= 0 || id > FAQEntry.questions.size())
                    throw new Exception();
            } catch (Exception e) {
                Utils.reply(message, "Unknown id. Type `/faq` for a table of contents and list of " +
                                     "question numbers.");
                return;
            }
        }

        // Finally, send the FAQ embed
        Utils.reply(replyTarget, getFaqEmbed(id, user));
    }

    /**
     * This is a helper method for {@link #faqCommand(Message, String[])}. It returns a {@link Message} to send in reply
     * to the <code>faq</code> command. The message contains a description of the FAQ, a link to either the document
     * itself or a specific header, and the name of the person who requested it.
     *
     * @param id             the specific question id to link to (or -1 to link to the document itself)
     * @param requestingUser the user who requested the <code>faq</code> command
     * @return the message to send in response
     */
    private Message getFaqEmbed(int id, String requestingUser) {
        return Utils.addLinkButton(
                Utils.makeEmbed(
                        "Frequently Asked Questions",
                        "For help with common stats questions, check out this " +
                        Utils.link("FAQ", Link.FAQ) + ". Type /faq for more info." +
                        (id == -1 ?
                                "" :
                                "\nIn particular, see: __" + FAQEntry.questions.get(id - 1).getHyperlink() + "__"
                        ),
                        Colors.INFO,
                        "Sent by @" + requestingUser),
                Link.FAQ,
                "View the FAQ"
        );
    }

    /**
     * Determine if the message mentions StatsBot (without saying anything else). If so, the user is most likely looking
     * for the bot's prefix, so send that.
     *
     * @param event the message data
     * @return true if the bot was mentioned and sent a response; false otherwise
     */
    private boolean isMentioned(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getContentRaw().equals("<@!" + ID.SELF + ">")) {
            message.reply("Hi, my prefix is `" + Setting.PREFIX + "`. You can also use `/help` for more info.")
                    .queue();
            return true;
        }

        return false;
    }

    /**
     * Checks to see if someone sent a message in the form "I'm [x]". If they did, there's a 1% chance StatsBot will
     * respond with "hi [x], I'm StatsBot!"
     *
     * @param event the message received event
     * @return true if the bot responded; false if nothing happened
     */
    private boolean checkDadBot(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();

        // Ignore messages that are too short or long to be funny
        if (message.length() < 5 || message.length() > 40)
            return false;

        String name = null;

        if (message.toLowerCase(Locale.ROOT).startsWith("i'm "))
            name = message.substring(4);
        else if (message.toLowerCase(Locale.ROOT).startsWith("im "))
            name = message.substring(3);

        if (name != null && Math.random() < Setting.DAD_BOT_CHANCE) {
            event.getMessage().reply("Hi " + name + ", I'm StatsBot!").queue();
            return true;
        }

        return false;
    }

    /**
     * Checks if a message matches one of the standard survey advertisement messages. If it does, a "thumbs up" reaction
     * is added to indicate the bot's endorsement of the message.
     *
     * @param event The {@link MessageReceivedEvent}
     * @return true if the message matched a survey link template; false otherwise
     */
    private boolean checkSurveyLink(MessageReceivedEvent event) {
        String[] surveyMessages = {
                """
                fill out this survey to help future ap stats students: https://bit.ly/apstat-survey""",
                """
                if you've taken ap statistics, please fill out this survey to help future students prepare for the exam:
                https://bit.ly/apstat-survey"""
        };

        Message message = event.getMessage();
        String text = message.getContentDisplay().toLowerCase(Locale.ROOT);

        for (String m : surveyMessages)
            if (m.equals(text)) {
                message.addReaction("\uD83D\uDC4D").queue();
                return true;
            }

        return false;
    }
}
