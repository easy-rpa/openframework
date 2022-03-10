package eu.ibagroup.easyrpa.openframework.excel.constants;

import org.apache.poi.ss.usermodel.Font;

/**
 * Helps to identify type of cell font offset.
 */
public enum FontOffsetType {

    /**
     * Means the type of font offset is not defined.
     */
    UNDEFINED((short) -1),

    /**
     * Means normal font without offset.
     */
    NORMAL(Font.SS_NONE),

    /**
     * Means using of superscript
     */
    SUPERSCRIPT(Font.SS_SUPER),

    /**
     * Means using of subscript
     */
    SUBSCRIPT(Font.SS_SUB);

    /**
     * Value of corresponding POI constant.
     */
    private final short poiValue;

    FontOffsetType(short poiValue) {
        this.poiValue = poiValue;
    }

    /**
     * Gets value of corresponding POI constant.
     *
     * @return value of corresponding POI constant.
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    public short getPoiValue() {
        return poiValue;
    }

    /**
     * Checks whether the current offset type is defined and has corresponding POI constant.
     *
     * @return <code>true</code> if the current offset type is defined and has corresponding POI constant or
     * <code>false</code> otherwise.
     * @see #UNDEFINED
     */
    public boolean isDefined() {
        return poiValue >= 0;
    }

    /**
     * Gets corresponding offset type based on given value of POI constant.
     *
     * @param poiValue the value of POI constant.
     * @return corresponding offset type.
     * @see Font#SS_NONE
     * @see Font#SS_SUPER
     * @see Font#SS_SUB
     */
    public static FontOffsetType valueOf(short poiValue) {
        for (FontOffsetType type : values()) {
            if (type.poiValue == poiValue) {
                return type;
            }
        }
        return UNDEFINED;
    }
}
