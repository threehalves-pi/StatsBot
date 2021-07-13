package announcements;

import data.Colors;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

public class Announcement extends MessageBuilder {
    /**
     * This is the probability that this {@link Announcement} will be chosen when randomly selecting an announcement to
     * send.
     * <p><br>
     * On initial creation of the announcement, this is a relative integer representing the announcement's frequency
     * weight. But after all the announcements are loaded, this is adjusted to a fixed probability.
     */
    private double weight;
    private Message message;

    /**
     * This creates a basic announcement using an {@link EmbedBuilder} with a title and contents. The emoji is placed on
     * either side of the title. The embed color is set to {@link Colors#ANNOUNCEMENTS} by default.
     *
     * @param title   the plain text title of the embed
     * @param emoji   the emoji to place on either side of the title
     * @param content the contents of the announcement
     */
    public Announcement(@NotNull String title, @NotNull String emoji, @NotNull String content, double weight) {
        super(Utils.makeEmbed(emoji + " " + title + " " + emoji, content, Colors.ANNOUNCEMENTS));
        this.weight = weight;
    }

    /**
     * This creates a basic announcement using an {@link EmbedBuilder}, similar to {@link
     * Announcement#Announcement(String, String, String, double)}, but with the addition of a link button to the end of
     * the message using {@link #setLinkButton(String, String)}.
     *
     * @param title   the plain text tile of the embed
     * @param emoji   the emoji to place on either side of the title
     * @param content the contents of the announcement
     * @param url     the target URL for this button
     * @param label   the text to display on the button
     */
    public Announcement(@NotNull String title, @NotNull String emoji, @NotNull String content, @NotNull String url,
                        @NotNull String label, double weight) {
        super(Utils.makeEmbed(emoji + " " + title + " " + emoji, content, Colors.ANNOUNCEMENTS));
        setLinkButton(url, label);
        this.weight = weight;
    }

    /**
     * This adds a link button to the bottom of the message. Note that this overwrites all existing {@link ActionRow}
     * instances attached to this message.
     *
     * @param url   the target URL for this button
     * @param label the text to display on the button
     * @return this {@link Announcement} instance for chaining
     */
    public Announcement setLinkButton(@NotNull String url, @NotNull String label) {
        setActionRows(ActionRow.of(Button.link(url, label)));
        return this;
    }

    /**
     * This is either the title of the first {@link MessageEmbed} associated with this {@link Announcement}, or the
     * first 15 characters of the message text. Note that the title may be empty if there is no text content associated
     * with this message, or null if {@link MessageEmbed#getTitle()} returns null.
     */
    public String getTitle() {
        if (super.embeds.size() > 0) {
            MessageEmbed embed = super.embeds.get(0);
            return embed.getTitle();
        }

        return super.builder.substring(0, 15);
    }

    /**
     * Get the announcement's probability {@link #weight}.
     *
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set the announcement's probability {@link #weight}.
     *
     * @param weight the new weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * This returns the {@link Message} built during {@link #prepareBuild()}. For a current build, use {@link
     * #build()}.
     *
     * @return the pre-built {@link Message}
     */
    public Message getMessage() {
        return message;
    }

    /**
     * This builds the {@link Announcement} into a {@link Message} and stores it for later use with {@link
     * #getMessage()}.
     *
     * @return the message
     */
    public Announcement prepareBuild() {
        message = super.build();
        return this;
    }
}
