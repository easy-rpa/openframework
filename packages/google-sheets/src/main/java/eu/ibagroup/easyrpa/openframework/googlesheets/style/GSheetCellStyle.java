package eu.ibagroup.easyrpa.openframework.googlesheets.style;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.Cell;
import eu.ibagroup.easyrpa.openframework.googlesheets.internal.GSessionManager;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.openframework.googlesheets.constants.HorizontalAlignment;
import eu.ibagroup.easyrpa.openframework.googlesheets.constants.VerticalAlignment;

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
    private CellFormat cellFormat;
    private Cell cell;

    public CellFormat getCellFormat() {
        return cellFormat;
    }

    public GSheetCellStyle() {
    }

    public GSheetCellStyle(Cell cell) {
        CellFormat cellFormat = cell.getGCell().getUserEnteredFormat();
        if (cellFormat == null) {
            cellFormat = getEmptyCellFormat();
        }
        this.cellFormat = cellFormat;
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
        this.cell = cell;
    }

    public GSheetCellStyle(CellFormat cellFormat) {
        this.cellFormat = cellFormat;
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
        this.cell = null;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public GSheetCellStyle backgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Borders getBorders() {
        return borders;
    }

    public GSheetCellStyle borders(Borders borders) {
        this.borders = borders;
        return this;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment == null ? null : HorizontalAlignment.valueOf(horizontalAlignment);
    }

    public GSheetCellStyle horizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment.name();
        return this;
    }

    public String getHyperlinkDisplayType() {
        return hyperlinkDisplayType;
    }

    public GSheetCellStyle hyperlinkDisplayType(String hyperlinkDisplayType) {
        this.hyperlinkDisplayType = hyperlinkDisplayType;
        return this;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public GSheetCellStyle numberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
        return this;
    }

    public Padding getPadding() {
        return padding;
    }

    public GSheetCellStyle padding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public String getTextDirection() {
        return textDirection;
    }

    public GSheetCellStyle textDirection(String textDirection) {
        this.textDirection = textDirection;
        return this;
    }

    public TextFormat getTextFormat() {
        return textFormat;
    }

    public GSheetCellStyle textFormat(TextFormat textFormat) {
        this.textFormat = textFormat;
        return this;
    }

    public TextRotation getTextRotation() {
        return textRotation;
    }

    public GSheetCellStyle textRotation(TextRotation textRotation) {
        this.textRotation = textRotation;
        return this;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment == null ? null : VerticalAlignment.valueOf(verticalAlignment);
    }

    public GSheetCellStyle verticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment.name();
        return this;
    }

    public String getWrapStrategy() {
        return wrapStrategy;
    }

    public GSheetCellStyle wrapStrategy(String wrapStrategy) {
        this.wrapStrategy = wrapStrategy;
        return this;
    }

    //what if cell format null;
    public void applyTo(Cell cell, SpreadsheetDocument document) {
        boolean isSessionHasBeenOpened = false;
        cell.getGCell().setUserEnteredFormat(getCellFormat());
        try {
            if (!GSessionManager.isSessionOpened(document)) {
                GSessionManager.openSession(document);
                isSessionHasBeenOpened = true;
            }
            GSessionManager.getSession(document).addCellStyleRequest(cell,document);
        } finally {
            if (isSessionHasBeenOpened) {
                GSessionManager.closeSession(document);
            }
        }
    }

    public void apply(){
        if (cell != null && !cell.isEmpty()) {
            applyTo(cell, cell.getDocument());
        }
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBorders(Borders borders) {
        this.borders = borders;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment.name();
    }

    public void setHyperlinkDisplayType(String hyperlinkDisplayType) {
        this.hyperlinkDisplayType = hyperlinkDisplayType;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public void setTextDirection(String textDirection) {
        this.textDirection = textDirection;
    }

    public void setTextFormat(TextFormat textFormat) {
        this.textFormat = textFormat;
    }

    public void setTextRotation(TextRotation textRotation) {
        this.textRotation = textRotation;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment.name();
    }

    public void setWrapStrategy(String wrapStrategy) {
        this.wrapStrategy = wrapStrategy;
    }

    public void setCellFormat(CellFormat cellFormat) {
        this.cellFormat = cellFormat;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public GSheetCellStyle cell(Cell cell) {
        this.cell = cell;
        return this;
    }

    private CellFormat getEmptyCellFormat() {
        return new CellFormat()
                .setNumberFormat(new NumberFormat())
                .setTextFormat(new TextFormat())
                .setTextRotation(new TextRotation())
                .setBorders(new Borders())
                .setBackgroundColor(new Color())
                .setPadding(new Padding());
    }
}
