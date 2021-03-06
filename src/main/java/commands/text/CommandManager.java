package commands.text;

import data.Setting;
import events.EventUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class manages the {@link CommandReply CommandReplies} associated with the current bot instance.
 */
public class CommandManager {
    private static final Map<Long, CommandReply> commandReplies = new HashMap<>();

    /**
     * This is the central {@link TextCommands} method. It confirms that a given message uses the bot's prefix and is
     * intended as a command, and calls a specific command method.
     *
     * @param event the message event from a server or DM
     */
    public static void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        Message message = event.getMessage();
        String contents = message.getContentRaw();
        MessageChannel channel = event.getChannel();
        long messageId = message.getIdLong();

        // Confirm that the message starts with the prefix, and remove the prefix if it does
        if ((contents = removePrefix(contents)) == null)
            return;

        // Separate the arguments
        String[] args = contents.toLowerCase(Locale.ROOT).split("\\s+");

        // Determine which command was requested, and call the appropriate function
        commandReplies.put(messageId, getReply(message, args, true));
    }

    public static void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        // If no command reply was sent to the message being edited, ignore it
        if (!commandReplies.containsKey(event.getMessageIdLong()))
            return;

        CommandReply reply = commandReplies.get(event.getMessageIdLong());
        String command = reply.getCommand();

        Message message = event.getMessage();
        String contents = message.getContentRaw();

        // Confirm that the message starts with the prefix, and remove the prefix if it does.
        // Otherwise, if a prefix was not used, delete the CommandReply
        if ((contents = removePrefix(contents)) == null) {
            reply.delete();
            return;
        }

        // Separate the arguments
        String[] args = contents.toLowerCase(Locale.ROOT).split("\\s+");

        // Confirm that the user's new command is the same as the original command. If not, delete the reply.
        if (!reply.commandMatches(args)) {
            delete(event.getMessageIdLong());
            return;
        }

        // Update the existing reply to have the contents of a newly generated reply
        reply.update(getReply(message, args, false));
    }

    public static void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        // If a command reply was sent to the message that was deleted, delete the reply
        if (commandReplies.containsKey(event.getMessageIdLong()))
            delete(event.getMessageIdLong());
    }

    /**
     * This determines which {@link TextCommands TextCommand} should be called based on the name of a command, calls it,
     * and returns the resulting {@link CommandReply}. Optionally, this {@link CommandReply} can be sent to Discord.
     *
     * @param args   the arguments of a user's message triggering a command
     * @param doSend whether to send the {@link CommandReply} to Discord using the appropriate method (send/reply)
     * @return the {@link CommandReply}
     */
    private static CommandReply getReply(@Nonnull Message message, @Nonnull String[] args, boolean doSend) {
        // Determine which command was requested, and call the appropriate function
        switch (args[0]) {

            // Generic commands

            case "help" -> {
                return send(
                        CommandReply.ofReply("help", TextCommands.help(message), message),
                        true, doSend);
            }

            case "faq" -> {
                return send(
                        TextCommands.faqCommand(message, args),
                        true, doSend);
            }


            // Admin commands

            case "admin" -> {
                if (EventUtils.isAdmin(message.getAuthor()))
                    return send(
                            CommandReply.ofReply("admin", TextCommands.admin(message), message),
                            true, doSend);
                else
                    return send(
                            invalidPermissions("admin", message),
                            true, doSend);
            }

            case "mode" -> {
                if (EventUtils.isAdmin(message.getAuthor()))
                    return send(
                            CommandReply.ofReply("mode", TextCommands.mode(message, args), message),
                            true, doSend);
                else
                    return send(
                            invalidPermissions("mode", message),
                            true, doSend);
            }

            default -> {
                return send(
                        CommandReply.ofReply(
                                args[0],
                                "Sorry, I don't recognize that command. Type `/` for a list of supported " +
                                "slash commands.",
                                message),
                        true, doSend);
            }
        }
    }

    /**
     * This is a helper method for {@link #getReply(Message, String[], boolean)}. It takes a {@link CommandReply} and
     * determines whether to send it based on the state of <code>doSend</code>.
     *
     * @param commandReply the command to send
     * @param isReply      <code>true</code> to send command response as a reply to a message; <code>false</code> to
     *                     merely send it in the channel
     * @param doSend       whether to actually send the reply to Discord
     * @return the initial {@link CommandReply} instance
     */
    private static CommandReply send(CommandReply commandReply, boolean isReply, boolean doSend) {
        if (doSend)
            return isReply ? commandReply.reply() : commandReply.send();
        return commandReply;
    }

    private static CommandReply invalidPermissions(@Nonnull String command, @Nonnull Message message) {
        return CommandReply.ofReply(command, "Sorry, this command is reserved for administrators.", message);
    }

    /**
     * This checks to see if the given message starts with the bot's {@link Setting#PREFIX prefix} and uses a command.
     * If it does, the prefix is removed and the message is {@link String#trim() trimmed} and returned. Otherwise,
     * <code>null</code> is returned.
     * <p>
     * If the message is starts with the prefix, but removing that prefix would make it an empty string, there cannot be
     * any command, so <code>null</code> is returned.
     *
     * @param message the message to check for a prefix
     * @return <code>null</code> if the prefix was not used; otherwise the message with the prefix removed.
     */
    private static String removePrefix(@Nonnull String message) {
        if (message.toLowerCase(Locale.ROOT).startsWith(Setting.PREFIX) && message.length() > Setting.PREFIX.length())
            return message.substring(Setting.PREFIX.length());
        return null;
    }

    /**
     * Delete a {@link CommandReply} based on its key in {@link #commandReplies}. This deletes the message associated
     * with the reply and the entry in {@link #commandReplies}.
     *
     * @param key they key corresponding to the {@link CommandReply} to be deleted
     */
    public static void delete(long key) {
        try {
            commandReplies.get(key).delete().queue(s -> {
            }, f -> {
                // It's possible that the message was deleted by an admin. If so, ignore any errors.
            });
            commandReplies.remove(key);
        } catch (Exception ignored) {
        }
    }
}
