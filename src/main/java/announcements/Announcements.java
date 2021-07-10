package announcements;

import Data.Colors;
import Data.ID;
import Data.Setting;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.TimerTask;
import java.util.concurrent.*;

public class Announcements {
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
     * This must be the current number of pre-written announcements that the bot can choose from and are recognized by
     * {@link #getAnnouncementMessage(int)}.
     */
    public static final int ANNOUNCEMENT_COUNT = 2;

    /**
     * Returns a pre-built {@link MessageBuilder} containing the announcement to post to Discord.
     *
     * @param id the announcement number to send. If the number is unknown, the last announcement is sent.
     * @return the announcement message to send
     */
    public static Message getAnnouncementMessage(int id) {
        MessageBuilder message = new MessageBuilder();

        switch (id) {
            case 0 -> message
                    .setEmbeds(
                            announcementEmbed("Survey Reminder", "\uD83D\uDCE3",
                                    "Have you taken AP Statistics already? Don't forget to complete " +
                                    "[this survey](" + Setting.SURVEY_LINK + ") to help future students!").build())
                    .setActionRows(
                            ActionRow.of(Button.link(Setting.SURVEY_LINK, "Take the Survey!")));

            case 1 -> message
                    .setEmbeds(
                            announcementEmbed("Frequently Asked Questions", "\u2754",
                                    "Are you new to AP Statistics? Check out " +
                                    "[this FAQ](" + Setting.FAQ_LINK + ") from pins with plenty of pre-written " +
                                    "answers to common questions.").build())
                    .setActionRows(
                            ActionRow.of(Button.link(Setting.FAQ_LINK, "View the FAQ!")));

            case 2 -> message
                    .setEmbeds(announcementEmbed("Question Help", "\uD83D\uDCDD",
                            "Looking for help with a specific problem? See our " +
                            "[guide](" + Setting.ASKING_QUESTIONS_FAQ_LINK + ") to asking good questions, " +
                            "and don't forget to use <@" + ID.AP_BOT + ">'s `;question` command to ping helpers.")
                            .build());

            default -> {
            }
        }

        return message.build();
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
        return Utils.buildEmbed(emoji + "  " + title + "  " + emoji, description, Colors.ANNOUNCEMENTS);
    }
}
