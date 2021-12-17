package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Date;
import java.util.Objects;

/**
 * Represents specific cell of Excel document and provides functionality to work with it.
 */
//TODO Supporting of cell constraints (data validation)
public class Cell {

    /**
     * Unique id of this cell.
     */
    private String id;

    /**
     * Reference to parent sheet.
     */
    private Sheet parent;

    /**
     * Unique id of parent Excel document.
     */
    private int documentId;

    /**
     * Index of the parent sheet.
     */
    private int sheetIndex;

    /**
     * Row index of this cell (0-based)
     */
    private int rowIndex;

    /**
     * Column index of this cell (0-based)
     */
    private int columnIndex;

    /**
     * Creates a new instance of cell.
     *
     * @param parent      reference to parent sheet.
     * @param rowIndex    0-based row index of the cell.
     * @param columnIndex 0-based column index of the cell.
     */
    protected Cell(Sheet parent, int rowIndex, int columnIndex) {
        this.parent = parent;
        this.documentId = parent.getDocument().getId();
        this.sheetIndex = parent.getIndex();
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.id = POIElementsCache.getId(sheetIndex, rowIndex, columnIndex);
    }

    /**
     * Gets parent Excel document.
     *
     * @return parent Excel document.
     */
    public ExcelDocument getDocument() {
        return parent.getDocument();
    }

    /**
     * Gets parent sheet.
     *
     * @return parent sheet.
     */
    public Sheet getSheet() {
        return parent;
    }

    /**
     * Gets index of parent sheet.
     *
     * @return index of parent sheet.
     */
    public int getSheetIndex() {
        return sheetIndex;
    }

    /**
     * Gets row index of this cell.
     *
     * @return 0-based row index of this cell.
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Gets column index of this cell.
     *
     * @return 0-based column index of this cell.
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Gets reference to this cell.
     *
     * @return this cell reference.
     * @see CellRef
     */
    public CellRef getReference() {
        return new CellRef(rowIndex, columnIndex);
    }

    /**
     * Gets current style of this cell.
     *
     * @return object that keeps all style parameters of this cell.
     * @see ExcelCellStyle
     */
    public ExcelCellStyle getStyle() {
        return new ExcelCellStyle(this);
    }

    /**
     * Sets and applies specific style for this cell.
     *
     * @param newStyle the cell style to apply.
     * @see ExcelCellStyle
     */
    public void setStyle(ExcelCellStyle newStyle) {
        newStyle.applyTo(this);
    }

    /**
     * Gets the value of this cell.
     *
     * @return current value of this cell. The actual class of value is depend on cell type. Can be returned
     * <code>Double</code>, <code>Boolean</code>, <code>Date</code> or <code>String</code>.
     */
    public Object getValue() {
        return getValue(Object.class);
    }

    /**
     * Gets the value of this cell and converts it to the type specified by <code>valueType</code>.
     * <p>
     * <b>Currently only following values of <code>valueType</code> are supported:</b>
     * <table><tr><td valign="top" width="70"><b>String.class</b></td><td>performs automatic conversion of cell
     * values to string based on data format specified for corresponding cells. The output values looks the same
     * as human can see it in cell of MS Excel application.</td></tr>
     * <tr><td valign="top" width="70"><b>Double.class</b></td><td>performs automatic conversion of cell value to double.
     * If such conversion is not possible then value will be <code>null</code>.</td></tr>
     * <tr><td valign="top" width="70">Other</td><td>performs simple type casting of cell value to <code>T</code>.
     * Throws <code>ClassCastException</code> if such type casting is not possible.</tr></table>
     *
     * @param valueType class instance of return value.
     * @param <T>       type of return value. Defined by value of <code>valueType</code>.
     * @return value of this cell. The class of return value is defined by <code>valueType</code>. If the actual class
     * of cell value is different from <code>valueType</code> the automatic conversion will be applied.
     * @throws ClassCastException if <code>T</code> is different from String or Double and the value of cell cannot be
     *                            cast to <code>T</code>.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> valueType) {
        if (String.class.isAssignableFrom(valueType)) {
            return (T) getValueAsString();
        } else if (Number.class.isAssignableFrom(valueType)) {
            //TODO Fix conversion of Double to other "numbers"
            return (T) getValueAsNumeric();
        }
        return (T) getTypedValue();
    }

    /**
     * Sets the value for this cell.
     *
     * @param value the value to set.
     */
    public void setValue(Object value) {
        org.apache.poi.ss.usermodel.Cell poiCell = getPoiCell();
        if (value == null) {
            poiCell.setBlank();

        } else if (value instanceof Date) {
            poiCell.setCellValue((Date) value);

        } else if (value instanceof Number) {
            poiCell.setCellValue(((Number) value).doubleValue());

        } else if (value instanceof Boolean) {
            poiCell.setCellValue((Boolean) value);

        } else if (value instanceof String && value.toString().startsWith("=")) {
            poiCell.setCellFormula((String) value);

        } else {
            poiCell.setCellValue(value.toString());
        }
    }

