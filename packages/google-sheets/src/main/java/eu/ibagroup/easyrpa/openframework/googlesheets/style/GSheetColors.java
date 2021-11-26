package eu.ibagroup.easyrpa.openframework.googlesheets.style;

public enum GSheetColors {

    UNDEFINED(0, (short) -1),
    BLACK1(1, (short) 1);

    private static final GSheetColor[] _colors = new GSheetColor[2];

    static {
        GSheetColors[] values = values();
        for (GSheetColors color : values()) {
            _colors[color._idx] = new GSheetColor(color.poiIndex);
        }
    }

    private final int _idx;

    private final short poiIndex;

    GSheetColors(int _idx, short poiIndex) {
        this._idx = _idx;
        this.poiIndex = poiIndex;
    }

    public GSheetColor get() {
        return _colors[_idx];
    }

    public short getPoiIndex() {
        return poiIndex;
    }

}