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
                                Colors.INFO,
                                Utils.makeField("Table of Contents", ""),
                                Utils.makeField("Resources",
                                        """
                                                Resource list
                                                1. What are the best resources for studying?"""),
                                Utils.makeField("Calculators and the Formula Sheet",
                                        """
                                                2. Which graphing calculator is best?
                                                3. How important is knowing how to use the calculator?
                                                4. How important is knowing how to use the formula sheet?"""),
                                Utils.makeField("Course Difficulty",
                                        """
                                                5. How hard is AP Statistics?
                                                6. What is the hardest unit?
                                                7. How much time should I spend studying?
                                                8. How can I prepare prior to starting the class?"""),
                                Utils.makeField("Questions and Etiquette",
                                        """
                                                9. How do I ask a good question?
                                                10. How do I answer a question well?
                                                11. How do I type formulas and symbols?"""),
                                Utils.makeField("Prerequisites",
                                        """
                                                12. What prerequisites should I take for AP Statistics?
                                                13. Which year should I take AP Statistics?
                                                14. Which is harder: AP Calculus or AP Statistics?
                                                15. Is taking AP Statistics along with algebra 2 a good idea?
                                                16. Is taking AP Statistics along with precalculus a good idea?
                                                17. Is taking AP Statistics along with AP Calculus a good idea?"""),
                                Utils.makeField("Self-Studying",
                                        """
                                                18. How easy is self-studying?
                                                19. Do you have any advice for self-studying?"""),
                                Utils.makeField("Miscellaneous",
                                        """
                                                20. How useful/relevant is AP Statistics?
                                                21. Is AP Statistics fun?
                                                22. Do you have any advice for new students?""")
                        ),
                        Setting.FAQ_LINK,
                        "Open the FAQ")
        );
    }
}
