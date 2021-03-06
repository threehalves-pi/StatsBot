package main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * This class contains convenience methods for performing many common tasks. All of the methods here simply make coding
 * other bot functionality slightly easier and more streamlined. Most do nothing but shorten a series of dot notation or
 * a few lines of code.
 */
public class Utils {
    /**
     * This creates a basic {@link EmbedBuilder} with minimal parameters.
     *
     * @param title       the title of the embed
     * @param description the description immediately below the title
     * @param color       the color of the bar on the left of the embed
     * @return the newly created {@link EmbedBuilder}.
     */
    public static EmbedBuilder makeEmbed(String title, String description, Color color) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color);
    }

    /**
     * This creates an {@link EmbedBuilder} with a variety of parameters.
     *
     * @param title       the title of the embed
     * @param description the description immediately below the title
     * @param color       the color of the bar on the left of the embed
     * @param fields      one or more {@link MessageEmbed.Field} instances placed beneath the description.
     * @return the newly created {@link EmbedBuilder}.
     */
    public static EmbedBuilder makeEmbed(String title, String description, Color color, MessageEmbed.Field... fields) {
        EmbedBuilder b = makeEmbed(title, description, color);

        for (MessageEmbed.Field f : fields)
            b.addField(f.getName(), f.getValue(), f.isInline());

        return b;
    }

    /**
     * This creates an {@link EmbedBuilder} with a variety of parameters.
     *
     * @param title       the title of the embed
     * @param description the description immediately below the title
     * @param color       the color of the bar on the left of the embed
     * @param footer      the small footer text placed at the bottom of the embed
     * @param fields      one or more {@link MessageEmbed.Field} instances placed beneath the description.
     * @return the newly created {@link EmbedBuilder}.
     */
    public static EmbedBuilder makeEmbed(
            String title, String description, Color color, String footer, MessageEmbed.Field... fields) {
        return makeEmbed(title, description, color, fields)
                .setFooter(footer);
    }

    /**
     * Convenience method for {@link #makeField(String, String, boolean)} that automatically sets <code>inline</code> to
     * false.
     *
     * @param name  the field header
     * @param value the contents of the field
     * @return the newly created {@link MessageEmbed.Field}.
     */
    public static MessageEmbed.Field makeField(String name, String value) {
        return makeField(name, value, false);
    }

    /**
     * Creates a field for an {@link EmbedBuilder}.
     *
     * @param name   the field header
     * @param value  the contents of the field
     * @param inline whether the field gets its own line (false) or is placed inline next to other fields (true)
     * @return the newly created {@link MessageEmbed.Field}.
     */
    public static MessageEmbed.Field makeField(String name, String value, boolean inline) {
        return new MessageEmbed.Field(name, value, inline);
    }

    /**
     * This returns a string for mentioning users based on their Discord id.
     *
     * @param id the Discord id of the user to mention
     * @return a string that mentions the user
     */
    public static String mention(long id) {
        return "<@" + id + ">";
    }

    /**
     * This returns a string for mentioning channels based on their Discord id.
     * @param id the Discord id of the channel to mention
     * @return a string that mentions that channel
     */
    public static String mentionChannel(long id) {
        return "<#" + id + ">";
    }

    /**
     * Similar to {@link #mention(long)}, this returns a Discord mention for the bot account. This is simply a
     * convenience method for <code>{@link Main#JDA}.getSelfUser().getAsMention()</code>.
     *
     * @return a string that mentions the bot account
     */
    public static String mentionMe() {
        return Main.JDA.getSelfUser().getAsMention();
    }

    /**
     * This adds a single link button to a {@link MessageBuilder} instance. Note that this overwrites any existing
     * {@link ActionRow} instances attached to the message (including any other buttons).
     *
     * @param messageBuilder the existing message to send
     * @param url            the destination url for the button
     * @param text           the text on the button
     * @return the original {@link MessageBuilder} with a link button appended and built to a {@link Message} instance
     */
    public static Message addLinkButton(
            @NotNull MessageBuilder messageBuilder, @NotNull String url, @NotNull String text) {
        return messageBuilder.setActionRows(ActionRow.of(Button.link(url, text))).build();
    }

    /**
     * This is a convenience method for {@link #addLinkButton(MessageBuilder, String, String)}. It wraps an {@link
     * EmbedBuilder} in a {@link MessageBuilder} to add a link, then returns everything as a built {@link Message} for
     * sending.
     *
     * @param embedBuilder the existing embed to send
     * @param url          the destination url for the button
     * @param text         the text on the button
     * @return the embed wrapped and built in a {@link Message} instance, with a link button at the end.
     */
    public static Message addLinkButton(
            @NotNull EmbedBuilder embedBuilder, @NotNull String url, @NotNull String text) {
        return addLinkButton(new MessageBuilder().setEmbeds(embedBuilder.build()), url, text);
    }

    // TODO move all reply methods to EventUtils
    /**
     * This is a convenience method to send an ephemeral reply to a slash command.
     *
     * @param event   the slash command event to reply to
     * @param message the message to send
     */
    public static void replyEphemeral(SlashCommandEvent event, Message message) {
        event.reply(message).setEphemeral(true).queue();
    }

    /**
     * This is a convenience method for calling {@link #replyEphemeral(SlashCommandEvent, Message)} using a {@link
     * String} message instead of a built {@link Message} instance. It sends an ephemeral reply to a slash command.
     *
     * @param event   the slash command event to reply to
     * @param message the message to send
     */
    public static void replyEphemeral(SlashCommandEvent event, String message) {
        replyEphemeral(event, new MessageBuilder(message).build());
    }

    /**
     * This is a convenience method to send an ephemeral reply to a slash command.
     *
     * @param event the slash command event to reply to
     * @param embed the message to send
     */
    public static void replyEphemeral(SlashCommandEvent event, EmbedBuilder embed) {
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    /**
     * This formats and returns a custom hyperlink for embed messages.
     * <p><br>
     * Note: Discord links for embeds are formatted:
     * <br>
     * <code>[link text](url)</code>
     * <br>
     * This means that the link text must not contain brackets.
     *
     * @param text the link text
     * @param url  the destination url when the link is clicked
     * @return the properly formatted link
     */
    public static String link(@NotNull String text, @NotNull String url) {
        return "[" + text + "](" + url + ")";
    }

    /**
     * This returns a resource {@link File} with the given name. Note that this resource file is based off the {@link
     * Utils} class with {@link #getClass()}, rather than the calling class. If the resource file was not found, a
     * {@link File} instance with an empty path name is returned instead, and an exception is printed to the console.
     * <p><br
     * <b>Note:</b> don't forget to include a <b><code>/</code></b> before the name of the file.
     *
     * @param file the name of the file to retrieve
     * @return the retrieved file, or a file with no path if a file with the given name was not found
     */
    public static @NotNull File getResourceFile(String file) {
        try {
            return new File(Objects.requireNonNull(Utils.class.getResource(file)).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return new File("");
        }
    }

    /**
     * This method wraps an {@link EmbedBuilder} in a {@link MessageBuilder} instance and builds it into a {@link
     * Message}.
     *
     * @param embed the embed to convert to a message
     * @return the fully built message for sending to Discord
     */
    public static Message buildEmbed(EmbedBuilder embed) {
        return new MessageBuilder().setEmbeds(embed.build()).build();
    }

    /**
     * This method checks to see if a given message contains at least one embed. If it does, the title of the first
     * embed in the message is returned. If there is no embed, or if the embed's title is empty, an empty string is
     * returned. This method will not return null and will not throw errors if there are no embeds.
     * @param message the message to check for embeds
     * @return the title of the first embed in the message, or an empty string if no embed or title is found
     */
    public static @NotNull String getEmbedTitle(Message message) {
        try {
            String title = message.getEmbeds().get(0).getTitle();
            return title == null ? "" : title;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * This method sends a reply to a message.
     * @param message the message to reply to
     * @param reply the reply to send
     */
    public static void reply(Message message, Message reply) {
        message.reply(reply).queue();
    }

    /**
     * This method sends a reply to a message by enclosing it in a {@link MessageBuilder} and forwarding it to
     * {@link #reply(Message, Message)}.
     * @param message the message to reply to
     * @param reply the reply to send
     */
    public static void reply(Message message, String reply) {
        reply(message, new MessageBuilder(reply).build());
    }
}
