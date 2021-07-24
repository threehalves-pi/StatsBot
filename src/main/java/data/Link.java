package data;

import commands.faq.FAQEntry;

/**
 * This class stores links to common websites that Stats Bot needs to reference in messages.
 */
public class Link {
    /**
     * Main bit.ly link to the AP Statistics survey
     */
    public static final String SURVEY = "https://bit.ly/apstat-survey";

    /**
     * Main bit.ly link to the FAQ document from the survey
     */
    public static final String FAQ = "https://bit.ly/apstats-faq";

    /**
     * The link to the FAQ Feedback survey
     */
    public static final String FAQ_FEEDBACK = "https://forms.gle/cCuRbiSnHtLwntXn9";

    /**
     * This links to the header "<code>How do I ask a good question?</code>" on the FAQ document.
     */
    public static final String FAQ_ASKING_QUESTIONS = "https://docs.google.com/document/d/1vzEopbD7GTql207P3lGk-egAjognligz1LpXyBAMCkM/edit#heading=h.5aswuxz5k9kq";

    /**
     * This is the base part of the Google docs link for the FAQ. Adding the link portion of each {@link FAQEntry} to
     * this base link produces a section link to a specific header in the FAQ document.
     */
    public static final String FAQ_HEADER = "https://docs.google.com/document/d/1vzEopbD7GTql207P3lGk-egAjognligz1LpXyBAMCkM/edit#heading=";

    /**
     * AP Stats Bot on Github
     */
    public static final String GITHUB = "https://github.com/threehalves-pi/StatsBot";
}
