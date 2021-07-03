package main;

import events.MessageReceived;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException, LoginException {
        String token = new String(
                Objects.requireNonNull(Main.class.getResourceAsStream("/token.txt"))
                        .readAllBytes());

        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(new MessageReceived())
                .build();
    }
}
