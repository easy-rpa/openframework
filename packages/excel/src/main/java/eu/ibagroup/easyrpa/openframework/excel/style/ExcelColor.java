package eu.ibagroup.easyrpa.openframework.excel.style;

import java.awt.*;

public class ExcelColor {

    private short index = -1;

    private int red = -1;

    private int green = -1;

    private int blue = -1;

    public ExcelColor(short index) {
        this.index = index;
    }

    public ExcelColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ExcelColor(String rgbHexCode) {
        Color color = Color.decode(rgbHexCode);
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
    }

    public short getIndex() {
        return index;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
