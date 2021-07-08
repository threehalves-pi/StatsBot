package main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Utils {
    /**
     * This creates a basic {@link EmbedBuilder} with minimal parameters.
     * @param title the title of the embed
     * @param description the description immediately below the title
     * @param color the color of the bar on the left of the embed
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
     * @param title the title of the embed
     * @param description the description immediately below the title
     * @param color the color of the bar on the left of the embed
     * @param fields one or more {@link MessageEmbed.Field} instances placed beneath the description.
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
     * @param title the title of the embed
     * @param description the description immediately below the title
     * @param color the color of the bar on the left of the embed
     * @param footer the small footer text placed at the bottom of the embed
     * @param fields one or more {@link MessageEmbed.Field} instances placed beneath the description.
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
     * @param name   the field header
     * @param value  the contents of the field
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
}
