package commands.text;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.DataMessage;

import javax.annotation.Nonnull;

/**
 * This class handles replies made to text based commands (e.g. everything except slash commands). It handles
 * interactions, including {@link Button Buttons} and {@link SelectionMenu SelectionMenus}, along with {@link
 * ListenerAdapter#onMessageUpdate(MessageUpdateEvent) updates} to the initial message triggering the command response.
 */
public class CommandReply {
    /**
     * After a {@link CommandReply} is created with a {@link #dataMessage} and that message is sent to Discord, Discord
     * returns the message with its assigned snowflake ID. This message represents that response from Discord. When a
     * {@link CommandReply} is updated, this is the message that is used to send the update via {@link
     * Message#editMessage(Message)}.
     */
    private Message replyMessage;

    /**
     * This is the {@link Message}, most likely a {@link DataMessage}, that was created in response to a command sent by
     * a user and sent in some channel. It probably does not have a snowflake ID yet, because it has not necessarily
     * been sent to or received from Discord.
     * <p>
     * This is the message that is saved immediately when a new {@link CommandReply} instance is created.
     */
    private Message dataMessage;

    /**
     * This is the channel that a user triggered a command in, and the destination of the {@link #dataMessage}
     * associated with this {@link CommandReply}. This will only change in the event that {@link #reply(Message)} or
     * {@link #reply(Message, boolean)} are used to reply to a message and that message is in a different channel.
     */
    private MessageChannel channel;

    /**
     * If {@link #reply()} is used to send a message, this is the {@link ISnowflake#getIdLong() id} of the message that
     * is replied to.
     */
    private long targetMessageId = 0;

    /**
     * This is the name of the command that a user executed that triggered this {@link CommandReply}. Note that the
     * Bot's {@link data.Setting#PREFIX prefix} is not included in the command name.
     * <p>
     * If a subcommand was used, this should contain both the command and the subcommand. It should never contain
     * arguments.
     */
    private final String command;

    /**
     * It is possible that a user could type a command in Discord and then edit that command before the bot had a chance
     * to reply. In that event, the bot would try to update its {@link #replyMessage} (since the user updated the
     * command) before it had actually {@link #send()} sent} a message and received that message back from Discord.
     * <p>
     * In other words, the bot would attempt to {@link Message#editMessage(Message) edit} the {@link #replyMessage}, but
     * that message would still be <code>null</code>, because the {@link RestAction#queue() queue()} forwarded to {@link
     * #processSendResult(Message)} would not have resolved yet.
     * <p>
     * In the case of this unlikely event, the new message that should be passed to {@link Message#editMessage(Message)}
     * is saved here. After the {@link RestAction#queue() queue()} resolves, it will check to see if this is
     * <code>null</code>. If it isn't, {@link #replyMessage} will be edited to reflect the message stored in this
     * request.
     */
    private Message updateRequest = null;

    /**
     * This is very similar to {@link #updateRequest}. When a user deletes their command request, the bot's reply should
     * also be deleted. However, it's possible that the user will delete their request before the bot manages to reply,
     * such that the delete call will occur before the bot sends its reply. In this case, the {@link CommandReply} would
     * not be properly deleted.
     * <p>
     * To rectify this issue, enable this if {@link #replyMessage} is <code>null</code>. After the {@link
     * RestAction#queue() queue()} in {@link #processSendResult(Message)} resolves, it will check to see if this is
     * <code>true</code>, in which case the newly set {@link #replyMessage} will be deleted.
     */
    private boolean deleteRequest = false;

    private CommandReply(String command, MessageChannel channel, Message dataMessage) {
        this.command = command;
        this.channel = channel;
        this.dataMessage = dataMessage;
    }

    private CommandReply(String command, MessageChannel channel, Message dataMessage, long targetMessageId) {
        this(command, channel, dataMessage);
        this.targetMessageId = targetMessageId;
    }

