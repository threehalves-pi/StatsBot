package commands;


import announcements.AnnouncementLoader;
import data.Colors;
import data.ID;
import events.EventUtils;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Objects;

public class PrivateCommands {

    public static void testing(SlashCommandEvent event) {
        Utils.replyEphemeral(event, "There's nothing to test right now.");
    }

    public static void panel(SlashCommandEvent event) {
        OnlineStatus status = Main.JDA.getPresence().getStatus();
        event
                .reply("**Set Discord Status**")
                .addActionRow(
                        Button.secondary("panel:status:" + OnlineStatus.ONLINE.getKey(), "Online")
                                .withDisabled(status == OnlineStatus.ONLINE),
                        Button.secondary("panel:status:" + OnlineStatus.IDLE.getKey(), "Idle")
                                .withDisabled(status == OnlineStatus.IDLE),
                        Button.secondary("panel:status:" + OnlineStatus.DO_NOT_DISTURB.getKey(), "DnD")
                                .withDisabled(status == OnlineStatus.DO_NOT_DISTURB),
                        Button.secondary("panel:status:" + OnlineStatus.INVISIBLE.getKey(), "Offline")
                                .withDisabled(status == OnlineStatus.INVISIBLE))
                .queue();
    }

    public static void announcement(SlashCommandEvent event) {
        String sub = event.getSubcommandName();

        if ("list".equals(sub)) {
            StringBuilder list = new StringBuilder();
            for (int i = 0; i < AnnouncementLoader.getAnnouncementCount(); i++)
                list
                        .append("\n")
                        .append(i)
                        .append(". ")
                        .append(Utils.getEmbedTitle(AnnouncementLoader.getAnnouncementMessage(i)));

            Utils.replyEphemeral(
                    event,
                    Utils.makeEmbed(
                            "Announcement List",
                            "Here's a list of all the pre-written announcements I support, sorted by id. " +
                            "To view a specific announcement, type `/announcement get [id]`.",
                            Colors.ADMIN,
                            Utils.makeField("ID. TITLE", list.substring(1)))
            );
            return;
        }

        int id;
        try {
            id = (int) Objects.requireNonNull(event.getOption("id")).getAsLong();
            assert id >= 0 && id < AnnouncementLoader.getAnnouncementCount();
        } catch (Exception e) {
            id = AnnouncementLoader.getRandomId();
        }

        if ("get".equals(sub)) {
            Utils.replyEphemeral(event, AnnouncementLoader.getAnnouncementMessage(id));
            return;
        }

        // At this point, it is guaranteed that the user is requesting to push an announcement. This action is
        // restricted to administrators.

        if (!EventUtils.isAdmin(event.getUser())) {
            Utils.replyEphemeral(event, "Sorry, this command is reserved for administrators.");
            return;
        }

        event.deferReply(true).queue();

        try {
            TextChannel channel = Main.JDA.getTextChannelById(ID.AP_STATS_CHANNEL);
            assert channel != null;
            int idF = id;
            channel.sendMessage(AnnouncementLoader.getAnnouncementMessage(id)).queue(
                    s -> event.getHook().editOriginal(
                            "Sent announcement " + idF + " to " + channel.getAsMention() + ".").queue(),
                    f -> {
                        event.getHook().editOriginal(
                                "An error occurred while sending announcement " + idF +
                                ". Please try again later.").queue();
                        f.printStackTrace();
                    }
            );
        } catch (Exception e) {
            event.getHook().editOriginal(
                    "An error occurred while sending announcement " + id + ". Please try again later.").queue();
        }
    }
}
