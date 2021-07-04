package commands;


import main.Main;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class AdminCommands {
    public static void runControlPanel(SlashCommandEvent event) {
        OnlineStatus status = Main.jda.getPresence().getStatus();
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
