package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.*;

public class GSheetCellStyle {

    private Color backgroundColor;
    private Borders borders;
    private String horizontalAlignment;
    private String hyperlinkDisplayType;
    private NumberFormat numberFormat;
    private Padding padding;
    private String textDirection;
    private TextFormat textFormat;
    private TextRotation textRotation = new TextRotation().setAngle(0).setVertical(false);
    private String verticalAlignment;
    private String wrapStrategy;

    public GSheetCellStyle(CellFormat cellFormat) {
        backgroundColor = cellFormat.getBackgroundColor();
        borders = cellFormat.getBorders();
        horizontalAlignment = cellFormat.getHorizontalAlignment();
        hyperlinkDisplayType = cellFormat.getHyperlinkDisplayType();
        numberFormat = cellFormat.getNumberFormat();
        padding = cellFormat.getPadding();
        textDirection = cellFormat.getTextDirection();
        textFormat = cellFormat.getTextFormat();
        textRotation = cellFormat.getTextRotation();
        verticalAlignment = cellFormat.getVerticalAlignment();
        wrapStrategy = cellFormat.getWrapStrategy();
    }

    public GSheetCellStyle() {
    }

    public GSheetCellStyle(Cell cell) {
        CellFormat cellFormat = cell.getGoogleCell().getUserEnteredFormat();
        backgroundColor = cellFormat.getBackgroundColor();
        borders = cellFormat.getBorders();
        horizontalAlignment = cellFormat.getHorizontalAlignment();
        hyperlinkDisplayType = cellFormat.getHyperlinkDisplayType();
        numberFormat = cellFormat.getNumberFormat();
        padding = cellFormat.getPadding();
        textDirection = cellFormat.getTextDirection();
        textFormat = cellFormat.getTextFormat();
        textRotation = cellFormat.getTextRotation();
        verticalAlignment = cellFormat.getVerticalAlignment();
        wrapStrategy = cellFormat.getWrapStrategy();

    }

    //private Cell cell;


    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public GSheetCellStyle setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Borders getBorders() {
        return borders;
    }

    public GSheetCellStyle setBorders(Borders borders) {
        this.borders = borders;
        return this;
    }

    public String getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public GSheetCellStyle setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        return this;
    }

    public String getHyperlinkDisplayType() {
        return hyperlinkDisplayType;
    }

    public GSheetCellStyle setHyperlinkDisplayType(String hyperlinkDisplayType) {
        this.hyperlinkDisplayType = hyperlinkDisplayType;
        return this;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public GSheetCellStyle setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
        return this;
    }

    public Padding getPadding() {
        return padding;
    }

    public GSheetCellStyle setPadding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public String getTextDirection() {
        return textDirection;
    }

    public GSheetCellStyle setTextDirection(String textDirection) {
        this.textDirection = textDirection;
        return this;
    }

    public TextFormat getTextFormat() {
        return textFormat;
    }

    public GSheetCellStyle setTextFormat(TextFormat textFormat) {
        this.textFormat = textFormat;
        return this;
    }

    public TextRotation getTextRotation() {
        return textRotation;
    }

    public GSheetCellStyle setTextRotation(TextRotation textRotation) {
        this.textRotation = textRotation;
        return this;
    }

    public String getVerticalAlignment() {
        return verticalAlignment;
    }

    public GSheetCellStyle setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        return this;
    }

    public String getWrapStrategy() {
        return wrapStrategy;
    }

    public GSheetCellStyle setWrapStrategy(String wrapStrategy) {
        this.wrapStrategy = wrapStrategy;
        return this;
    }

    public void applyTo(Cell cell) {
        cell.setStyle(this);
    }
}
