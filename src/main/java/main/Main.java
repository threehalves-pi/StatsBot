package main;

import events.ButtonClick;
import events.MessageReceived;
import events.SlashCommand;
import events.Startup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static JDA JDA;
    public static BotMode MODE = BotMode.testing();

    public static void main(String[] args) throws IOException, LoginException {
        String token = new String(
                Objects.requireNonNull(Main.class.getResourceAsStream("/token.txt"))
                        .readAllBytes());

        JDA = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new MessageReceived())
                .addEventListeners(new Startup())
                .addEventListeners(new SlashCommand())
                .addEventListeners(new ButtonClick())
                .build();
    }
}
