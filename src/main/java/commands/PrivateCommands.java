package commands;


import data.Colors;
import data.Setting;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class PrivateCommands {
    public static void testing(SlashCommandEvent event) {
        Utils.replyEphemeral(event, "Test.");
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

    public static void faq(SlashCommandEvent event) {
        Utils.replyEphemeral(event,
                Utils.addLinkButton(
                        Utils.buildEmbed(
                                "Frequently Asked Questions",
                                "Looking for answers to common questions? Check out this " +
                                "handy AP Stats " + Utils.link("FAQ", Setting.FAQ_LINK) + ". It's based on data " +
                                "from a " + Utils.link("survey", Setting.SURVEY_LINK) + " of over 100 past " +
                                "students.",
                                Colors.INFO),
                        Setting.FAQ_LINK,
                        "Open the FAQ")
        );
    }
}
