package eu.ibagroup.easyrpa.openframework.googlesheets;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellRef {
    /**
     * The character ($) that signifies a row or column value is absolute instead of relative
     */
    private static final char ABSOLUTE_REFERENCE_MARKER = '$';
    /**
     * The character (!) that separates sheet names from cell references
     */
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char SPECIAL_NAME_DELIMITER = '\'';


    private String sheetName;
    private int rowIndex = -1;
    private int colIndex = -1;
    private boolean isRowAbs = false;
    private boolean isColAbs = false;

    private String ref;
    private String rowColRef;
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Z]+)?(\\$?[0-9]+)?", Pattern.CASE_INSENSITIVE);

    public static boolean isPartAbsolute(String part) {
        return part.charAt(0) == ABSOLUTE_REFERENCE_MARKER;
    }

    public static int convertColStringToIndex(String ref) {
        int retval = 0;
        char[] refs = ref.toUpperCase(Locale.ROOT).toCharArray();

        for (int k = 0; k < refs.length; ++k) {
            char thechar = refs[k];
            if (thechar == ABSOLUTE_REFERENCE_MARKER) {
                if (k != 0) {
                    throw new IllegalArgumentException("Bad col ref format '" + ref + "'");
                }
            } else {
                retval = retval * 26 + thechar - 65 + 1;
            }
        }

        return retval - 1;
    }

    public CellRef(String cellRef) {
        if (endsWithIgnoreCase(cellRef, "#REF!")) {
            throw new IllegalArgumentException("Cell reference invalid: " + cellRef);
        } else {
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
    }

    private static CellRefParts separateRefParts(String reference) {
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
                    throw new IllegalArgumentException("Sheet names containing spaces must be quoted: (" + reference + ")");
                }
            } else {
                int lastQuotePos = indexOfSheetNameDelimiter - 1;
                if (reference.charAt(lastQuotePos) != SPECIAL_NAME_DELIMITER) {
                    throw new IllegalArgumentException("Mismatched quotes: (" + reference + ")");
                } else {
                    StringBuilder sb = new StringBuilder(indexOfSheetNameDelimiter);

                    for (int i = 1; i < lastQuotePos; ++i) {
                        char ch = reference.charAt(i);
                        if (ch != SPECIAL_NAME_DELIMITER) {
                            sb.append(ch);
                        } else {
                            if (i + 1 >= lastQuotePos || reference.charAt(i + 1) != SPECIAL_NAME_DELIMITER) {
                                throw new IllegalArgumentException("Bad sheet name quote escaping: (" + reference + ")");
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

    public CellRef(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public CellRef(String sheetName, int rowIndex, int colIndex) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public CellRef(int pRow, int pCol, boolean pAbsRow, boolean pAbsCol) {
        this(null, pRow, pCol, pAbsRow, pAbsCol);
    }

    public CellRef(String sheetName, int rowIndex, int colIndex, boolean isRowAbs, boolean isColAbs) {
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.isRowAbs = isRowAbs;
        this.isColAbs = isColAbs;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        if (!Objects.equals(this.sheetName, sheetName)) {
            this.sheetName = sheetName;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getRow() {
        return rowIndex;
    }

    public void setRow(int rowIndex) {
        if (this.rowIndex != rowIndex) {
            this.rowIndex = rowIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public int getCol() {
        return colIndex;
    }

    public void setCol(int colIndex) {
        if (this.colIndex != colIndex) {
            this.colIndex = colIndex;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isRowAbsolute() {
        return isRowAbs;
    }

    public void setRowAbsolute(boolean rowAbs) {
        if (this.isRowAbs != rowAbs) {
            this.isRowAbs = rowAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public boolean isColAbsolute() {
        return isColAbs;
    }

    public void setColAbsolute(boolean colAbs) {
        if (this.isColAbs != colAbs) {
            this.isColAbs = colAbs;
            this.ref = null;
            this.rowColRef = null;
        }
    }

    public String formatAsString() {
        if (ref == null) {
            ref = formatAsString(true);
        }
        return ref;
    }

    public String formatAsString(boolean includeSheetName) {
        StringBuilder sb = new StringBuilder(32);
        if (includeSheetName && sheetName != null) {
            appendFormat(sb, sheetName);
            sb.append(SHEET_NAME_DELIMITER);
        }

        this.appendCellReference(sb);
        return sb.toString();
    }

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

    public String formatAsRowColString() {
        return formatAsRowColString(true);
    }

    public String formatAsRowColString(boolean includeSheetName) {
        if (rowColRef == null) {
            StringBuilder sb = new StringBuilder(32);
            if (includeSheetName && sheetName != null) {
                appendFormat(sb, sheetName);
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
            rowColRef = sb.toString();
        }
        return rowColRef;
    }

    public boolean isSheetNameDefined() {
        return sheetName != null && sheetName.length() > 0;
    }

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

    private void appendFormat(Appendable out, String rawSheetName) {
        try {
            boolean needsQuotes = needsDelimiting(rawSheetName);
            if (needsQuotes) {
                out.append(SPECIAL_NAME_DELIMITER);
                appendAndEscape(out, rawSheetName);
                out.append(SPECIAL_NAME_DELIMITER);
            } else {
                appendAndEscape(out, rawSheetName);
            }

        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    private void appendAndEscape(Appendable sb, String rawSheetName) {
        try {
            if (rawSheetName == null) {
                sb.append("#REF");
            } else {
                int len = rawSheetName.length();

                for (int i = 0; i < len; ++i) {
                    char ch = rawSheetName.charAt(i);
                    if (ch == SPECIAL_NAME_DELIMITER) {
                        sb.append(SPECIAL_NAME_DELIMITER);
                    }

                    sb.append(ch);
                }

            }
        } catch (Exception var5) {
            throw new RuntimeException(var5);
        }
    }

    boolean needsDelimiting(String rawSheetName) {
        if (rawSheetName == null) {
            return false;
        } else {
            int len = rawSheetName.length();
            if (len < 1) {
                return false;
            } else if (Character.isDigit(rawSheetName.charAt(0))) {
                return true;
            } else {
                for (int i = 0; i < len; ++i) {
                    char ch = rawSheetName.charAt(i);
                    if (isSpecialChar(ch)) {
                        return true;
                    }
                }

                if (Character.isLetter(rawSheetName.charAt(0)) && Character.isDigit(rawSheetName.charAt(len - 1)) && nameLooksLikePlainCellReference(rawSheetName)) {
                    return true;
                } else return nameLooksLikeBooleanLiteral(rawSheetName);
            }
        }
    }

    private static boolean nameLooksLikeBooleanLiteral(String rawSheetName) {
        switch (rawSheetName.charAt(0)) {
            case 'F':
            case 'f':
                return "FALSE".equalsIgnoreCase(rawSheetName);
            case 'T':
            case 't':
                return "TRUE".equalsIgnoreCase(rawSheetName);
            default:
                return false;
        }
    }

    static boolean isSpecialChar(char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return false;
        } else {
            switch (ch) {
                case '\t':
                case '\n':
                case '\r':
                    throw new RuntimeException("Illegal character (0x" + Integer.toHexString(ch) + ") found in sheet name");
                case '.':
                case '_':
                    return false;
                default:
                    return true;
            }
        }
    }

    private boolean nameLooksLikePlainCellReference(String rawSheetName) {
        Matcher matcher = CELL_REF_PATTERN.matcher(rawSheetName);
        return matcher.matches();
    }

    private boolean endsWithIgnoreCase(String haystack, String suffix) {
        int length = suffix.length();
        int start = haystack.length() - length;
        return haystack.regionMatches(true, start, suffix, 0, length);
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
