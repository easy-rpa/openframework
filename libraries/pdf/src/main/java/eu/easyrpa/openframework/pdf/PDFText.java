package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;

public class PDFText {
    private PDFont font;
    private String text;
    private Color color;
    private int xCoordinate;
    private int yCoordinate;
    private  float size;
    private float leading;

    public PDFText(){

    }

    public PDFText(String text, Color color, int xCoordinate, int yCoordinate, float size) {
        this.text = text;
        this.color = color;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.size = size;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public PDFont getFont() {
        return font;
    }

    public void setFont(PDFont font) {
        this.font = font;
    }

    public float getLeading() {
        return leading;
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }
}
