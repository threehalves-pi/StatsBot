package commands;


import main.Main;
import main.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

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
}
