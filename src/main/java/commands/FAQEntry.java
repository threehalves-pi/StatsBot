package commands;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record FAQEntry(String type, String text, String link) {
    /**
     * This is the list of all {@link FAQEntry} instances imported from <code>faq.csv</code>. It is filled on startup by
     * {@link GenericCommands#loadFAQTableOfContents()}.
     */
    public static final List<FAQEntry> entries = new ArrayList<>();

    /**
     * This is the base part of the Google docs link for the FAQ. Adding the link portion of each {@link FAQEntry} to
     * this base link produces a section link to a specific header in the FAQ document.
     */
    public static final String BASE_LINK = "https://docs.google.com/document/d/1vzEopbD7GTql207P3lGk-egAjognligz1LpXyBAMCkM/edit#heading=";

    /**
     * This takes a line from <code>faq.csv</code> containing an faq entry in the format
     * <br>
     * <code>[type],[text],[link]</code>
     * <br>
     * and parses that into a {@link FAQEntry} record using regex. If the regex match fails, an {@link FAQEntry}
     * instance with empty parameters is returned instead.
     *
     * @param csvLine the line of csv code to parse into a type, text, and link
     * @return the newly created {@link FAQEntry} instance
     */
    public static FAQEntry of(String csvLine) {
        Matcher matcher = Pattern.compile("^(\\w+),(.+),(\\w\\.[\\w\\d]+)$").matcher(csvLine);
        if (matcher.find())
            return new FAQEntry(matcher.group(1), matcher.group(2), matcher.group(3));
        else
            return new FAQEntry("", "", "");
    }

    /**
     * This returns an {@link EmbedBuilder}-ready hyperlink using the name of this entry and the Google docs section
     * link.
     *
     * @return the hyperlink for this entry
     */
    public String getHyperlink() {
        return "[" + text + "](" + BASE_LINK + link + ")";
    }
}
