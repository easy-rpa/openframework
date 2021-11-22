package eu.ibagroup.easyrpa.openframework.excel.style;

import org.apache.poi.ss.usermodel.Font;

public enum FontOffsetType {

    UNDEFINED((short) -1),
    NORMAL(Font.SS_NONE),
    SUPERSCRIPT(Font.SS_SUPER),
    SUBSCRIPT(Font.SS_SUB);

    private final short poiValue;

    FontOffsetType(short poiValue) {
        this.poiValue = poiValue;
    }

    public short getPoiValue() {
        return poiValue;
    }

    public boolean isDefined() {
        return poiValue >= 0;
    }

    public static FontOffsetType valueOf(short poiValue) {
        for (FontOffsetType type : values()) {
            if (type.poiValue == poiValue) {
                return type;
            }
        }
        return UNDEFINED;
    }
}
