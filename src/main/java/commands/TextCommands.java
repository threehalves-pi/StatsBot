package commands;

import commands.faq.FAQEntry;
import data.Colors;
import data.Link;
import data.Setting;
import events.EventUtils;
import main.BotMode;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;

/**
 * This class contains all the methods used for evaluating standard text based commands send to Stats Bot. This means
 * all commands that are not based on slash commands are evaluated here.
 */
public class TextCommands {

    /**
     * This is the central {@link TextCommands} method. It confirms that a given message uses the bot's prefix and is
     * intended as a command, and calls a specific command method.
     *
     * @param event the message event from a server or DM
     */
    public static void processPrefixedCommands(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String contents = message.getContentRaw();

        // If there is no prefix or the message is too short, ignore it
        if (!contents.startsWith(Setting.PREFIX) || contents.length() < 2)
            return;

        // Remove the prefix and separate the arguments
        contents = contents.substring(Setting.PREFIX.length()).trim();
        String[] args = contents.toLowerCase(Locale.ROOT).split("\\s+");

        // Determine which command was requested, and call the appropriate function
        switch (args[0]) {

            // Generic commands
            case "help" -> help(message);
            case "faq" -> faqCommand(message, args);

            // Admin commands
            case "admin" -> {
                if (isAdmin(message))
                    admin(message);
            }

            case "mode" -> {
                if (isAdmin(message))
                    mode(message, args);
            }

            default -> Utils.reply(message, "Sorry, I don't recognize that command. Type `/` for a list " +
                                            "of supported slash commands.");
        }
    }

    /**
     * This checks to see if the author of a given message is a registered admin according to {@link
     * EventUtils#isAdmin(User)}. If they are an admin, <code>true</code> is returned. Otherwise, <code>false</code> is
     * returned to indicate that the author is not an admin, and a response is sent to the message indicating that the
     * command is reserved for admins.
     *
     * @param message the message to check
     * @return <code>true</code> if the message author is an admin; <code>false</code> otherwise
     */
    private static boolean isAdmin(Message message) {
        if (EventUtils.isAdmin(message.getAuthor()))
            return true;
        message.reply("Sorry, this command is reserved for administrators.").queue();
        return false;
    }

    private static void help(Message message) {
        message.reply("This command is deprecated in favor of the new `/help` command. " +
                      "Please use that instead.").queue();
    }

    /**
     * This method processes the standard prefixed command <code>faq</code>. The command allows users to forward someone
     * to the {@link Link#FAQ FAQ} in the channel pins.
     *
     * @param message the message requesting the faq
     * @param args    the user's message in all lowercase, separated by spaces. <code>args[0]</code> is guaranteed to be
     *                "<code>faq</code>".
     */
    private static void faqCommand(Message message, String[] args) {
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
    private static Message getFaqEmbed(int id, String requestingUser) {
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
    private static void mode(Message message, String[] args) {
        try {
            if (args.length < 2) {
                message.reply("I am currently running mode `" + Main.MODE.getModeName() + "`.\n\n" +
                              "To change the bot mode, type `" + Setting.PREFIX + "mode [mode]`, where " +
                              "`[mode]` is one of `running`, `testing`, or `all`.").queue();
                return;
            }

            BotMode newMode = BotMode.fromName(args[1]);
            assert newMode != null;

            if (newMode == Main.MODE)
                message.reply("I'm already running mode `" + newMode.getModeName() + "`").queue();
            else {
                Main.MODE = newMode;
                message.reply("Updated mode to `" + newMode.getModeName() + "`").queue();
            }

        } catch (Exception e) {
            message.reply("Sorry, I don't recognize that mode. Please use one of" +
                          "`running`, `testing`, or `all`.").queue();
        }
    }

    /**
     * Basically just the <code>help</code> command for admins
     *
     * @param message the message requesting the command
     */
    private static void admin(Message message) {
        message.replyEmbeds(
                Utils.makeEmbed(
                        "Admin Commands",
                        "Here are all the admin commands I currently support:\n" +
                        "`" + Setting.PREFIX + "mode [mode]` - Change the current BotMode\n" +
                        "`" + Setting.PREFIX + "help` - View this help panel",
                        Colors.ADMIN)
                        .build())
                .queue();
    }
}
