package main;

import Data.Discord;
import Data.ID;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Bot modes control the events to which the bot responds. It consists of a set of enums under {@link Mode} that define
 * the various event types this bot uses.
 * <p><br>
 * Using the presets {@link #running()} and {@link #testing()}, it is possible to run simultaneous StatsBot instances
 * for global functionality (in the AP Students server, DMs, and elsewhere) and a separate instance for testing (in
 * StatsBot Central).
 */
public class BotMode {
    /**
     * This is the list of currently enabled modes for this {@link BotMode} instance.
     */
    private final List<Mode> modes;

    /**
     * This creates a {@link BotMode} instance with a set of enabled modes.
     *
     * @param modes the {@link Mode}(s) to enable
     */
    private BotMode(Mode... modes) {
        this.modes = Arrays.asList(modes);
    }

    /**
     * This defines a custom {@link BotMode} instance which select modes enabled.
     *
     * @param modes one or more {@link Mode} enums to enable.
     * @return the new {@link BotMode} instance
     */
    public static BotMode of(Mode... modes) {
        return new BotMode(modes);
    }

    /**
     * This defines a {@link BotMode} with all modes enabled for a fully functional bot.
     *
     * @return the new {@link BotMode} instance
     */
    public static BotMode all() {
        return new BotMode(
                Mode.SERVER_MESSAGES,
                Mode.DIRECT_MESSAGES,
                Mode.STATSBOT_CENTRAL_MESSAGES,
                Mode.GLOBAL_SLASH_COMMANDS,
                Mode.PRIVATE_SLASH_COMMANDS);
    }

    /**
     * This defines a {@link BotMode} with official global modes enabled for the AP Students server and DMs. This
     * excludes all modes associated with {@link #testing()} mode.
     *
     * @return the new {@link BotMode} instance
     */
    public static BotMode running() {
        return new BotMode(
                Mode.SERVER_MESSAGES,
                Mode.DIRECT_MESSAGES,
                Mode.GLOBAL_SLASH_COMMANDS);
    }

    /**
     * This defines a {@link BotMode} with private modes enabled for the Stats Bot Central server. This excludes all
     * modes associated with {@link #running()} mode.
     *
     * @return the new {@link BotMode} instance
     */
    public static BotMode testing() {
        return new BotMode(
                Mode.STATSBOT_CENTRAL_MESSAGES,
                Mode.PRIVATE_SLASH_COMMANDS);
    }

    /**
     * This adds one or more {@link Mode} enums to the list of enabled modes.
     *
     * @param mode one or more modes to add
     * @return this {@link BotMode} instance for chaining
     */
    public BotMode addMode(Mode mode) {
        if (!modes.contains(mode))
            modes.add(mode);
        return this;
    }

    /**
     * This checks whether the current {@link BotMode} implements the given {@link Mode}.
     *
     * @param mode the mode to check
     * @return true if that mode is allowed and should be processed; false if it should be ignored
     */
    public boolean allows(Mode mode) {
        return modes.contains(mode);
    }

    /**
     * This is a convenience method for negating {@link #allows(Mode)}.
     *
     * @param mode the mode to check
     * @return true if that mode should be ignored; false if it is allowed and should be processed
     */
    public boolean ignores(Mode mode) {
        return !allows(mode);
    }

    /**
     * This determines if a given {@link net.dv8tion.jda.api.JDA} {@link Event} should be ignored according to the bot's
     * current {@link BotMode}. If the event should be ignored, true is returned, and all event processing should cease
     * immediately.
     *
     * @param event the incoming JDA event
     * @param <T>   the event type as a subclass of {@link Event}
     * @return true if the event should be ignored; false if it should be processed as normal
     */
    public <T extends Event> boolean ignoreEvent(T event) {
        // If the event is an incoming message or button click, determine if it is a DM,
        // StatsBot Central message, or other server, and respond accordingly.
        if (event instanceof MessageReceivedEvent e)
            return ignoreEvent(e.isFromGuild(), e.getGuild());
        if (event instanceof ButtonClickEvent e)
            return ignoreEvent(e.isFromGuild(), e.getGuild());

        // If the event is a slash command, check the source
        if (event instanceof SlashCommandEvent e) {
            if (e.getGuild() != null && e.getGuild().getIdLong() == ID.STATSBOT_CENTRAL_GUILD)
                return ignores(Mode.PRIVATE_SLASH_COMMANDS);
            else
                return ignores(Mode.GLOBAL_SLASH_COMMANDS);
        }

        // If the event type is unknown, do not ignore it
        return false;
    }

    /**
     * This is simply a helper method for {@link #ignoreEvent(Event)} that allows separate event objects to be treated
     * by the same if structure as message events. Events occurring in DMs are controlled by {@link
     * Mode#DIRECT_MESSAGES}. If an event occurs in a Guild, messages in {@link Discord#STATSBOT_CENTRAL} are controlled
     * by {@link Mode#STATSBOT_CENTRAL_MESSAGES}, and all other messages by {@link Mode#SERVER_MESSAGES}.
     *
     * @param isFromGuild whether the message was sent in a {@link Guild}
     * @param guild the {@link Guild} that the message was sent in (null if not applicable)
     * @return true if the event should be ignored; false if it should be allowed and processed
     */
    private boolean ignoreEvent(boolean isFromGuild, @Nullable Guild guild) {
        if (isFromGuild)
            return ignores(Mode.DIRECT_MESSAGES);
        else if (guild != null && guild.getIdLong() == ID.STATSBOT_CENTRAL_GUILD)
            return ignores(Mode.STATSBOT_CENTRAL_MESSAGES);
        else
            return ignores(Mode.SERVER_MESSAGES);
    }

    enum Mode {
        /**
         * All Discord messages received in servers that are not {@link Discord#STATSBOT_CENTRAL}
         */
        SERVER_MESSAGES,

        /**
         * All Discord messages received in DMs
         */
        DIRECT_MESSAGES,

        /**
         * All Discord messages received in {@link Discord#STATSBOT_CENTRAL}
         */
        STATSBOT_CENTRAL_MESSAGES,

        /**
         * All global slash commands (as used in all servers)
         */
        GLOBAL_SLASH_COMMANDS,

        /**
         * Private slash commands registered to {@link Discord#STATSBOT_CENTRAL}
         */
        PRIVATE_SLASH_COMMANDS
    }
}
