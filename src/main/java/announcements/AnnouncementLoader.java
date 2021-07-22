package announcements;

import commands.faq.FAQEntry;
import data.*;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;

public class AnnouncementLoader {
    /**
     * This list contains all the possible announcement messages that the bot can choose from when it sends an
     * announcement to the AP Students server.
     */
    private static final List<Announcement> announcements = new ArrayList<>();

    public static final Logger LOG = JDALogger.getLog(AnnouncementLoader.class);
    public static final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    public static final TimerTask announcementTask = new AnnounceTimer();
    public static ScheduledFuture<?> schedule;

    /**
     * This loads all the announcements into {@link #announcements} and their corresponding frequency weights into
     * weights. They will later be used to send announcements in the <code>#apstats</code> channel.
     */
    public static void loadAnnouncements() {
        announcements.add(
                new Announcement(
                        "Survey Reminder",
                        Discord.EMOJI_MEGA,
                        "Have you taken AP Statistics already? Don't forget to complete " +
                        Utils.link("this survey", Link.SURVEY) + " to help future students!",
                        Link.SURVEY,
                        "Take the Survey!",
                        2)
                        .prepareBuild()
        );

        announcements.add(
                new Announcement(
                        "Frequently Asked Questions",
                        Discord.EMOJI_GREY_QUESTION,
                        "Are you new to AP Statistics? Check out " +
                        Utils.link("this FAQ", Link.FAQ) + " from pins with plenty of pre-written " +
                        "answers to common questions.",
                        Link.FAQ,
                        "View the FAQ!",
                        2)
                        .prepareBuild()
        );

        announcements.add(
                new Announcement(
                        "Question Help",
                        Discord.EMOJI_PENCIL,
                        "Looking for help with a specific problem? See our " +
                        Utils.link("guide", Link.FAQ_ASKING_QUESTIONS) + " to asking good questions, " +
                        "and don't forget to use " + Utils.mention(ID.AP_BOT) + "'s `;question` command to ping " +
                        "helpers.",
                        1
                ).prepareBuild()
        );

        announcements.add(
                new Announcement(
                        "Resources",
                        Discord.EMOJI_BOOKS,
                        "Looking for high quality AP Statistics resources? Check out our " +
                        Utils.link("resources list", FAQEntry.entries.get(0).getFullLink()) +
                        " for college board resources, textbooks, prep books, practice exams, curricula, and more.",
                        2
                ).prepareBuild()
        );

        announcements.add(
                new Announcement(
                        "Update AP Scores",
                        Discord.EMOJI_NEWSPAPER,
                        "Was your AP Statistics score released? Don't forget to update your " +
                        Utils.link("survey response", Link.SURVEY) + "!",
                        Link.SURVEY,
                        "AP Stats Survey",
                        1
                ).prepareBuild()
        );

        weightAnnouncements();
        LOG.info("Loaded pre-written announcements");
    }

    /**
     * Configures an instance of {@link AnnounceTimer} to send periodic announcements to Discord. This is done by simply
     * calling {@link #resetTimer()} and logging the initiation of the timer.
     */
    public static void initiateTimer() {
        resetTimer();
        LOG.info("Initiated announcement timer");
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
        return announcements.get(id).getMessage();
    }

    /**
     * Returns a random announcement message from {@link #announcements} to post to Discord. The random assignment is
     * based on the announcement weights. This method works by simply using {@link #getRandomId()} to obtain an id and
     * retrieving the corresponding announcement from the {@link #announcements} list.
     *
     * @return the announcement message to send
     */
    public static Message getAnnouncementMessage() {
        return announcements.get(getRandomId()).getMessage();
    }

    /**
     * This returns a random announcement ID based on the announcement weights.
     *
     * @return a random announcement id
     */
    public static int getRandomId() {
        double r = Math.random();
        for (int i = 0; i < announcements.size() - 1; i++)
            if (announcements.get(i).getWeight() >= r)
                return i;
            else
                r -= announcements.get(i).getWeight();

        return announcements.size() - 1;
    }

    /**
     * This returns the number of pre-written announcements that were loaded on startup with {@link
     * #loadAnnouncements()}.
     *
     * @return the size of the {@link #announcements} list
     */
    public static int getAnnouncementCount() {
        return announcements.size();
    }

    /**
     * This converts announcement weights from relative integers to a 0-1 scale designed for comparison against {@link
     * Math#random()}.
     */
    private static void weightAnnouncements() {
        // Compute the total sum of all the raw weights
        double sum = 0;
        for (Announcement a : announcements)
            sum += a.getWeight();

        // Adjust all the weights to their relative probabilities
        for (Announcement announcement : announcements)
            announcement.setWeight(announcement.getWeight() / sum);
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
