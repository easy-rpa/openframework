package eu.ibagroup.easyrpa.openframework.google.sheets.constants;

import java.awt.*;

/**
 * Provides list of default colors. It helps to identify required colors to style specific cell of Google Spreadsheet
 * document.
 */
public enum Colors {

    UNSPECIFIED(null),
    BLACK(Color.BLACK),
    WHITE(Color.WHITE),
    LIGHT_GRAY(Color.LIGHT_GRAY),
    GRAY(Color.GRAY),
    DARK_GRAY(Color.DARK_GRAY),
    RED(Color.RED),
    PINK(Color.PINK),
    ORANGE(Color.ORANGE),
    YELLOW(Color.YELLOW),
    GREEN(Color.GREEN),
    MAGENTA(Color.MAGENTA),
    CYAN(Color.CYAN),
    BLUE(Color.BLUE);

    /**
     * Related color object.
     */
    private final Color color;

    Colors(Color color) {
        this.color = color;
    }

    /**
     * Gets related color object.
     *
     * @return related color object.
     */
    public Color get() {
        return color;
    }

}