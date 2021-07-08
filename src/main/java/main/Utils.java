package main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Utils {
    public static EmbedBuilder buildEmbed(String title, String description, Color color) {
        EmbedBuilder b = new EmbedBuilder();
        b.setTitle(title);
        b.setDescription(description);
        b.setColor(color);

        return b;
    }

    public static EmbedBuilder buildEmbed(String title, String description, Color color, MessageEmbed.Field... fields) {
        EmbedBuilder b = buildEmbed(title, description, color);

        for (MessageEmbed.Field f : fields)
            b.addField(f.getName(), f.getValue(), f.isInline());

        return b;
    }

    public static EmbedBuilder buildEmbed(
            String title, String description, Color color, String footer, MessageEmbed.Field... fields) {
        EmbedBuilder b = buildEmbed(title, description, color, fields);
        b.setFooter(footer);

        return b;
    }

    public static MessageEmbed.Field makeField(String name, String value, boolean inline) {
        return new MessageEmbed.Field(name, value, inline);
    }

    public static MessageEmbed.Field makeField(String name, String value, boolean inline, boolean checked) {
        return new MessageEmbed.Field(name, value, inline, checked);
    }
}
