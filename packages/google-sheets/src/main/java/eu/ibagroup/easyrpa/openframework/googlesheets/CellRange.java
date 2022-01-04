package eu.ibagroup.easyrpa.openframework.googlesheets;

import java.util.Objects;

public class CellRange {

    /**
     * The character (:) that separates the two cell references in a multi-cell area reference
     */
    private static final char CELL_DELIMITER = ':';
    private static final char SPECIAL_NAME_DELIMITER = '\'';
    private static final char SHEET_NAME_DELIMITER = '!';
    private CellRef firstCell;
    private CellRef lastCell;
    private boolean isSingleCell;


    private String ref;
    private String rowColRef;

    public CellRange(String rangeRef) {
        String[] parts = separateRangeRefs(rangeRef);
        String part0 = parts[0];
        if (parts.length == 1) {
            firstCell = new CellRef(part0);
            lastCell = firstCell;
            isSingleCell = true;
        } else if (parts.length != 2) {
            throw new IllegalArgumentException("Bad range ref '" + rangeRef + "'");
        } else {
            String part1 = parts[1];
            firstCell = new CellRef(part0);
            lastCell = new CellRef(part1);
            isSingleCell = part0.equals(part1);
        }
    }

    public CellRange(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstCell = new CellRef(firstRow, firstCol);
        this.lastCell = new CellRef(lastRow, lastCol);
    }

    public CellRange(String sheetName, int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstCell = new CellRef(sheetName, firstRow, firstCol);
        this.lastCell = new CellRef(sheetName, lastRow, lastCol);
    }

    public String getSheetName() {
        return firstCell.getSheetName();
    }

    public void setSheetName(String sheetName) {
        if (!Objects.equals(getSheetName(), sheetName)) {
            firstCell.setSheetName(sheetName);
            lastCell.setSheetName(sheetName);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getFirstRow() {
        return firstCell.getRow();
    }

    public void setFirstRow(int rowIndex) {
        if (firstCell.getRow() != rowIndex) {
            firstCell.setRow(rowIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getFirstCol() {
        return firstCell.getCol();
    }

    public void setFirstCol(int colIndex) {
        if (firstCell.getCol() != colIndex) {
            firstCell.setRow(colIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getLastRow() {
        return lastCell.getRow();
    }

    public void setLastRow(int rowIndex) {
        if (lastCell.getRow() != rowIndex) {
            lastCell.setRow(rowIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getLastCol() {
        return lastCell.getCol();
    }

    public void setLastCol(int colIndex) {
        if (lastCell.getCol() != colIndex) {
            lastCell.setRow(colIndex);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isFirstRowAbsolute() {
        return firstCell.isRowAbsolute();
    }

    public void setFirstRowAbsolute(boolean rowAbs) {
        if (firstCell.isRowAbsolute() != rowAbs) {
            firstCell.setRowAbsolute(rowAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isFirstColAbsolute() {
        return firstCell.isColAbsolute();
    }

    public void setFirstColAbsolute(boolean colAbs) {
        if (firstCell.isColAbsolute() != colAbs) {
            firstCell.setColAbsolute(colAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isLastRowAbsolute() {
        return lastCell.isRowAbsolute();
    }

    public void setLastRowAbsolute(boolean rowAbs) {
        if (lastCell.isRowAbsolute() != rowAbs) {
            lastCell.setRowAbsolute(rowAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isLastColAbsolute() {
        return lastCell.isColAbsolute();
    }

    public void setLastColAbsolute(boolean colAbs) {
        if (lastCell.isColAbsolute() != colAbs) {
            lastCell.setColAbsolute(colAbs);
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public String formatAsString() {
        if (ref == null) {
            StringBuilder sb = new StringBuilder(32);
            sb.append(firstCell.formatAsString());
            if (!isSingleCell) {
                sb.append(CELL_DELIMITER);
                if (lastCell.getSheetName() == null) {
                    sb.append(lastCell.formatAsString());
                } else {
                    lastCell.appendCellReference(sb);
                }
            }
            return sb.toString();
        }
        return ref;
    }

    public String formatAsRowColString() {
        if (rowColRef == null) {
            if (firstCell.equals(lastCell)) {
                rowColRef = firstCell.formatAsRowColString();
            } else {
                rowColRef = firstCell.formatAsRowColString() + CELL_DELIMITER + lastCell.formatAsRowColString(false);
            }
        }
        return rowColRef;
    }

    public boolean isSheetNameDefined() {
        String sheetName = getSheetName();
        return sheetName != null && sheetName.length() > 0;
    }

    private String[] separateRangeRefs(String reference) {
        int len = reference.length();
        int delimiterPos = -1;
        boolean insideDelimitedName = false;

        for (int i = 0; i < len; ++i) {
            switch (reference.charAt(i)) {
                case SPECIAL_NAME_DELIMITER:
                    if (!insideDelimitedName) {
                        insideDelimitedName = true;
                    } else {
                        if (i >= len - 1) {
                            throw new IllegalArgumentException("Range reference '" + reference + "' ends with special name delimiter '" + SPECIAL_NAME_DELIMITER + "'");
                        }

                        if (reference.charAt(i + 1) == SPECIAL_NAME_DELIMITER) {
                            ++i;
                        } else {
                            insideDelimitedName = false;
                        }
                    }
                    break;
                case CELL_DELIMITER:
                    if (!insideDelimitedName) {
                        if (delimiterPos >= 0) {
                            throw new IllegalArgumentException("More than one cell delimiter ':' appears in range reference '" + reference + "'");
                        }

                        delimiterPos = i;
                    }
            }
        }

        if (delimiterPos < 0) {
            return new String[]{reference};
        } else {
            String partA = reference.substring(0, delimiterPos);
            String partB = reference.substring(delimiterPos + 1);
            if (partB.indexOf(SHEET_NAME_DELIMITER) >= 0) {
                throw new RuntimeException("Unexpected ! in second cell reference of '" + reference + "'");
            } else {
                int plingPos = partA.lastIndexOf(SHEET_NAME_DELIMITER);
                if (plingPos < 0) {
                    return new String[]{partA, partB};
                } else {
                    String sheetName = partA.substring(0, plingPos + 1);
                    return new String[]{partA, sheetName + partB};
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellRange)) return false;
        CellRange cellRange = (CellRange) o;
        return Objects.equals(firstCell, cellRange.firstCell) &&
                Objects.equals(lastCell, cellRange.lastCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstCell, lastCell);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName()).append(" [");

        try {
            sb.append(this.formatAsString());
        } catch (Exception var3) {
            sb.append(var3);
        }

        sb.append(']');
        return sb.toString();
    }
}
