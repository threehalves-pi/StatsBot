package commands.text;

import commands.faq.FAQEntry;
import data.Colors;
import data.Link;
import data.Setting;
import events.EventUtils;
import main.BotMode;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;

/**
 * This class contains all the methods used for evaluating standard text based commands send to Stats Bot. This means
 * all commands that are not based on slash commands are evaluated here.
 */
public class TextCommands {
    static String help(Message message) {
        return "This command is deprecated in favor of the new `/help` command. " +
               "Please use that instead.";
    }

    /**
     * This method processes the standard prefixed command <code>faq</code>. The command allows users to forward someone
     * to the {@link Link#FAQ FAQ} in the channel pins.
     *
     * @param message the message requesting the faq
     * @param args    the user's message in all lowercase, separated by spaces. <code>args[0]</code> is guaranteed to be
     *                "<code>faq</code>".
     * @return the {@link CommandReply}, generated using an {@link CommandReply#ofReply(String, MessageChannel, Message,
     * long) ofReply()} method to be eligible for {@link CommandReply#reply()}
     */
    static CommandReply faqCommand(Message message, String[] args) {
        // This is the message that the bot should reply to when it sends the FAQ link. By default, it's just the
        // message that requested it with the `faq` command. But if that message replied to someone else, use the
        // referenced message instead.
        Message replyTarget = message;


        // First, determine if the user is simply requesting help
        if (args.length > 1 && args[1].equals("help")) {
            return CommandReply.ofReply(
                    "faq",
                    "Type `" + Setting.PREFIX + "faq [header #]` to get a link to a " +
                    "specific question in the FAQ. For a table of contents, type `/faq`.",
                    replyTarget
            );
        }

        String user = message.getMember() == null ?
                message.getAuthor().getName() : message.getMember().getEffectiveName();

        // Now determine which FAQ header the user is requesting and who should receive the link
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
                return CommandReply.ofReply(
                        "faq",
                        new MessageBuilder("Unknown id. Type `/faq` for a table of contents and list of " +
                                           "question numbers.").build(),
                        message
                );
            }
        }

        // Finally, return the FAQ embed
        return CommandReply.ofReply(
                "faq",
                getFaqEmbed(id, user),
                replyTarget
        );
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
    private static Message getFaqEmbed(int id, String requestingUser) {
        return Utils.addLinkButton(
                Utils.makeEmbed(
                        "Frequently Asked Questions",
                        "For common questions, check out " +
                        Utils.link("this FAQ", Link.FAQ) + "." +
                        (id == -1 ?
                                "" :
                                "\nSee: **" + FAQEntry.questions.get(id - 1).getHyperlink() + "**"
                        ),
                        Colors.FAQ,
                        "Type /faq for more info | Requested by @" + requestingUser),
                id == -1 ? Link.FAQ : FAQEntry.questions.get(id - 1).getFullLink(),
                "View the FAQ"
        );
    }

    /**
     * Admin command allowing me to change the current {@link Main#MODE BotMode)}
     *
     * @param message the message requesting the command
     * @param args    the message contents in all lowercase, split by spaces, with the prefix removed.
     */
    static String mode(Message message, String[] args) {
        try {
            if (args.length < 2) {
                return "I am currently running mode `" + Main.MODE.getModeName() + "`.\n\n" +
                       "To change the bot mode, type `" + Setting.PREFIX + "mode [mode]`, where " +
                       "`[mode]` is one of `running`, `testing`, or `all`.";
            }

            BotMode newMode = BotMode.fromName(args[1]);
            assert newMode != null;

            if (newMode == Main.MODE)
                return "I'm already running mode `" + newMode.getModeName() + "`";
            else {
                Main.MODE = newMode;
                return "Updated mode to `" + newMode.getModeName() + "`";
            }

        } catch (Exception e) {
            return "Sorry, I don't recognize that mode. Please use one of" +
                   "`running`, `testing`, or `all`.";
        }
    }

    /**
     * This is basically just the <code>help</code> command for admins.
     * <p>
     * <b>Precondition:</b> the user requesting this command must be an administrator according to {@link
     * EventUtils#isAdmin(User)}.
     *
     * @param message the message requesting the command
     */
    static EmbedBuilder admin(Message message) {
        return Utils.makeEmbed(
                "Admin Commands",
                "Here are all the admin commands I currently support:\n" +
                "`" + Setting.PREFIX + "mode [mode]` - Change the current BotMode\n" +
                "`" + Setting.PREFIX + "help` - View this help panel",
                Colors.ADMIN);
    }
}