    /**
     * Create a new {@link CommandReply} instance with the necessary information to {@link #send()} it.
     *
     * @param command the command a user requested
     * @param channel the channel to {@link #send()} the reply in
     * @param message the {@link #dataMessage message} to send
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply of(String command, MessageChannel channel, Message message) {
        return new CommandReply(command, channel, message);
    }

    /**
     * Overloaded method for {@link #of(String, MessageChannel, Message)} using an {@link MessageBuilder#build()
     * unbuilt} {@link MessageBuilder}.
     *
     * @param command the command a user requested
     * @param channel the channel to {@link #send()} the reply in
     * @param message the {@link #dataMessage message} to send
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply of(String command, MessageChannel channel, MessageBuilder message) {
        return of(command, channel, message.build());
    }

    /**
     * Overloaded method for {@link #of(String, MessageChannel, Message)} using an {@link EmbedBuilder#build() unbuilt}
     * {@link EmbedBuilder}.
     *
     * @param command the command a user requested
     * @param channel the channel to {@link #send()} the reply in
     * @param message the {@link #dataMessage message} to send
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply of(String command, MessageChannel channel, EmbedBuilder message) {
        return of(command, channel, new MessageBuilder(message).build());
    }

    /**
     * Overloaded method for {@link #of(String, MessageChannel, Message)} using a {@link MessageEmbed}.
     *
     * @param command the command a user requested
     * @param channel the channel to {@link #send()} the reply in
     * @param message the {@link #dataMessage message} to send
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply of(String command, MessageChannel channel, MessageEmbed message) {
        return of(command, channel, new MessageBuilder(message).build());
    }

    /**
     * Overloaded method for {@link #of(String, MessageChannel, Message)} using a {@link CharSequence}.
     *
     * @param command the command a user requested
     * @param channel the channel to {@link #send()} the reply in
     * @param message the {@link #dataMessage message} to send
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply of(String command, MessageChannel channel, CharSequence message) {
        return of(command, channel, new MessageBuilder(message).build());
    }

    /**
     * Similar to {@link #of(String, MessageChannel, Message)}, this creates a {@link CommandReply} instance with the
     * necessary information to {@link #send()} it. However, this also provides information for a target message to
     * allow {@link #reply() replying} as well.
     *
     * @param command         the command a user requested
     * @param channel         the channel to {@link #send()} the reply in
     * @param message         the {@link #dataMessage message} to send
     * @param targetMessageId the {@link #targetMessageId id} of the message to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageChannel channel, Message message, long targetMessageId) {
        return new CommandReply(command, channel, message, targetMessageId);
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using an {@link
     * MessageBuilder#build() unbuilt} {@link MessageBuilder}.
     *
     * @param command         the command a user requested
     * @param channel         the channel to {@link #send()} the reply in
     * @param message         the {@link #dataMessage message} to send
     * @param targetMessageId the {@link #targetMessageId id} of the message to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageChannel channel, MessageBuilder message, long targetMessageId) {
        return ofReply(command, channel, message.build(), targetMessageId);
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using an {@link
     * EmbedBuilder#build() unbuilt} {@link EmbedBuilder}.
     *
     * @param command         the command a user requested
     * @param channel         the channel to {@link #send()} the reply in
     * @param message         the {@link #dataMessage message} to send
     * @param targetMessageId the {@link #targetMessageId id} of the message to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageChannel channel, EmbedBuilder message, long targetMessageId) {
        return ofReply(command, channel, new MessageBuilder(message).build(), targetMessageId);
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using a {@link MessageEmbed}.
     *
     * @param command         the command a user requested
     * @param channel         the channel to {@link #send()} the reply in
     * @param message         the {@link #dataMessage message} to send
     * @param targetMessageId the {@link #targetMessageId id} of the message to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageChannel channel, MessageEmbed message, long targetMessageId) {
        return ofReply(command, channel, new MessageBuilder(message).build(), targetMessageId);
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using a {@link CharSequence}.
     *
     * @param command         the command a user requested
     * @param channel         the channel to {@link #send()} the reply in
     * @param message         the {@link #dataMessage message} to send
     * @param targetMessageId the {@link #targetMessageId id} of the message to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageChannel channel, CharSequence message, long targetMessageId) {
        return ofReply(command, channel, new MessageBuilder(message).build(), targetMessageId);
    }

    /**
     * Similar to {@link #of(String, MessageChannel, Message)}, this creates a {@link CommandReply} instance with the
     * necessary information to {@link #send()} it. However, this also provides information for a target message to
     * allow {@link #reply() replying} as well.
     *
     * @param command       the command a user requested
     * @param message       the {@link #dataMessage message} to send
     * @param targetMessage the {@link Message} to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, Message message, Message targetMessage) {
        return ofReply(command, targetMessage.getChannel(), message, targetMessage.getIdLong());
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using an {@link
     * MessageBuilder#build() unbuilt} {@link MessageBuilder} and raw {@link Message targetMessage}.
     *
     * @param command       the command a user requested
     * @param message       the {@link #dataMessage message} to send
     * @param targetMessage the {@link Message} to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageBuilder message, Message targetMessage) {
        return ofReply(command, targetMessage.getChannel(), message.build(), targetMessage.getIdLong());
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using an {@link
     * EmbedBuilder#build() unbuilt} {@link EmbedBuilder} and raw {@link Message targetMessage}.
     *
     * @param command       the command a user requested
     * @param message       the {@link #dataMessage message} to send
     * @param targetMessage the {@link Message} to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, EmbedBuilder message, Message targetMessage) {
        return ofReply(command, targetMessage.getChannel(), new MessageBuilder(message).build(), targetMessage.getIdLong());
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using a {@link MessageEmbed} and
     * raw {@link Message targetMessage}.
     *
     * @param command       the command a user requested
     * @param message       the {@link #dataMessage message} to send
     * @param targetMessage the {@link Message} to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, MessageEmbed message, Message targetMessage) {
        return ofReply(command, targetMessage.getChannel(), new MessageBuilder(message).build(), targetMessage.getIdLong());
    }

    /**
     * Overloaded method for {@link #ofReply(String, MessageChannel, Message, long)} using a {@link CharSequence} and
     * raw {@link Message targetMessage}.
     *
     * @param command       the command a user requested
     * @param message       the {@link #dataMessage message} to send
     * @param targetMessage the {@link Message} to reply to
     * @return a new {@link CommandReply} instance
     */
    public static CommandReply ofReply(String command, CharSequence message, Message targetMessage) {
        return ofReply(command, targetMessage.getChannel(), new MessageBuilder(message).build(), targetMessage.getIdLong());
    }

