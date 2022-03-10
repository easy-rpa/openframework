package eu.ibagroup.easyrpa.openframework.google.sheets.constants;

/**
 * Helps to identify the style of cell border.
 */
public enum BorderStyle {

    /**
     * The border is dotted.
     */
    DOTTED,

    /**
     * The border is dashed.
     */
    DASHED,

    /**
     * The border is a thin solid line.
     */
    SOLID,

    /**
     * The border is a medium solid line.
     */
    SOLID_MEDIUM,

    /**
     * The border is a thick solid line.
     */
    SOLID_THICK,

    /**
     * No border. Should be used for updating a border in order to erase it.
     */
    NONE,

    /**
     * The border is two solid lines.
     */
    DOUBLE
}
