package eu.ibagroup.easyrpa.openframework.google.sheets;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keeps information that identifies specific cell of the sheet.
 */
public class CellRef {
    /**
     * The character ($) that signifies a row or column value is absolute instead of relative
     */
    private static final char ABSOLUTE_REFERENCE_MARKER = '$';

    /**
     * The character (!) that separates sheet names from cell references
     */
    private static final char SHEET_NAME_DELIMITER = '!';

    /**
     * The character (') used to quote sheet names when they contain special characters
     */
    private static final char SPECIAL_NAME_DELIMITER = '\'';

    /**
     * Matches a run of one or more letters followed by a run of one or more digits.
     * Both the letter and number groups are optional.
     * The run of letters is group 1 and the run of digits is group 2.
     * Each group may optionally be prefixed with a single '$'.
     */
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Z]+)?(\\$?[0-9]+)?",
            Pattern.CASE_INSENSITIVE);

    /**
     * Name of related sheet.
     */
    private String sheetName;

    /**
     * Row index of this cell reference.
     */
    private int rowIndex = -1;

    /**
     * Column index of this cell reference.
     */
    private int colIndex = -1;

    /**
     * Shows whether index of the row is absolute.
     */
    private boolean isRowAbs = false;

    /**
     * Shows whether index of the column is absolute.
     */
    private boolean isColAbs = false;

    /**
     * Cached string representation of this reference in A1-style.
     */
    private String ref;

    /**
     * Cached string representation of this reference in R1C1-style.
     */
    private String rowColRef;

    /**
     * Creates a new reference based on given A1-style string.
     *
     * @param cellRef string representation of the reference in A1-style. E.g. "A3" or "Sheet1!A3"
     */
    public CellRef(String cellRef) {
        if (cellRef.toUpperCase().contains("#REF!")) {
            throw new IllegalArgumentException("Cell reference invalid: " + cellRef);
        }

        CellRefParts parts = separateRefParts(cellRef);
        sheetName = parts.sheetName;
        String colRef = parts.colRef;
        isColAbs = colRef.length() > 0 && colRef.charAt(0) == ABSOLUTE_REFERENCE_MARKER;
        if (isColAbs) {
            colRef = colRef.substring(1);
        }

        if (colRef.length() == 0) {
            colIndex = -1;
        } else {
            colIndex = convertColStringToIndex(colRef);
        }

        String rowRef = parts.rowRef;
        isRowAbs = rowRef.length() > 0 && rowRef.charAt(0) == ABSOLUTE_REFERENCE_MARKER;
        if (isRowAbs) {
            rowRef = rowRef.substring(1);
        }

        if (rowRef.length() == 0) {
            rowIndex = -1;
        } else {
            rowIndex = Integer.parseInt(rowRef) - 1;
        }
    }

    /**
     * Creates a new sheet-free reference based on given row and column indexes.
     *
     * @param rowIndex 0-based row index of the reference.
     * @param colIndex 0-based column index of the reference.
     */
    public CellRef(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    /**
     * Creates a new sheet-based reference based on given row and column indexes.
     *
     * @param sheetName name of related sheet.
     * @param rowIndex  0-based row index of the reference.
     * @param colIndex  0-based column index of the reference.
     */
    public CellRef(String sheetName, int rowIndex, int colIndex) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    /**
     * Creates a new sheet-based reference.
     *
     * @param sheetName name of related sheet.
     * @param rowIndex  0-based row index of the reference.
     * @param colIndex  0-based column index of the reference.
     * @param isRowAbs  {@code true} if row index should be absolute or {@code false} otherwise.
     * @param isColAbs  {@code true} if column index should be absolute or {@code false} otherwise.
     */
    public CellRef(String sheetName, int rowIndex, int colIndex, boolean isRowAbs, boolean isColAbs) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.isRowAbs = isRowAbs;
        this.isColAbs = isColAbs;
    }

    /**
     * Gets the name of related sheet.
     *
     * @return name of related sheet or {@code null} if this cell reference is sheet-free.
     */
    public String getSheetName() {
        return sheetName;
    }


    /**
     * Sets the name of related sheet and makes this cell reference as sheet-based.
     *
     * @param sheetName the name of sheet to set.
     */
    public void setSheetName(String sheetName) {
        if (!Objects.equals(this.sheetName, sheetName)) {
            this.sheetName = sheetName;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets row index of this cell reference.
     *
     * @return row index of this cell reference.
     */
    public int getRow() {
        return rowIndex;
    }

    /**
     * Sets row index for this cell reference.
     *
     * @param rowIndex 0-based row index to set.
     */
    public void setRow(int rowIndex) {
        if (this.rowIndex != rowIndex) {
            this.rowIndex = rowIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets column index of this cell reference.
     *
     * @return column index of this cell reference.
     */
    public int getCol() {
        return colIndex;
    }

    /**
     * Sets column index for this cell reference.
     *
     * @param colIndex 0-based column index to set.
     */
    public void setCol(int colIndex) {
        if (this.colIndex != colIndex) {
            this.colIndex = colIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether row index is absolute.
     *
     * @return {@code true} if row index is absolute or {@code false} otherwise.
     */
    public boolean isRowAbsolute() {
        return isRowAbs;
    }

    /**
     * Sets row index as absolute.
     *
     * @param rowAbs {@code true} if row index should be absolute or {@code false} otherwise.
     */
    public void setRowAbsolute(boolean rowAbs) {
        if (this.isRowAbs != rowAbs) {
            this.isRowAbs = rowAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Gets whether column index is absolute.
     *
     * @return {@code true} if column index is absolute or {@code false} otherwise.
     */
    public boolean isColAbsolute() {
        return isColAbs;
    }

    /**
     * Sets column index as absolute.
     *
     * @param colAbs {@code true} if column index should be absolute or {@code false} otherwise.
     */
    public void setColAbsolute(boolean colAbs) {
        if (this.isColAbs != colAbs) {
            this.isColAbs = colAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    /**
     * Formats this cell reference as string in A1-style. E.g. "A3" or "Sheet1!A3".
     *
     * @return string representation of this cell reference in A1-style.
     */
    public String formatAsString() {
        if (ref == null) {
            ref = formatAsString(true);
        }
        return ref;
    }

    /**
     * Formats this cell reference as string in A1-style. E.g. "A3" or "Sheet1!A3".
     *
     * @param includeSheetName set to {@code false} if necessary to exclude sheet name in the output string.
     * @return string representation of this cell reference in A1-style.
     */
    public String formatAsString(boolean includeSheetName) {
        StringBuilder sb = new StringBuilder(32);
        if (includeSheetName && sheetName != null) {
            appendFormattedSheetName(sb, sheetName);
            sb.append(SHEET_NAME_DELIMITER);
        }
        this.appendCellReference(sb);
        return sb.toString();
    }

    /**
     * Formats this cell reference as string in R1C1-style. E.g. "R3C1:R20C7" or "Sheet1!R3C1:R20C7".
     *
     * @return string representation of this cell reference in R1C1-style.
     */
    public String formatAsRowColString() {
        if (rowColRef == null) {
            rowColRef = formatAsRowColString(true);
        }
        return rowColRef;
    }

    /**
     * Formats this cell reference as string in R1C1-style. E.g. "R3C1:R20C7" or "Sheet1!R3C1:R20C7".
     *
     * @param includeSheetName set to {@code false} if necessary to exclude sheet name in the output string.
     * @return string representation of this cell reference in R1C1-style.
     */
    public String formatAsRowColString(boolean includeSheetName) {
        StringBuilder sb = new StringBuilder(32);
        if (includeSheetName && sheetName != null) {
            appendFormattedSheetName(sb, sheetName);
            sb.append(SHEET_NAME_DELIMITER);
        }
        if (rowIndex >= 0) {
            if (isRowAbs) {
                sb.append(ABSOLUTE_REFERENCE_MARKER);
            }
            sb.append("R").append(rowIndex + 1);
        }
        if (colIndex >= 0) {
            if (isColAbs) {
                sb.append(ABSOLUTE_REFERENCE_MARKER);
            }
            sb.append("C").append(colIndex + 1);
        }
        return sb.toString();
    }

    /**
     * Checks whether related sheet name is defined for this cell reference (sheet-based or not).
     *
     * @return {@code true} is related sheet name is defined or {@code false} otherwise.
     */
    public boolean isSheetNameDefined() {
        return sheetName != null && sheetName.length() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellRef)) return false;
        CellRef cellRef = (CellRef) o;
        return rowIndex == cellRef.rowIndex &&
                colIndex == cellRef.colIndex &&
                isRowAbs == cellRef.isRowAbs &&
                isColAbs == cellRef.isColAbs &&
                Objects.equals(sheetName, cellRef.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetName, rowIndex, colIndex, isRowAbs, isColAbs);
    }

    @Override
    public String toString() {
        return formatAsString();
    }

    /**
     * Appends cell reference with '$' markers for absolute values as required.
     * Sheet name is not included.
     */
    void appendCellReference(StringBuilder sb) {
        if (colIndex != -1) {
            if (isColAbs) {
                sb.append(ABSOLUTE_REFERENCE_MARKER);
            }

            sb.append(convertNumToColString(colIndex));
        }

        if (rowIndex != -1) {
            if (isRowAbs) {
                sb.append(ABSOLUTE_REFERENCE_MARKER);
            }

            sb.append(rowIndex + 1);
        }
    }

    private void appendFormattedSheetName(Appendable out, String rawSheetName) {
        try {
            if (needsDelimitingForSheetName(rawSheetName)) {
                out.append(SPECIAL_NAME_DELIMITER);
                appendAndEscape(out, rawSheetName);
                out.append(SPECIAL_NAME_DELIMITER);
            } else {
                appendAndEscape(out, rawSheetName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tell if the given raw sheet name needs screening/delimiting.
     *
     * @param rawSheetName the sheet name.
     * @return {@code true} if the given raw sheet name needs screening/delimiting, {@code false} otherwise or if the
     * sheet name is {@code null}.
     */
    private boolean needsDelimitingForSheetName(String rawSheetName) {
        if (rawSheetName == null) {
            return false;
        }
        int len = rawSheetName.length();
        if (len < 1) {
            return false; // some cases we get missing external references, resulting in empty sheet names
        }
        if (Character.isDigit(rawSheetName.charAt(0))) {
            // sheet name with digit in the first position always requires delimiting
            return true;
        }
        for (int i = 0; i < len; i++) {
            char ch = rawSheetName.charAt(i);
            if (isSpecialChar(ch)) {
                return true;
            }
        }
        if (Character.isLetter(rawSheetName.charAt(0))
                && Character.isDigit(rawSheetName.charAt(len - 1))) {
            // note - values like "A$1:$C$20" don't get this far
            if (nameLooksLikePlainCellReference(rawSheetName)) {
                return true;
            }
        }
        return nameLooksLikeBooleanLiteral(rawSheetName);
    }

    /**
     * @return {@code true} if the presence of the specified character in a sheet name would
     * require the sheet name to be delimited in formulas.  This includes every non-alphanumeric
     * character besides underscore '_' and dot '.'.
     */
    private boolean isSpecialChar(char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return false;
        } else {
            switch (ch) {
                case '\t':
                case '\n':
                case '\r':
                    throw new RuntimeException(
                            String.format("Illegal character (0x%s) found in sheet name.", Integer.toHexString(ch))
                    );
                case '.':
                case '_':
                    return false;
                default:
                    return true;
            }
        }
    }

    private void appendAndEscape(Appendable sb, String rawSheetName) {
        try {
            if (rawSheetName == null) {
                sb.append("#REF");
                return;
            }
            int len = rawSheetName.length();
            for (int i = 0; i < len; i++) {
                char ch = rawSheetName.charAt(i);
                if (ch == SPECIAL_NAME_DELIMITER) {
                    // single quotes (') are encoded as ('')
                    sb.append(SPECIAL_NAME_DELIMITER);
                }
                sb.append(ch);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean nameLooksLikeBooleanLiteral(String rawSheetName) {
        switch (rawSheetName.charAt(0)) {
            case 'T':
            case 't':
                return "TRUE".equalsIgnoreCase(rawSheetName);
            case 'F':
            case 'f':
                return "FALSE".equalsIgnoreCase(rawSheetName);
        }
        return false;
    }

    /**
     * Note - this method assumes the specified rawSheetName has only letters and digits.  It
     * cannot be used to match absolute or range references (using the dollar or colon char).
     * <p>
     * e notable cases:
     * <table>
     *   <caption>Notable cases</caption>
     *   <tr><th>Input&nbsp;</th><th>Result&nbsp;</th><th>Comments</th></tr>
     *   <tr><td>"A1"&nbsp;&nbsp;</td><td>true</td><td>&nbsp;</td></tr>
     *   <tr><td>"a111"&nbsp;&nbsp;</td><td>true</td><td>&nbsp;</td></tr>
     *   <tr><td>"AA"&nbsp;&nbsp;</td><td>false</td><td>&nbsp;</td></tr>
     *   <tr><td>"aa1"&nbsp;&nbsp;</td><td>true</td><td>&nbsp;</td></tr>
     *   <tr><td>"A1A"&nbsp;&nbsp;</td><td>false</td><td>&nbsp;</td></tr>
     *   <tr><td>"A1A1"&nbsp;&nbsp;</td><td>false</td><td>&nbsp;</td></tr>
     *   <tr><td>"A$1:$C$20"&nbsp;&nbsp;</td><td>false</td><td>Not a plain cell reference</td></tr>
     *   <tr><td>"SALES20080101"&nbsp;&nbsp;</td><td>true</td>
     *   <td>Still needs delimiting even though well out of range</td></tr>
     * </table>
     *
     * @return {@code true} if there is any possible ambiguity that the specified rawSheetName
     * could be interpreted as a valid cell name.
     */
    private boolean nameLooksLikePlainCellReference(String rawSheetName) {
        Matcher matcher = CELL_REF_PATTERN.matcher(rawSheetName);
        return matcher.matches();
    }

    /**
     * Takes in a column reference portion of a CellRef and converts it from
     * symbol format to 0-based base 10.
     * 'A' -&gt; 0
     * 'Z' -&gt; 25
     * 'AA' -&gt; 26
     * 'IV' -&gt; 255
     *
     * @param ref cell reference string with column number in symbol format.
     * @return zero based column index
     */
    private int convertColStringToIndex(String ref) {
        int result = 0;
        char[] refs = ref.toUpperCase(Locale.ROOT).toCharArray();

        for (int k = 0; k < refs.length; ++k) {
            char symbol = refs[k];
            if (symbol == ABSOLUTE_REFERENCE_MARKER) {
                if (k != 0) {
                    throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
                }
            } else {
                result = result * 26 + symbol - 'A' + 1;
            }
        }

        return result - 1;
    }

    /**
     * Takes in a 0-based base-10 column and returns its symbol representation.
     *
     * @param col 0-based column index to convert.
     * @return column number in symbol format. E.g. {@code convertNumToColString(3)} returns {@code "D"}
     */
    public String convertNumToColString(int col) {
        int excelColNum = col + 1;
        StringBuilder colRef = new StringBuilder(2);
        int colRemain = excelColNum;

        while (colRemain > 0) {
            int thisPart = colRemain % 26;
            if (thisPart == 0) {
                thisPart = 26;
            }

            colRemain = (colRemain - thisPart) / 26;
            char colChar = (char) (thisPart + 64);
            colRef.insert(0, colChar);
        }

        return colRef.toString();
    }

    /**
     * Separates the sheet name, row, and columns from a cell reference string.
     *
     * @param reference is a string that identifies a cell within the sheet.
     * @return object with separated sheetName, column (in symbol format) and row.
     */
    private CellRefParts separateRefParts(String reference) {
        int plingPos = reference.lastIndexOf(33);
        String sheetName = parseSheetName(reference, plingPos);
        String cell = reference.substring(plingPos + 1).toUpperCase(Locale.ROOT);
        Matcher matcher = CELL_REF_PATTERN.matcher(cell);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid CellReference: " + reference);
        } else {
            String col = matcher.group(1);
            String row = matcher.group(2);
            return new CellRefParts(sheetName, row, col);
        }
    }

    private static String parseSheetName(String reference, int indexOfSheetNameDelimiter) {
        if (indexOfSheetNameDelimiter < 0) {
            return null;
        } else {
            boolean isQuoted = reference.charAt(0) == SPECIAL_NAME_DELIMITER;
            if (!isQuoted) {
                if (!reference.contains(" ")) {
                    return reference.substring(0, indexOfSheetNameDelimiter);
                } else {
                    throw new IllegalArgumentException(
                            String.format("Sheet names containing spaces must be quoted: (%s)", reference)
                    );
                }
            } else {
                int lastQuotePos = indexOfSheetNameDelimiter - 1;
                if (reference.charAt(lastQuotePos) != SPECIAL_NAME_DELIMITER) {
                    throw new IllegalArgumentException(String.format("Mismatched quotes: (%s)", reference));
                } else {
                    StringBuilder sb = new StringBuilder(indexOfSheetNameDelimiter);

                    for (int i = 1; i < lastQuotePos; ++i) {
                        char ch = reference.charAt(i);
                        if (ch != SPECIAL_NAME_DELIMITER) {
                            sb.append(ch);
                        } else {
                            if (i + 1 >= lastQuotePos || reference.charAt(i + 1) != SPECIAL_NAME_DELIMITER) {
                                throw new IllegalArgumentException(
                                        String.format("Bad sheet name quote escaping: (%s)", reference)
                                );
                            }
                            ++i;
                            sb.append(ch);
                        }
                    }
                    return sb.toString();
                }
            }
        }
    }

    /**
     * Keeps separated sheetName, column (in symbol format) and row.
     */
    private static final class CellRefParts {
        final String sheetName;
        final String rowRef;
        final String colRef;

        CellRefParts(String sheetName, String rowRef, String colRef) {
            this.sheetName = sheetName;
            this.rowRef = rowRef != null ? rowRef : "";
            this.colRef = colRef != null ? colRef : "";
        }
    }
}
