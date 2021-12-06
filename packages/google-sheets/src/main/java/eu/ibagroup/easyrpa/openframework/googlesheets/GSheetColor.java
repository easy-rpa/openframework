package eu.ibagroup.easyrpa.openframework.googlesheets;

import java.awt.*;

public class GSheetColor {
    private Float alpha;
    private Float blue;
    private Float green;
    private Float red;


    public GSheetColor(Float red, Float green, Float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 1f;
    }

    public GSheetColor(String rgbHexCode) {
        Color color = Color.decode(rgbHexCode);
        red = (float) color.getRed() / 255;
        green = (float) color.getGreen() / 255;
        blue = (float) color.getBlue() / 255;
        alpha = (float) color.getAlpha();
    }

    protected GSheetColor(com.google.api.services.sheets.v4.model.Color color) {
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();
    }

    public GSheetColor(java.awt.Color awtColor) {
        red = (float) awtColor.getRed() /255;
        green = (float) awtColor.getGreen() /255;
        blue = (float) awtColor.getBlue() /255;
        alpha = (float) awtColor.getAlpha();
    }

    public Float getRed() {
        return red;
    }

    public Float getGreen() {
        return green;
    }

    public Float getBlue() {
        return blue;
    }

    public Float getAlpha() {
        return alpha;
    }

    public boolean isDefined() {
        return (red >= 0 && green >= 0 && blue >= 0 && alpha >= 0);
    }

    public com.google.api.services.sheets.v4.model.Color toNativeColor() {
        return new com.google.api.services.sheets.v4.model.Color()
                .setRed(this.getRed())
                .setGreen(this.getGreen())
                .setBlue(this.getBlue())
                .setAlpha(this.getAlpha());
    }

    protected boolean isSameColorAs(com.google.api.services.sheets.v4.model.Color color) {
        if(color.getRed() == red && color.getGreen() == green && color.getBlue() == blue && color.getAlpha() == alpha)
            return true;
        return false;
    }
}