    /**
     * Checks whether this cell is empty.
     *
     * @return <code>true</code> if this cell is not defined, blank or has empty value. Returns <code>false</code>
     * otherwise.
     */
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

    /**
     * Checks whether formula is specified for this cell.
     *
     * @return <code>true</code> if the formula is specified for this cell or <code>false</code> otherwise.
     */
    public boolean hasFormula() {
        return getPoiCell().getCellType() == CellType.FORMULA;
    }

    /**
     * Gets string with specified for this cell formula.
     *
     * @return string with specified for this cell formula or <code>null</code> if cell does not have formula.
     */
    public String getFormula() {
        return getPoiCell().getCellFormula();
    }

    /**
     * Sets formula for this cell.
     *
     * @param newCellFormula string with formula to set.
     */
    public void setFormula(String newCellFormula) {
        getPoiCell().setCellFormula(newCellFormula);
        setValue(getValue());
    }

    /**
     * Checks whether this cell is merged with other neighbour cells.
     *
     * @return <code>true</code> if this cell is merged with other neighbour cells or <code>false</code> otherwise.
     */
    public boolean isMerged() {
        return POIElementsCache.getMergedRegionIndex(documentId, id) != null;
    }

    /**
     * Gets merged region where this cell is hit.
     *
     * @return object representing merged region of this cell or <code>null</code> if this cell is not merged with
     * other cells.
     * @see CellRange
     */
    public CellRange getMergedRegion() {
        Integer regionIndex = POIElementsCache.getMergedRegionIndex(documentId, id);
        if (regionIndex != null) {
            CellRangeAddress ra = getSheet().getPoiSheet().getMergedRegion(regionIndex);
            return new CellRange(ra.getFirstRow(), ra.getFirstColumn(), ra.getLastRow(), ra.getLastColumn());
        }
        return null;
    }

    /**
     * Gets top-left cell of merged region where this cell is hit.
     *
     * @return object representing top-left cell of merged region of this cell or <code>null</code> if this cell is
     * not merged with other cells.
     */
    public Cell getMergedRegionCell() {
        Integer regionIndex = POIElementsCache.getMergedRegionIndex(documentId, id);
        if (regionIndex != null) {
            CellRangeAddress ra = getSheet().getPoiSheet().getMergedRegion(regionIndex);
            return new Cell(getSheet(), ra.getFirstRow(), ra.getFirstColumn());
        }
        return null;
    }

    /**
     * Gets format of this cell. Format includes information about style and data validation constraints of this cell.
     *
     * @return object representing the format of this cell.
     * @see ExcelCellsFormat
     */
    public ExcelCellsFormat getFormat() {
        return new ExcelCellsFormat(this);
    }

    /**
     * Returns underlay POI object representing this cell. This object can be used directly if some specific
     * POI functionality is necessary within RPA process.
     *
     * @return Apache POI object representing this cell.
     */
    public org.apache.poi.ss.usermodel.Cell getPoiCell() {
        Cell mergedRegionCell = getMergedRegionCell();
        if (mergedRegionCell != null && !mergedRegionCell.equals(this)) {
            return mergedRegionCell.getPoiCell();
        }
        return POIElementsCache.getPoiCell(documentId, id, sheetIndex, rowIndex, columnIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return documentId == cell.documentId &&
                sheetIndex == cell.sheetIndex &&
                rowIndex == cell.rowIndex &&
                columnIndex == cell.columnIndex &&
                Objects.equals(id, cell.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documentId, sheetIndex, rowIndex, columnIndex);
    }

    /**
     * Gets the value of this cell based on its type.
     * <p>
     * If the cell has formula, then this formula will be calculated before returning of value.
     *
     * @return current value of this cell. The actual class of value is depend on cell type. Can be returned
     * <code>Double</code>, <code>Boolean</code>, <code>Date</code> or <code>String</code>.
     */
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

    /**
     * Gets the value of this cell converted as string.
     * <p>
     * The result string value looks the same as human can see it in the cell of MS Excel application. Its taken into
     * consideration cell type and specified data format. If the cell has formula, then this formula will
     * be calculated before returning of value.
     *
     * @return current value of this cell as string.
     */
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

    /**
     * Gets the value of this cell converted as numeric.
     * <p>
     * If the cell has formula, then this formula will be calculated before returning of value.
     *
     * @return current value of this cell as numeric or <code>null</code> if the value cannot be converted to numeric.
     */
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
            case STRING:
                try {
                    return Double.parseDouble(poiCell.getStringCellValue());
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
