package commands.slash;

import data.Discord;
import events.Startup;
import main.Utils;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Instances of this class store a diagram file found in the <code>/diagrams/</code> resources directory. Each diagram
 * is composed of:
 * <ul>
 *     <li>A {@link #name}, which is shown to end users in Discord to let them select a diagram.
 *     <li>The {@link #file} that the diagram points to.
 *     <li>The {@link #value}, similar to the name, which is used by {@link #loadSelectionMenu()} to set the
 *     {@link SelectionMenu.Builder#addOption(String, String) option value}. This is used when a user chooses an option,
 *     and thus processing on a {@link SelectionMenuEvent} checks {@link #diagrams} based on their values.
 *     <li>A {@link #link} to the diagram in the assets channel in {@link Discord#STATSBOT_CENTRAL}.
 * </ul>
 */
public record Diagram(String name, File file, String value, String link) {
    /**
     * This is the list of all the {@link Diagram diagrams} from the <code>/diagrams/</code> resource directory. It is
     * set once at startup via {@link #loadDiagrams()}.
     */
    public static final List<Diagram> diagrams = new ArrayList<>();

    /**
     * This is a {@link SelectionMenu} containing all the {@link #diagrams} as options. It is created on startup by
     * {@link #loadSelectionMenu()} after {@link #loadDiagrams()} completes.
     */
    public static SelectionMenu selectionMenu;

    /**
     * The map of responses to the <code>/diagram</code> command based on their {@link ISnowflake#getIdLong() IDs}.
     */
    public static final Map<Long, InteractionHook> diagramResponses = new HashMap<>();

    /**
     * The regex {@link Pattern} used to parse diagram records from <code>diagrams.csv</code> in {@link #of(String)}.
     */
    private static final Pattern CSV_PATTERN = Pattern.compile("^(.+),(.+),(.+),([\\d]{18}/.+)$");

    /**
     * This is the first part of the link to diagram images in the assets channel in Discord. The rest of the link (with
     * the relevant message id and image name) is stored in {@link #link}.
     */
    private static final String LINK_PREFIX = "https://cdn.discordapp.com/attachments/870112032710610984/";

    /**
     * This method loads {@link #diagrams} with all the files found in the <code>/diagrams/</code> directory in the
     * module {@link Class#getResource(String) resources}. If that directory is not found or does not contain any files,
     * an error is thrown to the {@link Startup#LOG log}. Otherwise, all the files are added to the list of diagrams.
     * After that, the {@link #selectionMenu} is set via {@link #loadSelectionMenu()}.
     * <p>
     * Note that the name of the file is used as the diagram {@link #name}, meaning that the file names are important
     * and will be shown to users in Discord. Additionally, this method assumes that file names will have standard file
     * extensions and match the regex <code>/^([^.]+)\.[^.]*$/</code>, where <code>$1</code> is the file name to be
     * shown to end users.
     * <p>
     * This method is called once at {@link Startup#onReady(ReadyEvent) startup}.
     */
    public static void loadDiagrams() {
        try {
            // Locate the diagrams resource directory
            File csv = Utils.getResourceFile("/diagrams/diagrams.csv");
            Scanner in = new Scanner(csv);
            in.nextLine();

            // Load each diagram as a record instance
            while (in.hasNextLine())
                diagrams.add(of(in.nextLine()));

            in.close();

            // Load the selection menu
            loadSelectionMenu();

            Startup.LOG.info("Loaded diagram resources and selection menu");

        } catch (Exception e) {
            Startup.LOG.error("Failed to load diagrams.", e);
        }
    }

    /**
     * Create a {@link Diagram} record based on <code>csv</code> data from
     * <code>resources/diagrams/diagrams.csv</code>. The <code>csv</code> data is parsed using regex from {@link
     * #CSV_PATTERN}. If the regex expression does not match the provided <code>csvData</code>, <code>null</code> is
     * returned.
     *
     * @param csvData a line from <code>diagrams.csv</code> to be parsed into a {@link Diagram} record
     */
    public static Diagram of(@Nonnull String csvData) {
        Matcher matcher = CSV_PATTERN.matcher(csvData);
        if (matcher.find())
            return new Diagram(
                    matcher.group(1),
                    Utils.getResourceFile("/diagrams/" + matcher.group(2)),
                    matcher.group(3),
                    matcher.group(4)
            );

        return null;
    }

    /**
     * This instantiates the {@link #selectionMenu} after {@link #loadDiagrams() loading} the {@link #diagrams}. The
     * options are the {@link #name names} of all the {@link #diagrams}, and their corresponding values are set by the
     * diagram {@link #value}. Note that if the {@link #name} is longer than 25 characters, it is trimmed to only the
     * first 25.
     */
    public static void loadSelectionMenu() {
        SelectionMenu.Builder menu = SelectionMenu.create("diagram");
        for (Diagram diagram : diagrams)
            menu.addOption(diagram.name.length() > 25 ? diagram.name.substring(0, 25) : diagram.name, diagram.value);
        selectionMenu = menu.build();
    }

    /**
     * Get a {@link Diagram} in the {@link #diagrams} list that matches the given {@link #value}. If there is no
     * matching {@link Diagram}, <code>null</code> is returned.
     * <p>
     * Note that the provided <code>value</code> <i>is</i> case-sensitive.
     *
     * @param value the <code>value</code> to check for
     * @return the matching {@link Diagram}
     */
    public static Diagram getDiagram(String value) {
        for (Diagram diagram : diagrams)
            if (diagram.value.equals(value))
                return diagram;
        return null;
    }

    /**
     * Get a {@link Diagram} in the {@link #diagrams} list that matches the given {@link #link}. Note that this tests
     * against {@link #getFullLink()}, not {@link #link()}. If there is no matching {@link Diagram}, <code>null</code>
     * is returned.
     *
     * @param link the link to test
     * @return the matching {@link Diagram}
     */
    public static Diagram getDiagramFromLink(String link) {
        for (Diagram diagram : diagrams)
            if (diagram.getFullLink().equals(link))
                return diagram;
        return null;
    }

    /**
     * This combines the {@link #link} with the {@link #LINK_PREFIX} to obtain the full link to the diagram asset.
     * <p>
     * This is equivalent to <code>{@link #LINK_PREFIX} + {@link #link()}</code>
     *
     * @return the full link
     */
    public String getFullLink() {
        return LINK_PREFIX + link;
    }
}
