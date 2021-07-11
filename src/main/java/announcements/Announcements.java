package announcements;

import commands.PrivateCommands;
import data.Colors;
import data.ID;
import data.Link;
import data.Setting;
import events.Startup;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;

public class Announcements {
    /**
     * This list contains all the possible announcement messages that the bot can choose from when it sends an
     * announcement to the AP Students server.
     */
    private static final List<Message> announcements = new ArrayList<>();

    /**
     * This list contains relative weights that control the frequency with which each announcement is selected to send.
     * <p><br>
     * When initially added, the weights should be simple integers with no specific scale. 1 is the default weight that
     * should be assigned to unimportant announcements. Higher numbers can be used for announcements that should be
     * displayed more frequently.
     * <p><br>
     * After creating all the announcements with {@link #loadAnnouncements()}, the weights are automatically adjusted to
     * fit on a 0-1 scale.
     */
    private static final List<Double> weights = new ArrayList<>();

    public static final Logger LOG = JDALogger.getLog(Announcements.class);
    public static final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    public static final TimerTask announcementTask = new AnnounceTimer();
    public static ScheduledFuture<?> schedule;

    /**
     * Configures an instance of {@link AnnounceTimer} to send periodic announcements to Discord. This is done by simply
     * calling {@link #resetTimer()} and logging the initiation of the timer.
     */
    public static void initiateTimer() {
        resetTimer();
        LOG.info("Initiated announcement timer.");
    }

    /**
     * Reset the timer countdown in the event that it must be postponed, such as when a user sends a message in the
     * announcements channel. This method simply cancels creates the timer again, and is identical to {@link
     * #initiateTimer()} except that it does not log any output.
     */
    public static void resetTimer() {
        // If the timer is running, cancel it
        clearTimer();

        schedule = timer.schedule(
                announcementTask,
                Setting.ANNOUNCEMENT_DELAY,
                TimeUnit.SECONDS);
    }

    /**
     * This stops the timer (if a timer exists), either in preparation for resetting it, or because channel validation
     * failed and the bot must wait until another message is sent before restarting the timer.
     */
    public static void clearTimer() {
        if (schedule != null)
            schedule.cancel(false);
    }

    /**
     * Returns the desired announcement message from {@link #announcements} to post to Discord.
     *
     * @param id the position of the desired announcement in the {@link #announcements} array
     * @return the announcement message to send
     */
    public static Message getAnnouncementMessage(int id) {
        return announcements.get(id);
    }

    /**
     * Returns a random announcement message from {@link #announcements} to post to Discord. The random assignment is
     * based on the announcement {@link #weights}.
     *
     * @return the announcement message to send
     */
    public static Message getAnnouncementMessage() {
        double r = Math.random();
        for (int i = 0; i < announcements.size(); i++)
            if (weights.get(i) >= r)
                return announcements.get(i);

        // This line shouldn't be reached--but if it is, simply return the last announcement, as there was probably
        // a rounding error.
        return announcements.get(announcements.size() - 1);
    }

    /**
     * This loads all the announcements into {@link #announcements} and their corresponding frequency weights into
     * {@link #weights}. They will later be used to send announcements in the <code>#apstats</code> channel.
     */
    public static void loadAnnouncements() {
        announcements.add(Utils.addLinkButton(
                announcementEmbed("Survey Reminder",
                        "\uD83D\uDCE3",
                        "Have you taken AP Statistics already? Don't forget to complete " +
                        Utils.link("this survey", Link.SURVEY) + " to help future students!"),
                Link.SURVEY,
                "Take the Survey!")
        );
        weights.add(2.0);

        announcements.add(Utils.addLinkButton(
                announcementEmbed("Frequently Asked Questions",
                        "\u2754",
                        "Are you new to AP Statistics? Check out " +
                        Utils.link("this FAQ", Link.FAQ) + " from pins with plenty of pre-written " +
                        "answers to common questions."),
                Link.FAQ,
                "View the FAQ!")
        );
        weights.add(1.0);

        announcements.add(Utils.buildEmbed(
                announcementEmbed("Question Help",
                        "\uD83D\uDCDD",
                        "Looking for help with a specific problem? See our " +
                        Utils.link("guide", Link.FAQ_ASKING_QUESTIONS) + " to asking good questions, " +
                        "and don't forget to use " + Utils.mention(ID.AP_BOT) + "'s `;question` command to ping " +
                        "helpers.")
                )
        );
        weights.add(1.0);

        // Compute the total sum of all the raw weights
        double sum = 0;
        for (double d : weights)
            sum += d;

        // Adjust all the weights to fit a 0-1 scale for comparison with Math.random().
        double cumulative = 0;
        for (int i = 0; i < weights.size(); i++) {
            cumulative += weights.get(i) / sum;
            weights.set(i, cumulative);
        }

        Startup.LOG.info("Loaded pre-written announcements");
    }

    /**
     * Creates a template {@link EmbedBuilder} for announcements based on a title and description. The emoji is placed
     * on either side of the title. The color of the embed is set to {@link Colors#ANNOUNCEMENTS} by default.
     *
     * @param title       the title of the embed
     * @param emoji       the emoji to be placed on either side of the title
     * @param description the description of the embed with the actual content
     * @return a brand new {@link EmbedBuilder}
     */
    private static EmbedBuilder announcementEmbed(@NotNull String title, @NotNull String emoji, @NotNull String description) {
        return Utils.makeEmbed(emoji + "  " + title + "  " + emoji, description, Colors.ANNOUNCEMENTS);
    }
}
