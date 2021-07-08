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

public class Utils {
    /**
     * This creates a basic {@link EmbedBuilder} with minimal parameters.
     *
     * @param title       the title of the embed
     * @param description the description immediately below the title
     * @param color       the color of the bar on the left of the embed
     * @return the newly created {@link EmbedBuilder}.
     */
    public static EmbedBuilder buildEmbed(String title, String description, Color color) {
        EmbedBuilder b = new EmbedBuilder();
        b.setTitle(title);
        b.setDescription(description);
        b.setColor(color);

        return b;
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
    public static EmbedBuilder buildEmbed(String title, String description, Color color, MessageEmbed.Field... fields) {
        EmbedBuilder b = buildEmbed(title, description, color);

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
    public static EmbedBuilder buildEmbed(
            String title, String description, Color color, String footer, MessageEmbed.Field... fields) {
        EmbedBuilder b = buildEmbed(title, description, color, fields);
        b.setFooter(footer);

        return b;
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
     * Similar to {@link #mention(long)}, this returns a Discord mention for the bot account. This is simply a
     * convenience method for <code>{@link Main#jda}.getSelfUser().getAsMention()</code>.
     *
     * @return a string that mentions the bot account
     */
    public static String mentionMe() {
        return Main.jda.getSelfUser().getAsMention();
    }

    /**
     * Adds a single link button to a {@link MessageBuilder} instance. Note that this overwrites any existing {@link
     * ActionRow} instances attached to the message (including any other buttons).
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
     * @param url the destination url when the link is clicked
     * @param text the link text
     * @return the properly formatted link
     */
    public static String link(@NotNull String url, @NotNull String text) {
        return "[" + text + "](" + url + ")";
    }
}
