package eu.ibagroup.easyrpa.openframework.googlesheets.style;

public class GSheetColor {
    private short index = -1;

    private short red = -1;

    private short green = -1;

    private short blue = -1;

    private short alpha = -1;

    public GSheetColor(short index) {
        this.index = index;
    }

    public GSheetColor(short red, short green, short blue, short alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
