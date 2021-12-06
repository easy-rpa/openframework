package eu.ibagroup.easyrpa.openframework.googlesheets.style;

import eu.ibagroup.easyrpa.openframework.googlesheets.GSheetColor;

import java.awt.*;

public enum GSheetColors {

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

    private final Color color;

    GSheetColors(Color color) {
        this.color = color;
    }

    public GSheetColor get() {
        return new GSheetColor(color);
    }



}