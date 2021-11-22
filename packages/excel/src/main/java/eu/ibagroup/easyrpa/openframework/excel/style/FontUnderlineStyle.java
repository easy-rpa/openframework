package eu.ibagroup.easyrpa.openframework.excel.style;

import org.apache.poi.ss.usermodel.Font;

public enum FontUnderlineStyle {

    UNDEFINED((byte) -1),
    NONE(Font.U_NONE),
    SINGLE(Font.U_SINGLE),
    DOUBLE(Font.U_DOUBLE),
    SINGLE_ACCOUNTING(Font.U_SINGLE_ACCOUNTING),
    U_DOUBLE_ACCOUNTING(Font.U_DOUBLE_ACCOUNTING);

    private final byte poiValue;

    FontUnderlineStyle(byte poiValue) {
        this.poiValue = poiValue;
    }

    public byte getPoiValue() {
        return poiValue;
    }

    public boolean isDefined() {
        return poiValue >= 0;
    }

    public static FontUnderlineStyle valueOf(byte poiValue) {
        for (FontUnderlineStyle style : values()) {
            if (style.poiValue == poiValue) {
                return style;
            }
        }
        return UNDEFINED;
    }
}