    /**
     * Send this {@link CommandReply} to the appropriate {@link #channel} in Discord, and update {@link #replyMessage}
     * with Discord's response.
     *
     * @return this {@link CommandReply} instance for chaining
     */
    public CommandReply send() {
        channel.sendMessage(dataMessage).queue(this::processSendResult);
        return this;
    }

    /**
     * Send this {@link CommandReply} to the {@link #channel} in Discord as a {@link Message#reply(Message) reply} to
     * the {@link #targetMessageId targetMessage}, and update {@link #replyMessage} with Discord's response. Warning:
     * this will ping the author of the target message. To control that behavior, use {@link #reply(boolean)}.
     * <p>
     * Note: this only works if {@link #targetMessageId} was set properly when creating this {@link CommandReply}
     * instance, using one of the {@link #ofReply(String, MessageChannel, Message, long) ofReply()} methods. If this
     * {@link CommandReply} was created through a standard {@link #of(String, MessageChannel, Message) of()} method, no
     * {@link #targetMessageId targetMessage} will have been specified, and this method will fail to send the message.
     * <p>
     * If you need to send a reply but did not specify a {@link #targetMessageId}, use {@link #reply(Message)} instead.
     *
     * @return this {@link CommandReply} instance for chaining
     */
    public CommandReply reply() {
        return reply(true);
    }

    /**
     * Send this {@link CommandReply} as a reply to a custom <code>targetMessage</code>. This method should be used when
     * {@link #targetMessageId} was not set because this {@link CommandReply} was created through an {@link
     * #ofReply(String, MessageChannel, Message, long) ofReply()} method. Warning: this will ping the author of the
     * original message being replied to. To control that behavior, use {@link #reply(Message, boolean)}.
     * <p>
     * If {@link #targetMessageId} was set and you wish to reply to that message, use {@link #reply()} instead.
     * <p>
     * Note: if the given <code>targetMessage</code> is not in the same {@link MessageChannel} as the {@link #channel}
     * that this {@link CommandReply} was created for, {@link #channel} will be overwritten to the new channel.
     *
     * @param targetMessage the message to reply to
     * @return this {@link CommandReply} instance for chaining
     */
    public CommandReply reply(@Nonnull Message targetMessage) {
        return reply(targetMessage, true);
    }

    /**
     * This is identical to {@link #reply()}, except that you can control whether the author of the original message
     * being replied to is pinged.
     *
     * @param enableMention <code>true</code> to ping the user, <code>false</code> to not ping them
     * @return this {@link CommandReply} instance for chaining
     */
    public CommandReply reply(boolean enableMention) {
        channel.sendMessage(dataMessage).referenceById(targetMessageId).mentionRepliedUser(enableMention)
                .queue(this::processSendResult);
        return this;
    }

