package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import org.apache.poi.ss.usermodel.*;

import java.util.Date;

//TODO Supporting of cell constraints (data validation)
public class Cell {

    private String id;

    private Sheet parent;

    private int documentId;

    private int sheetIndex;

    private int rowIndex;

    private int columnIndex;

    protected Cell(Sheet parent, int rowIndex, int columnIndex) {
        this.parent = parent;
        this.documentId = parent.getDocument().getId();
        this.sheetIndex = parent.getIndex();
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.id = this.sheetIndex + "|" + this.rowIndex + "|" + this.columnIndex;
    }

    public ExcelDocument getDocument() {
        return parent.getDocument();
    }

    public Sheet getSheet() {
        return parent;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public CellRef getReference() {
        return new CellRef(rowIndex, columnIndex);
    }

    public void setStyle(ExcelCellStyle newStyle) {
        newStyle.applyTo(this);
    }

    public ExcelCellStyle getStyle() {
        return new ExcelCellStyle(this);
    }

    public Object getValue() {
        return getValue(Object.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> valueType) {
        if (String.class.isAssignableFrom(valueType)) {
            return (T) getValueAsString();
        } else if (Number.class.isAssignableFrom(valueType)) {
            return (T) getValueAsNumeric();
        }
        return (T) getTypedValue();
    }

    public void setValue(Object value) {
        org.apache.poi.ss.usermodel.Cell poiCell = getPoiCell();
        if (value == null) {
            poiCell.setBlank();

        } else if (value instanceof Date) {
            poiCell.setCellValue((Date) value);

        } else if (value instanceof Double) {
            poiCell.setCellValue((Double) value);

        } else if (value instanceof Boolean) {
            poiCell.setCellValue((Boolean) value);

        } else if (value instanceof String && value.toString().startsWith("=")) {
            poiCell.setCellFormula((String) value);

        } else {
            poiCell.setCellValue(value.toString());
        }
    }

    public boolean isEmpty() {
        org.apache.poi.ss.usermodel.Cell poiCell = getPoiCell();
        if (poiCell == null)
            return true;
        switch (poiCell.getCellType()) {
            case STRING:
                return poiCell.getStringCellValue().isEmpty();
            case FORMULA:
                FormulaEvaluator evaluator = POIElementsCache.getEvaluator(documentId);
                if (evaluator == null) {
                    evaluator = poiCell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                }
                CellValue cellValue = evaluator.evaluate(poiCell);
                switch (cellValue.getCellType()) {
                    case STRING:
                        return cellValue.getStringValue().isEmpty();
                    case ERROR:
                    case BLANK:
                        return true;
                    default:
                        return false;
                }
            case ERROR:
            case BLANK:
                return true;
            default:
                return false;
        }
    }

    public boolean hasFormula() {
        return getPoiCell().getCellType() == CellType.FORMULA;
    }

    public String getFormula() {
        return getPoiCell().getCellFormula();
    }

    public void setFormula(String newCellFormula) {
        getPoiCell().setCellFormula(newCellFormula);
        setValue(getValue());
    }

    public org.apache.poi.ss.usermodel.Cell getPoiCell() {
        return POIElementsCache.getPoiCell(documentId, id, sheetIndex, rowIndex, columnIndex);
    }

    private Object getTypedValue() {
        org.apache.poi.ss.usermodel.Cell poiCell = getPoiCell();
        if (poiCell == null) {
            return null;
        }
        Object value;
        switch (poiCell.getCellType()) {
            case NUMERIC:
                CellStyle cellStyle = poiCell.getCellStyle();
                short formatIndex = cellStyle.getDataFormat();
                String formatString = cellStyle.getDataFormatString();
                if (formatString == null) {
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
                if (DateUtil.isADateFormat(formatIndex, formatString)) {
                    value = poiCell.getDateCellValue();
                } else {
                    value = poiCell.getNumericCellValue();
                }
                break;
            case FORMULA:
                try {
                    FormulaEvaluator evaluator = POIElementsCache.getEvaluator(documentId);
                    if (evaluator == null) {
                        evaluator = poiCell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    }
                    CellValue cellValue = evaluator.evaluate(poiCell);
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            cellStyle = poiCell.getCellStyle();
                            formatIndex = cellStyle.getDataFormat();
                            formatString = cellStyle.getDataFormatString();
                            if (formatString == null) {
                                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                            }
                            if (DateUtil.isADateFormat(formatIndex, formatString)) {
                                value = new Date((long) cellValue.getNumberValue());
                            } else {
                                value = cellValue.getNumberValue();
                            }
                            break;
                        case BOOLEAN:
                            value = cellValue.getBooleanValue();
                            break;
                        case STRING:
                            value = cellValue.getStringValue().trim();
                            break;
                        case ERROR:
                            value = "N/A";
                            break;
                        default:
                            value = cellValue.formatAsString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    value = "#Err!";
                }
                break;
            case BOOLEAN:
                value = poiCell.getBooleanCellValue();
                break;
            case STRING:
                RichTextString str = poiCell.getRichStringCellValue();
                value = str.numFormattingRuns() > 0 ? str : str.getString();
                break;
            case ERROR:
                value = "N/A";
                break;
            default:
                value = poiCell.toString();
        }
        return value;
    }

    private String getValueAsString() {
        org.apache.poi.ss.usermodel.Cell poiCell = getPoiCell();
        if (poiCell == null) {
            return "";
        }
        switch (poiCell.getCellType()) {
            case NUMERIC:
                CellStyle cellStyle = poiCell.getCellStyle();
                short formatIndex = cellStyle.getDataFormat();
                String formatString = cellStyle.getDataFormatString();
                if (formatString == null) {
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
                return POIElementsCache.getDataFormatter(documentId)
                        .formatRawCellContents(poiCell.getNumericCellValue(), formatIndex, formatString);
            case FORMULA:
                try {
                    FormulaEvaluator evaluator = POIElementsCache.getEvaluator(documentId);
                    if (evaluator == null) {
                        evaluator = poiCell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    }
                    CellValue cellValue = evaluator.evaluate(poiCell);
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            cellStyle = poiCell.getCellStyle();
                            formatIndex = cellStyle.getDataFormat();
                            formatString = cellStyle.getDataFormatString();
                            if (formatString == null) {
                                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                            }
                            return POIElementsCache.getDataFormatter(documentId)
                                    .formatRawCellContents(cellValue.getNumberValue(), formatIndex, formatString);
                        case BOOLEAN:
                            return Boolean.toString(cellValue.getBooleanValue());
                        case STRING:
                            return cellValue.getStringValue().trim();
                        case ERROR:
                            return "N/A";
                        default:
                            return "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "N/A";
                }
            default:
                return poiCell.toString();
        }
    }

    private Double getValueAsNumeric() {
        org.apache.poi.ss.usermodel.Cell poiCell = getPoiCell();
        if (poiCell == null) {
            return null;
        }
        switch (poiCell.getCellType()) {
            case NUMERIC:
                return poiCell.getNumericCellValue();
            case FORMULA:
                try {
                    FormulaEvaluator evaluator = POIElementsCache.getEvaluator(documentId);
                    if (evaluator == null) {
                        evaluator = poiCell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    }
                    CellValue cellValue = evaluator.evaluate(poiCell);
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            return cellValue.getNumberValue();
                        case STRING:
                            try {
                                return Double.parseDouble(cellValue.getStringValue());
                            } catch (Exception e) {
                                return null;
                            }
                        default:
                            return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
