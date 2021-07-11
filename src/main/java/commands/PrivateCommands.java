package commands;


import data.Colors;
import data.Setting;
import main.Main;
import main.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class PrivateCommands {
    public static void testing(SlashCommandEvent event) {
        Utils.replyEphemeral(event, Utils.buildEmbed(
                "Title",
                "Description",
                Colors.INFO,
                Utils.makeField("Field1", "text1\ntext2\n\ntext3\n"),
                Utils.makeField("Field2", "text1"),
                Utils.makeField("Field3", "abc\n"),
                Utils.makeField("Field4", "def")));
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
        Utils.replyEphemeral(event, GenericCommands.faqMessage);

        /*Utils.replyEphemeral(event,
                Utils.addLinkButton(
                        Utils.buildEmbed(
                                "Frequently Asked Questions",
                                "Looking for answers to common questions? Check out this " +
                                "handy AP Stats " + Utils.link("FAQ", Setting.FAQ_LINK) + ". It's based on data " +
                                "from a " + Utils.link("survey", Setting.SURVEY_LINK) + " of over 100 past " +
                                "students.\n\n**__Table of Contents__**",
                                Colors.INFO,
                                Utils.makeField("Resources",
                                        """
                                                1. Resource list
                                                2. What are the best resources for studying?"""),
                                Utils.makeField("Calculators and the Formula Sheet",
                                        """
                                                3. Which graphing calculator is best?
                                                4. How important is knowing how to use the calculator?
                                                5. How important is knowing how to use the formula sheet?"""),
                                Utils.makeField("Course Difficulty",
                                        """
                                                6. How hard is AP Statistics?
                                                7. What is the hardest unit?
                                                8. How much time should I spend studying?
                                                9. How can I prepare prior to starting the class?"""),
                                Utils.makeField("Questions and Etiquette",
                                        """
                                                10. How do I ask a good question?
                                                11. How do I answer a question well?
                                                12. How do I type formulas and symbols?"""),
                                Utils.makeField("Prerequisites",
                                        """
                                                13. What prerequisites should I take for AP Statistics?
                                                14. Which year should I take AP Statistics?
                                                15. Which is harder: AP Calculus or AP Statistics?
                                                16. Is taking AP Statistics along with algebra 2 a good idea?
                                                17. Is taking AP Statistics along with precalculus a good idea?
                                                18. Is taking AP Statistics along with AP Calculus a good idea?"""),
                                Utils.makeField("Self-Studying",
                                        """
                                                19. How easy is self-studying?
                                                20. Do you have any advice for self-studying?"""),
                                Utils.makeField("Miscellaneous",
                                        """
                                                21. How useful/relevant is AP Statistics?
                                                22. Is AP Statistics fun?
                                                23. Do you have any advice for new students?""")
                        ),
                        Setting.FAQ_LINK,
                        "Open the FAQ")
        );*/
    }
}
