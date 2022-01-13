package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.*;

import java.util.ArrayList;
import java.util.List;

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
    private List<Request> requests = new ArrayList<>();
    private CellFormat cellFormat;

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

    }

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

    public List<Request> applyTo(Cell cell, SpreadsheetDocument document) {
        String sessionId = document.generateNewSessionId();
        document.openSessionIfRequired(sessionId);
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cell.getRowIndex())
                                .setEndRowIndex(cell.getRowIndex() + 1)
                                .setStartColumnIndex(cell.getColumnIndex())
                                .setEndColumnIndex(cell.getColumnIndex() + 1)
                        )
                        .setCell(cell.getGCell()
                                .setUserEnteredFormat(this.cellFormat))
                        .setFields("userEnteredValue")));
        document.closeSessionIfRequired(sessionId, requests);
        return requests;
    }

    public List<Request> applyTo(Cell cell, SpreadsheetDocument document, CellRange cellRange) {
        String sessionId = document.generateNewSessionId();
        document.openSessionIfRequired(sessionId);
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cellRange.getFirstRow())
                                .setEndRowIndex(cellRange.getLastRow())
                                .setStartColumnIndex(cellRange.getFirstCol())
                                .setEndColumnIndex(cellRange.getLastCol())
                        )
                        .setCell(cell.getGCell()
                                .setUserEnteredFormat(this.cellFormat))
                        .setFields("userEnteredValue")));
        document.closeSessionIfRequired(sessionId, requests);
        return requests;
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
