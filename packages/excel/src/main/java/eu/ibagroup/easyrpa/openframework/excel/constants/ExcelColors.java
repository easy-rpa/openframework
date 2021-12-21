package eu.ibagroup.easyrpa.openframework.excel.constants;

import eu.ibagroup.easyrpa.openframework.excel.style.ExcelColor;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Provides list of built-in Excel colors. It helps to identify required colors to style specific cell
 * of Excel Document.
 */
public enum ExcelColors {

    UNDEFINED(0, (short) -1),
    BLACK1(1, IndexedColors.BLACK1.index),
    WHITE1(2, IndexedColors.WHITE1.index),
    RED1(3, IndexedColors.RED1.index),
    BRIGHT_GREEN1(4, IndexedColors.BRIGHT_GREEN1.index),
    BLUE1(5, IndexedColors.BLUE1.index),
    YELLOW1(6, IndexedColors.YELLOW1.index),
    PINK1(7, IndexedColors.PINK1.index),
    TURQUOISE1(8, IndexedColors.TURQUOISE1.index),
    BLACK(9, IndexedColors.BLACK.index),
    WHITE(10, IndexedColors.WHITE.index),
    RED(11, IndexedColors.RED.index),
    BRIGHT_GREEN(12, IndexedColors.BRIGHT_GREEN.index),
    BLUE(13, IndexedColors.BLUE.index),
    YELLOW(14, IndexedColors.YELLOW.index),
    PINK(15, IndexedColors.PINK.index),
    TURQUOISE(16, IndexedColors.TURQUOISE.index),
    DARK_RED(17, IndexedColors.DARK_RED.index),
    GREEN(18, IndexedColors.GREEN.index),
    DARK_BLUE(19, IndexedColors.DARK_BLUE.index),
    DARK_YELLOW(20, IndexedColors.DARK_YELLOW.index),
    VIOLET(21, IndexedColors.VIOLET.index),
    TEAL(22, IndexedColors.TEAL.index),
    GREY_25_PERCENT(23, IndexedColors.GREY_25_PERCENT.index),
    GREY_50_PERCENT(24, IndexedColors.GREY_50_PERCENT.index),
    CORNFLOWER_BLUE(25, IndexedColors.CORNFLOWER_BLUE.index),
    MAROON(26, IndexedColors.MAROON.index),
    LEMON_CHIFFON(27, IndexedColors.LEMON_CHIFFON.index),
    LIGHT_TURQUOISE1(28, IndexedColors.LIGHT_TURQUOISE1.index),
    ORCHID(29, IndexedColors.ORCHID.index),
    CORAL(30, IndexedColors.CORAL.index),
    ROYAL_BLUE(31, IndexedColors.ROYAL_BLUE.index),
    LIGHT_CORNFLOWER_BLUE(32, IndexedColors.LIGHT_CORNFLOWER_BLUE.index),
    SKY_BLUE(33, IndexedColors.SKY_BLUE.index),
    LIGHT_TURQUOISE(34, IndexedColors.LIGHT_TURQUOISE.index),
    LIGHT_GREEN(35, IndexedColors.LIGHT_GREEN.index),
    LIGHT_YELLOW(36, IndexedColors.LIGHT_YELLOW.index),
    PALE_BLUE(37, IndexedColors.PALE_BLUE.index),
    ROSE(38, IndexedColors.ROSE.index),
    LAVENDER(39, IndexedColors.LAVENDER.index),
    TAN(40, IndexedColors.TAN.index),
    LIGHT_BLUE(41, IndexedColors.LIGHT_BLUE.index),
    AQUA(42, IndexedColors.AQUA.index),
    LIME(43, IndexedColors.LIME.index),
    GOLD(44, IndexedColors.GOLD.index),
    LIGHT_ORANGE(45, IndexedColors.LIGHT_ORANGE.index),
    ORANGE(46, IndexedColors.ORANGE.index),
    BLUE_GREY(47, IndexedColors.BLUE_GREY.index),
    GREY_40_PERCENT(48, IndexedColors.GREY_40_PERCENT.index),
    DARK_TEAL(49, IndexedColors.DARK_TEAL.index),
    SEA_GREEN(50, IndexedColors.SEA_GREEN.index),
    DARK_GREEN(51, IndexedColors.DARK_GREEN.index),
    OLIVE_GREEN(52, IndexedColors.OLIVE_GREEN.index),
    BROWN(53, IndexedColors.BROWN.index),
    PLUM(54, IndexedColors.PLUM.index),
    INDIGO(55, IndexedColors.INDIGO.index),
    GREY_80_PERCENT(56, IndexedColors.GREY_80_PERCENT.index),
    AUTOMATIC(57, IndexedColors.AUTOMATIC.index);

    private static final ExcelColor[] _colors = new ExcelColor[58];

    static {
        ExcelColors[] values = values();
        for (ExcelColors color : values()) {
            _colors[color._idx] = new ExcelColor(color.poiIndex);
        }
    }

    /**
     * Serial index of the color
     */
    private final int _idx;

    /**
     * Index of related built-in Excel color.
     */
    private final short poiIndex;

    ExcelColors(int _idx, short poiIndex) {
        this._idx = _idx;
        this.poiIndex = poiIndex;
    }

    /**
     * Gets related color object.
     *
     * @return related color object.
     * @see ExcelColor
     */
    public ExcelColor get() {
        return _colors[_idx];
    }

    /**
     * Gets index of related built-in Excel color.
     *
     * @return index of related built-in Excel color.
     * @see IndexedColors
     */
    public short getPoiIndex() {
        return poiIndex;
    }
}
