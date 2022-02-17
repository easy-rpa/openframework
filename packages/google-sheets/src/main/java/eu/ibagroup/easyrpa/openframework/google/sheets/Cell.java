package eu.ibagroup.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.model.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents specific cell of Spreadsheet document and provides functionality to work with it.
 */
public class Cell {

    private static final String DATE_TYPE = "DATE";

    private static final String NUMBER_TYPE = "NUMBER";

    private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

    /**
     * Reference to parent sheet.
     */
    private eu.ibagroup.easyrpa.openframework.google.sheets.Sheet parent;

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
    protected Cell(eu.ibagroup.easyrpa.openframework.google.sheets.Sheet parent, int rowIndex, int columnIndex) {
        this.parent = parent;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    /**
     * Gets parent Spreadsheet document.
     *
     * @return parent Spreadsheet document.
     */
    public SpreadsheetDocument getDocument() {
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
     * @see CellStyle
     */
    public CellStyle getStyle() {
        return new CellStyle(this);
    }

    /**
     * Sets and applies specific style for this cell.
     *
     * @param newStyle the cell style to apply.
     * @see CellStyle
     */
    public void setStyle(CellStyle newStyle) {
        newStyle.applyTo(this, true);
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
     * as human can see it in cell of Google Sheets in browser.</td></tr>
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
        Cell thisCell = getMergedRegionCell();
        if (thisCell == null) {
            thisCell = this;
        }
        if (String.class.isAssignableFrom(valueType)) {
            return (T) thisCell.getValueAsString();
        } else if (Number.class.isAssignableFrom(valueType)) {
            return (T) thisCell.getValueAsNumeric();
        }
        return (T) thisCell.getTypedValue();
    }

    /**
     * Sets the value for this cell.
     *
     * @param value the value to set.
     */
    public void setValue(Object value) {
        getDocument().batchUpdate(r -> {
            Cell thisCell = getMergedRegionCell();
            if (thisCell == null) {
                thisCell = this;
            }
            CellData gCell = thisCell.getGCell();
            if (gCell == null) {
                return;
            }
            List<String> fieldsToUpdate = new ArrayList<>();
            fieldsToUpdate.add("userEnteredValue");

            if (value == null) {
                gCell.setUserEnteredValue(new ExtendedValue().setStringValue(""));

            } else if (value instanceof Date) {
                NumberFormat format = gCell.getUserEnteredFormat() != null
                        ? gCell.getUserEnteredFormat().getNumberFormat()
                        : null;
                if (format == null) {
                    format = new NumberFormat();
                }
                if (!DATE_TYPE.equals(format.getType())) {
                    format.setType(DATE_TYPE);
                    format.setPattern(DEFAULT_DATE_FORMAT);
                    fieldsToUpdate.add("userEnteredFormat.numberFormat.type");
                    fieldsToUpdate.add("userEnteredFormat.numberFormat.pattern");
                }
                SimpleDateFormat formatter = new SimpleDateFormat(format.getPattern());
                gCell.setUserEnteredValue(new ExtendedValue().setStringValue(formatter.format((Date) value)));

            } else if (value instanceof Double) {
                NumberFormat format = gCell.getUserEnteredFormat() != null
                        ? gCell.getUserEnteredFormat().getNumberFormat()
                        : null;
                if (format == null) {
                    format = new NumberFormat();
                }
                if (!NUMBER_TYPE.equals(format.getType())) {
                    format.setType(NUMBER_TYPE);
                    fieldsToUpdate.add("userEnteredFormat.numberFormat.type");
                }
                gCell.setUserEnteredValue(new ExtendedValue().setNumberValue((Double) value));

            } else if (value instanceof Boolean) {
                gCell.setUserEnteredValue(new ExtendedValue().setBoolValue((Boolean) value));

            } else if (value instanceof String && value.toString().startsWith("=")) {
                gCell.setUserEnteredValue(new ExtendedValue().setFormulaValue((String) value));

            } else {
                gCell.setUserEnteredValue(new ExtendedValue().setStringValue(value.toString()));
            }

            r.addUpdateCellRequest(gCell, thisCell.rowIndex, thisCell.columnIndex, getSheet().getId(), fieldsToUpdate);
        });
    }

    /**
     * Checks whether this cell is empty.
     *
     * @return <code>true</code> if this cell is not defined, blank or has empty value. Returns <code>false</code>
     * otherwise.
     */
    public boolean isEmpty() {
        Cell thisCell = getMergedRegionCell();
        if (thisCell == null) {
            thisCell = this;
        }
        CellData gCell = thisCell.getGCell();
        if (gCell == null) {
            return true;
        }
        ExtendedValue value = gCell.getEffectiveValue();
        if (value.isEmpty())
            return true;
        return value.getNumberValue() == null && value.getFormulaValue() == null && value.getBoolValue() == null
                && value.getStringValue() == null && value.getErrorValue() == null;
    }

    /**
     * Checks whether formula is specified for this cell.
     *
     * @return <code>true</code> if the formula is specified for this cell or <code>false</code> otherwise.
     */
    public boolean hasFormula() {
        Cell thisCell = getMergedRegionCell();
        if (thisCell == null) {
            thisCell = this;
        }
        CellData gCell = thisCell.getGCell();
        return gCell != null && gCell.getUserEnteredValue().getFormulaValue() != null;
    }

    /**
     * Gets string with specified for this cell formula.
     *
     * @return string with specified for this cell formula or <code>null</code> if cell does not have formula.
     */
    public String getFormula() {
        Cell thisCell = getMergedRegionCell();
        if (thisCell == null) {
            thisCell = this;
        }
        CellData gCell = thisCell.getGCell();
        return gCell != null ? gCell.getUserEnteredValue().getFormulaValue() : null;
    }

    /**
     * Sets formula for this cell.
     *
     * @param newCellFormula string with formula to set.
     */
    public void setFormula(String newCellFormula) {
        Cell thisCell = getMergedRegionCell();
        if (thisCell == null) {
            thisCell = this;
        }
        CellData gCell = thisCell.getGCell();
        if (gCell != null) {
            final Cell cell = thisCell;
            final String formula = newCellFormula != null && !newCellFormula.startsWith("=")
                    ? "=" + newCellFormula
                    : newCellFormula;
            getDocument().batchUpdate(r -> {
                gCell.setUserEnteredValue(new ExtendedValue().setFormulaValue(formula));
                r.addUpdateCellRequest(gCell, cell.rowIndex, cell.columnIndex, getSheet().getId(),
                        "userEnteredValue");
            });
        }
    }

    /**
     * Checks whether this cell is merged with other neighbour cells.
     *
     * @return <code>true</code> if this cell is merged with other neighbour cells or <code>false</code> otherwise.
     */
    public boolean isMerged() {
        if (parent.getGSheet().getMerges() == null) {
            return false;
        }
        return parent.getMergedRegions().stream().anyMatch(r -> r.isInRange(rowIndex, columnIndex));
    }

    /**
     * Gets merged region where this cell is hit.
     *
     * @return object representing merged region of this cell or <code>null</code> if this cell is not merged with
     * other cells.
     * @see CellRange
     */
    public CellRange getMergedRegion() {
        if (parent.getGSheet().getMerges() == null) {
            return null;
        }
        return parent.getMergedRegions().stream()
                .filter(r -> r.isInRange(rowIndex, columnIndex))
                .findFirst().orElse(null);
    }

    /**
     * Gets top-left cell of merged region where this cell is hit.
     *
     * @return object representing top-left cell of merged region of this cell or <code>null</code> if this cell is
     * not merged with other cells.
     */
    public Cell getMergedRegionCell() {
        CellRange region = getMergedRegion();
        if (region != null) {
            if (region.getFirstRow() == rowIndex && region.getFirstCol() == columnIndex) {
                return this;
            }
            return new Cell(getSheet(), region.getFirstRow(), region.getFirstCol());
        }
        return null;
    }

    /**
     * Returns underlay Google API object representing this cell. This object can be used directly if some specific
     * Google API functionality is necessary within RPA process.
     *
     * @return Google API object representing this cell.
     */
    public CellData getGCell() {
        for (GridData gridData : parent.getGSheet().getData()) {
            if (gridData.getRowData() == null || gridData.getRowData().isEmpty()) {
                continue;
            }
            int startRow = gridData.getStartRow() != null ? gridData.getStartRow() : 0;
            int startColumn = gridData.getStartColumn() != null ? gridData.getStartColumn() : 0;
            if (rowIndex >= startRow && rowIndex < startRow + gridData.getRowData().size()) {
                RowData gRow = gridData.getRowData().get(rowIndex - startRow);
                int cellIndex = columnIndex - startColumn;
                return gRow.getValues() != null && cellIndex >= 0 && cellIndex < gRow.getValues().size()
                        ? gRow.getValues().get(cellIndex)
                        : null;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return parent.getId() == cell.parent.getId() &&
                rowIndex == cell.rowIndex &&
                columnIndex == cell.columnIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent.getId(), rowIndex, columnIndex);
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
        CellData gCell = getGCell();
        if (gCell == null || gCell.size() == 0) {
            return null;
        }
        Object value;
        //TODO gCell.getEffectiveValue() that includes formula evaluation results.
        ExtendedValue extendedValue = gCell.getUserEnteredValue();

        if (extendedValue != null) {
            if (extendedValue.getNumberValue() != null) {
                value = extendedValue.getNumberValue();
            } else if (extendedValue.getFormulaValue() != null && !extendedValue.getFormulaValue().isEmpty()) {
                value = gCell.getFormattedValue();
            } else if (extendedValue.getBoolValue() != null) {
                value = extendedValue.getBoolValue();
            } else if (!extendedValue.getStringValue().isEmpty()) {
                value = extendedValue.getStringValue();
            } else if (!extendedValue.getErrorValue().isEmpty()) {
                value = extendedValue.getErrorValue().getMessage();
            } else {
                value = extendedValue.toString();
            }
        } else {
            value = "";
        }
        return value;
    }

    /**
     * Gets the value of this cell converted as string.
     * <p>
     * The result string value looks the same as human can see it in the cell of Google Sheets in browser. Its taken
     * into consideration cell type and specified data format. If the cell has formula, then this formula will
     * be calculated before returning of value.
     *
     * @return current value of this cell as string.
     */
    private String getValueAsString() {
        CellData gCell = getGCell();
        if (gCell == null || gCell.size() == 0) {
            return "";
        }
        ExtendedValue extendedValue = gCell.getUserEnteredValue();
        if (extendedValue == null) {
            return "";
        }

        if (extendedValue.getNumberValue() != null) {
            return extendedValue.getNumberValue().toString();
        } else if (extendedValue.getFormulaValue() != null && !extendedValue.getFormulaValue().isEmpty()) {
            return gCell.getFormattedValue();
        } else if (extendedValue.getBoolValue() != null) {
            return extendedValue.getBoolValue().toString();
        } else if (!extendedValue.getStringValue().isEmpty()) {
            return extendedValue.getStringValue();
        } else if (!extendedValue.getErrorValue().isEmpty()) {
            return extendedValue.getErrorValue().getMessage();
        }
        return extendedValue.getStringValue();
    }

    /**
     * Gets the value of this cell converted as numeric.
     * <p>
     * If the cell has formula, then this formula will be calculated before returning of value.
     *
     * @return current value of this cell as numeric or <code>null</code> if the value cannot be converted to numeric.
     */
    private Double getValueAsNumeric() {
        CellData gCell = getGCell();
        if (gCell == null || gCell.size() == 0) {
            return null;
        }

        if (hasFormula()) {
            return gCell.getEffectiveValue().getNumberValue();
        } else {
            return gCell.getUserEnteredValue().getNumberValue();
        }
    }
}
