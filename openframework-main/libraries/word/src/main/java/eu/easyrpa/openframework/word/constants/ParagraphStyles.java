package eu.easyrpa.openframework.word.constants;

public enum ParagraphStyles {
    TITLE("Title"),
    HEADING_1("Heading1"),
    HEADING_2("Heading2"),
    HEADING_3("Heading3"),
    HEADING_4("Heading4"),
    HEADING_5("Heading5"),
    SUBTITLE("Subtitle"),
    QUOTE("Quote"),
    INTENSE_QUOTE("IntenseQuote"),
    NORMAL_INDENT("NormalIndent"),
    LIST_PARAGRAPH("ListParagraph");

    private final String text;

    /**
     * @param text choose on of paragraph style
     */
    ParagraphStyles(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
