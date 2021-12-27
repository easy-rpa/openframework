package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.NumberFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cell {

    private String id;

    private Sheet parent;

    private String documentId;

    private int sheetIndex;

    private int rowIndex;

    private int columnIndex;

    protected Cell(Sheet parent, int rowIndex, int columnIndex) {
        this.parent = parent;
        this.documentId = parent.getParentSpreadsheet().getId();
        this.sheetIndex = parent.getIndex();
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.id = this.sheetIndex + "|" + this.rowIndex + "|" + this.columnIndex;
    }

    public SpreadsheetDocument getDocument() {
        return parent.getParentSpreadsheet();
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

    public void setStyle(GSheetCellStyle newStyle) {
       newStyle.applyTo(this);
    }

    public GSheetCellStyle getStyle() {
        return new GSheetCellStyle(this);
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
        CellData googleCell = getGoogleCell();
        if (value == null) {
            // poiCell.setBlank(); //todo check

        } else if (value instanceof Date) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(formatter.format((Date) value)));
            googleCell.setUserEnteredFormat(
                    new CellFormat().setNumberFormat(
                            new NumberFormat().setType("DATE").setPattern("dd.MM.yyyy")
                    )
            );

        } else if (value instanceof Double) {
            googleCell.setUserEnteredValue(new ExtendedValue().setNumberValue((Double) value));
            googleCell.setUserEnteredFormat(
                    new CellFormat().setNumberFormat(
                            new NumberFormat().setType("NUMBER")
                    )
            );
        } else if (value instanceof Boolean) {
            googleCell.setUserEnteredValue(new ExtendedValue().setBoolValue((Boolean) value));

        } else if (value instanceof String && value.toString().startsWith("=")) {
            googleCell.setUserEnteredValue(new ExtendedValue().setFormulaValue((String) value));

        } else {
            googleCell.setUserEnteredValue(new ExtendedValue().setStringValue(value.toString()));
        }
    }

    public boolean isEmpty() {
        CellData googleCell = getGoogleCell();
        ExtendedValue value = googleCell.getUserEnteredValue();
        if (value.isEmpty())
            return true;
        //getFormulaEvaluate??

        return value.getNumberValue() == null && value.getFormulaValue() == null && value.getBoolValue() == null
                && value.getStringValue() == null && value.getErrorValue() == null;
    }

    public boolean hasFormula() {
        return getGoogleCell().getUserEnteredValue().getFormulaValue() != null;
    }

    public String getFormula() {
        return getGoogleCell().getUserEnteredValue().getFormulaValue();
    }

    public void setFormula(String newCellFormula) {
        getGoogleCell().setUserEnteredValue(new ExtendedValue().setFormulaValue(newCellFormula));
    }

    public CellData getGoogleCell() {
        return parent.getData().getRowData().get(rowIndex).getValues().get(columnIndex);
    }

    private Object getTypedValue() {
        CellData googleCell = getGoogleCell();
        if (googleCell == null) {
            return null;
        }
        Object value = null;
        ExtendedValue extendedValue = googleCell.getUserEnteredValue();

        if(extendedValue != null) {
            if (extendedValue.getNumberValue() != null) {
                value = extendedValue.getNumberValue();
            } else if (extendedValue.getFormulaValue() != null && !extendedValue.getFormulaValue().isEmpty()) {
                value = extendedValue.getFormulaValue();
            } else if (extendedValue.getBoolValue() != null) {
                value = extendedValue.getBoolValue();
            } else if (!extendedValue.getStringValue().isEmpty()) {
                CellFormat format = googleCell.getUserEnteredFormat();
                if (format != null) {
                    String type = format.getNumberFormat().getType();
                    switch (type) {
                        case "DATE": {
                            try {
                                value = new SimpleDateFormat("dd.MM.yyyy").parse(extendedValue.getStringValue());
                            } catch (ParseException e) {
                                value = "NaN";
                            }
                            break;
                        }
                        default:
                            value = extendedValue.getStringValue();
                    }
                }
            } else if (!extendedValue.getErrorValue().isEmpty()) {
                value = extendedValue.getErrorValue().getMessage();
            } else {
                value = extendedValue.toString();
            }
        }
        return value;
    }

    private String getValueAsString() {
        CellData googleCell = getGoogleCell();
        if (googleCell == null) {
            return "";
        }
        ExtendedValue extendedValue = googleCell.getUserEnteredValue();

        if (extendedValue.getNumberValue() != null) {
            return extendedValue.getNumberValue().toString();
        } else if (extendedValue.getFormulaValue() != null && !extendedValue.getFormulaValue().isEmpty()) {
            return extendedValue.getFormulaValue();
        } else if (extendedValue.getBoolValue() != null) {
            return extendedValue.getBoolValue().toString();
        } else if (!extendedValue.getStringValue().isEmpty()) {
            CellFormat format = googleCell.getUserEnteredFormat();
            if(format != null) {
                String type = format.getNumberFormat().getType();
                switch (type) {
                    case "DATE": {
                        try {
                            return new SimpleDateFormat("dd.MM.yyyy").parse(type).toString();
                        } catch (ParseException e) {
                            return "NaN";
                        }
                    }
                }
            }
        } else if (!extendedValue.getErrorValue().isEmpty()) {
            return extendedValue.getErrorValue().getMessage();
        }
        return extendedValue.getStringValue();
    }

    private Double getValueAsNumeric() {
        CellData googleCell = getGoogleCell();
        if (googleCell == null) {
            return null;
        }
        return googleCell.getUserEnteredValue().getNumberValue();
    }
}
