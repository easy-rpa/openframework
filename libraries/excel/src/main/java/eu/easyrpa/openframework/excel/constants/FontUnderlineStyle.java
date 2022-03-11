package eu.easyrpa.openframework.excel.constants;

import org.apache.poi.ss.usermodel.Font;

/**
 * Helps to identify style of cell font underline.
 */
public enum FontUnderlineStyle {

    /**
     * Means the style of font underline is not defined.
     */
    UNDEFINED((byte) -1),

    /**
     * Means normal font without underline.
     */
    NONE(Font.U_NONE),

    /**
     * Means single (normal) underline
     */
    SINGLE(Font.U_SINGLE),

    /**
     * Means double underlined
     */
    DOUBLE(Font.U_DOUBLE),

    /**
     * Means accounting style single underline
     */
    SINGLE_ACCOUNTING(Font.U_SINGLE_ACCOUNTING),

    /**
     * Means accounting style double underline
     */
    U_DOUBLE_ACCOUNTING(Font.U_DOUBLE_ACCOUNTING);

    /**
     * Value of corresponding POI constant.
     */
    private final byte poiValue;

    FontUnderlineStyle(byte poiValue) {
        this.poiValue = poiValue;
    }

    /**
     * Gets value of corresponding POI constant.
     *
     * @return value of corresponding POI constant.
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    public byte getPoiValue() {
        return poiValue;
    }

    /**
     * Checks whether the current style of underline is defined and has corresponding POI constant.
     *
     * @return <code>true</code> if the current style of underline is defined and has corresponding POI constant or
     * <code>false</code> otherwise.
     * @see #UNDEFINED
     */
    public boolean isDefined() {
        return poiValue >= 0;
    }

    /**
     * Gets corresponding style of underline based on given value of POI constant.
     *
     * @param poiValue the value of POI constant.
     * @return corresponding style of underline.
     * @see Font#U_NONE
     * @see Font#U_SINGLE
     * @see Font#U_DOUBLE
     * @see Font#U_SINGLE_ACCOUNTING
     * @see Font#U_DOUBLE_ACCOUNTING
     */
    public static FontUnderlineStyle valueOf(byte poiValue) {
        for (FontUnderlineStyle style : values()) {
            if (style.poiValue == poiValue) {
                return style;
            }
        }
        return UNDEFINED;
    }
}
