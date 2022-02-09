package eu.ibagroup.easyrpa.openframework.google.sheets.constants;

/**
 * Helps to identify how to wrap text in a cell.
 */
public enum WrapStrategy {

    /**
     * The wrap strategy is not specified.
     */
    UNSPECIFIED,

    /**
     * Lines that are longer than the cell width will be written in the next cell over, so long as that cell is empty.
     * If the next cell over is non-empty, this behaves the same as CLIP. The text will never wrap to the next
     * line unless the user manually inserts a new line.
     * <p>
     * Example:
     * <pre>
     * | First sentence. |
     * | Manual newline that is very long. <- Text continues into next cell
     * | Next newline.   |
     * </pre>
     */
    OVERFLOW_CELL,

    /**
     * This wrap strategy represents the old Google Sheets wrap strategy where words that are longer than a line are
     * clipped rather than broken. This strategy is not supported on all platforms and is being phased out.
     * <p>
     * Example:
     * <pre>
     * | Cell has a |
     * | loooooooooo| <- Word is clipped.
     * | word.      |
     * </pre>
     */
    LEGACY_WRAP,

    /**
     * Lines that are longer than the cell width will be clipped. The text will never wrap to the next line unless the
     * user manually inserts a new line.
     * <p>
     * Example:
     * <pre>
     * | First sentence. |
     * | Manual newline t| <- Text is clipped
     * | Next newline.   |
     * </pre>
     */
    CLIP,

    /**
     * Words that are longer than a line are wrapped at the character level rather than clipped.
     * <p>
     * Example:
     * <pre>
     * | Cell has a |
     * | loooooooooo| <- Word is broken.
     * | ong word.  |
     * </pre>
     */
    WRAP;

}