    /**
     * This is identical to {@link #reply(Message)}, except that you can control whether the author of the original
     * message being replied to is pinged.
     *
     * @param enableMention <code>true</code> to ping the user, <code>false</code> to not ping them
     * @return this {@link CommandReply} instance for chaining
     */
    public CommandReply reply(@Nonnull Message targetMessage, boolean enableMention) {
        if (targetMessage.getChannel().getIdLong() != channel.getIdLong())
            channel = targetMessage.getChannel();
        targetMessageId = targetMessage.getIdLong();
        return reply(enableMention);
    }

    /**
     * This method is shared by {@link #send()} and {@link #reply()}. It handles the message returned by Discord through
     * {@link RestAction#queue()}, storing it in {@link #replyMessage}.
     *
     * @param message the message returned by Discord
     */
    private void processSendResult(Message message) {
        replyMessage = message;
        if (deleteRequest) {
            delete().queue(s -> {
            }, f -> {
            });
            deleteRequest = false;
        } else if (updateRequest != null) {
            update(updateRequest);
            updateRequest = null;
        }
    }

    /**
     * This updates the original {@link #replyMessage message} sent in response to a command.
     *
     * @param message the new message to replace the old one with
     */
    public void update(Message message) {
        // If there is no replyMessage, the CommandReply was likely just created. Save the new message for later,
        // when the .queue() in send(Message) resolves.
        if (replyMessage == null) {
            updateRequest = message;

            try {
                // Wait a moment to see if the send() method's .queue() has received a response but did not update
                // the queued message. This should almost never happen, and could only occur if the .queue() response
                // occurred at exactly the same time as this method's update was triggered.
                Thread.sleep(500);
                if (replyMessage != null && updateRequest != null) {
                    update(message);
                    updateRequest = null;
                }
            } catch (InterruptedException ignored) {
            }

            return;
        }

        // If there IS a replyMessage, proceed to edit it as usual
        replyMessage.editMessage(message).queue(
                m -> replyMessage = m,
                f -> {
                    // It's possible that this message has since been deleted and an error will be thrown. If so,
                    // ignore it.
                }
        );

        // Update dataMessage to reflect the new message
        dataMessage = message;
    }

    /**
     * Overloaded method for {@link #update(Message)}. This method updates this {@link CommandReply} and replaces it
     * with the {@link #dataMessage} from another {@link CommandReply}.
     *
     * @param commandReply the new {@link CommandReply} instance containing the new message to use
     */
    public void update(CommandReply commandReply) {
        update(commandReply.dataMessage);
    }

    /**
     * Delete the {@link CommandReply reply} made to a user's command request. This is typically done because the user
     * deleted their initial message with the command, and so the bot's response would look out of place.
     * <p>
     * This method returns an {@link AuditableRestAction}. You will need to {@link AuditableRestAction#queue() queue}
     * the result to send the delete request to Discord.
     */
    public AuditableRestAction<Void> delete() {
        // Just like with update(), it's possible that the replyMessage has not been set yet, in which case it cannot
        // be deleted. If this is the case, enable a delete request for later.
        if (replyMessage == null) {
            deleteRequest = true;
            try {
                Thread.sleep(500);
                if (replyMessage != null && deleteRequest)
                    delete();
            } catch (InterruptedException ignored) {
            }
            return null;
        }

        // If there IS a replyMessage, delete it, along with this entire CommandReply instance
        return replyMessage.delete();
    }


    /**
     * Get the {@link #command}
     *
     * @return the name of the {@link #command} sent by a user that triggered this reply
     */
    public String getCommand() {
        return command;
    }

    /**
     * This checks to see if a given {@link String} {@link String#startsWith(String) starts} with the {@link #command}
     * associated with this {@link CommandReply}.
     *
     * @param command the given command to check against this one
     * @return <code>true</code> if the command matches; <code>false</code> otherwise
     */
    public boolean commandMatches(@Nonnull String command) {
        return command.startsWith(this.command);
    }

    /**
     * This is an overloaded method for {@link #commandMatches(String)}, which allows the command to be presented as an
     * array of strings rather than a single one. The arguments are joined with single spaces, and then checked to see
     * if they begin with {@link #command}.
     *
     * @param args the arguments of a command message sent by a user
     * @return <code>true</code> if the command matches; <code>false</code> otherwise
     */
    public boolean commandMatches(@Nonnull String[] args) {
        String[] command = this.command.split("\\s+");

        if (args.length < command.length)
            return false;

        for (int i = 0; i < command.length; i++)
            if (!command[i].equalsIgnoreCase(args[i]))
                return false;
        return true;
    }
}
