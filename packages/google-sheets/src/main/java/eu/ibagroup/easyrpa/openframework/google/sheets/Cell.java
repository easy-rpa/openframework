package eu.ibagroup.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.NumberFormats;
import eu.ibagroup.easyrpa.openframework.google.sheets.utils.SpreadsheetDateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents specific cell of Spreadsheet document and provides functionality to work with it.
 */
public class Cell {

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
     * {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    public Object getValue() {
        return getValue(Object.class);
    }

    /**
     * Gets the value of this cell and converts it to the type specified by {@code valueType}.
     * <p>
     * If {@code valueType} is <b>{@code String.class}</b>, <b>{@code Byte.class}</b>, <b>{@code Short.class}</b>,
     * <b>{@code Integer.class}</b>, <b>{@code Long.class}</b>, <b>{@code Float.class}</b> or <b>{@code Double.class}</b>
     * this method performs automatic conversion of cell value to corresponding type or return {@code null} if
     * the conversion fails.
     * <p>
     * For other types it performs simple type casting of cell value to {@code T} or throws {@code ClassCastException}
     * if such type casting is not possible.
     *
     * @param valueType class instance of return value.
     * @param <T>       type of return value. Defined by value of {@code valueType}.
     * @return value of this cell. The class of return value is defined by {@code valueType}. If the actual class
     * of cell value is different from {@code valueType} the automatic conversion will be applied.
     * @throws ClassCastException if {@code T} is different from String or Number and the value of cell cannot be
     *                            cast to {@code T}.
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
            return (T) thisCell.getValueAsNumeric(valueType);
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

            } else if (value instanceof Date || value instanceof LocalDate || value instanceof LocalDateTime) {
                double date;
                NumberFormats dateFormat;
                if (value instanceof LocalDateTime) {
                    date = SpreadsheetDateUtil.getSpreadsheetDate((LocalDateTime) value);
                    dateFormat = NumberFormats.DATE_TIME;
                } else if (value instanceof LocalDate) {
                    date = SpreadsheetDateUtil.getSpreadsheetDate((LocalDate) value);
                    dateFormat = NumberFormats.DATE;
                } else {
                    date = SpreadsheetDateUtil.getSpreadsheetDate((Date) value);
                    dateFormat = NumberFormats.DATE_TIME;
                }
                fieldsToUpdate.addAll(checkAndSetNumberFormat(gCell, dateFormat));
                gCell.setUserEnteredValue(new ExtendedValue().setNumberValue(date));

            } else if (value instanceof Number) {
                fieldsToUpdate.addAll(checkAndSetNumberFormat(gCell, NumberFormats.NUMBER));
                gCell.setUserEnteredValue(new ExtendedValue().setNumberValue(((Number) value).doubleValue()));

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
     * @return {@code true} if this cell is not defined, blank or has empty value. Returns {@code false} otherwise.
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
        return gCell.getFormattedValue() == null || gCell.getFormattedValue().isEmpty();
    }

    /**
     * Checks whether formula is specified for this cell.
     *
     * @return {@code true} if the formula is specified for this cell or {@code false} otherwise.
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
     * @return string with specified for this cell formula or {@code null} if cell does not have formula.
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
     * @return {@code true} if this cell is merged with other neighbour cells or {@code false} otherwise.
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
     * @return object representing merged region of this cell or {@code null} if this cell is not merged with
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
     * @return object representing top-left cell of merged region of this cell or {@code null} if this cell is
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
     * {@code Double}, {@code Boolean}, {@code LocalDateTime} or {@code String}.
     */
    private Object getTypedValue() {
        CellData gCell = getGCell();
        if (gCell == null || gCell.size() == 0) {
            return null;
        }
        Object value;

        ExtendedValue extendedValue = gCell.getEffectiveValue();
        if (extendedValue != null) {
            if (extendedValue.getNumberValue() != null) {
                NumberFormat numberFormat = gCell.getEffectiveFormat().getNumberFormat();
                if (numberFormat != null && (NumberFormats.DATE_TIME.name().equals(numberFormat.getType())
                        || NumberFormats.DATE.name().equals(numberFormat.getType())
                        || NumberFormats.TIME.name().equals(numberFormat.getType()))) {
                    value = SpreadsheetDateUtil.getLocalDateTime(extendedValue.getNumberValue());
                } else {
                    value = extendedValue.getNumberValue();
                }
            } else if (extendedValue.getBoolValue() != null) {
                value = extendedValue.getBoolValue();
            } else if (extendedValue.getErrorValue() != null) {
                value = extendedValue.getErrorValue().getMessage();
            } else if (extendedValue.getStringValue() != null) {
                value = extendedValue.getStringValue();
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
     * The result string value looks the same as human can see it in the cell of Google Sheets in browser.
     *
     * @return current value of this cell as string.
     */
    private String getValueAsString() {
        CellData gCell = getGCell();
        if (gCell == null || gCell.size() == 0) {
            return "";
        }
        return gCell.getFormattedValue();
    }

    /**
     * Gets the value of this cell converted as numeric.
     *
     * @return current value of this cell as numeric or {@code null} if the value cannot be converted to numeric.
     */
    @SuppressWarnings("unchecked")
    private <T> T getValueAsNumeric(Class<T> numberClass) {
        CellData gCell = getGCell();
        if (gCell == null || gCell.size() == 0) {
            return null;
        }
        if (gCell.getEffectiveValue() != null && gCell.getEffectiveValue().getNumberValue() != null) {
            Double value = gCell.getEffectiveValue().getNumberValue();
            try {
                if (Byte.class.isAssignableFrom(numberClass)) return (T) Byte.valueOf(value.byteValue());
                if (Short.class.isAssignableFrom(numberClass)) return (T) Short.valueOf(value.shortValue());
                if (Integer.class.isAssignableFrom(numberClass)) return (T) Integer.valueOf(value.intValue());
                if (Long.class.isAssignableFrom(numberClass)) return (T) Long.valueOf(value.longValue());
                if (Float.class.isAssignableFrom(numberClass)) return (T) Float.valueOf(value.floatValue());
                if (Double.class.isAssignableFrom(numberClass)) return (T) value;
            } catch (NumberFormatException ignore) {
            }
        } else if (gCell.getFormattedValue() != null) {
            String value = gCell.getFormattedValue();
            try {
                if (Byte.class.isAssignableFrom(numberClass)) return (T) Byte.valueOf(value);
                if (Short.class.isAssignableFrom(numberClass)) return (T) Short.valueOf(value);
                if (Integer.class.isAssignableFrom(numberClass)) return (T) Integer.valueOf(value);
                if (Long.class.isAssignableFrom(numberClass)) return (T) Long.valueOf(value);
                if (Float.class.isAssignableFrom(numberClass)) return (T) Float.valueOf(value);
                if (Double.class.isAssignableFrom(numberClass)) return (T) Double.valueOf(value);
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }

    /**
     * Checks that current number format of given cell is equal to {@code formatToSet}. If it's not equal then
     * changes it accordingly.
     *
     * @param gCell       source cell to check.
     * @param formatToSet the type of number format to check and set if it's needed.
     * @return the list with names of fields that are necessary to update for the cell.
     */
    private List<String> checkAndSetNumberFormat(CellData gCell, NumberFormats formatToSet) {
        NumberFormat format = gCell.getUserEnteredFormat() != null
                ? gCell.getUserEnteredFormat().getNumberFormat()
                : null;
        if (format == null || !formatToSet.name().equals(format.getType())) {
            if (format == null) {
                format = new NumberFormat();
                if (gCell.getUserEnteredFormat() == null) {
                    gCell.setUserEnteredFormat(new CellFormat());
                }
                gCell.getUserEnteredFormat().setNumberFormat(format);
            }
            format.setType(formatToSet.name());
            //Set "" pattern to tell Google API to use the default pattern.
            format.setPattern("");
            return Arrays.asList("userEnteredFormat.numberFormat.type", "userEnteredFormat.numberFormat.pattern");
        }
        return Collections.emptyList();
    }
}
