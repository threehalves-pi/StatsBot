package commands.faq;

import commands.GenericCommands;

import java.util.ArrayList;
import java.util.List;

public class FAQLoader {
    /**
     * This is the list of all {@link FAQEntry} instances imported from <code>faq.csv</code>. It is filled on startup by
     * {@link GenericCommands#loadFAQTableOfContents()}.
     */
    private static final List<FAQEntry> entries = new ArrayList<>();

    /**
     * This is the same as {@link #entries}, except that it excludes categories in the FAQ document and only contains
     * questions.
     */
    private static final List<FAQEntry> questions = new ArrayList<>();
}
