package eu.easyrpa.openframework.word.constants;

public enum Color {
    RED("FF0000"),
    DARK_RED("C00000"),
    ORANGE("FFC000"),
    YELLOW("FFFF00"),
    LIGHT_GREEN("92D050"),
    GREEN("00B050"),
    LIGHT_BLUE("00B0F0"),
    BLUE("0070C0"),
    DARK_BLUE("002060"),
    PURPLE("7030A0"),
    BLACK("000000"),
    WHITE("FFFFFF");

    private final String text;

    /**
     * @param text choose on of cell color
     */
    Color(final String text) {
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
