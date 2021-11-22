package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.function.RecordMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.model.ExternalLinksTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Useful methods to work with SpreadsheetDocument
 */
public class SpreadsheetUtil {

    private static final DataFormatter defaultDataFormatter = new DataFormatter();

    /**
     * Copy one sheet to another without styles. Source and destination sheets can
     * be from different spreadsheets.
     *
     * @param srcSheet  source sheet
     * @param destSheet destination sheet
     */
    public static void copySheetWithoutStyles(Sheet srcSheet, Sheet destSheet) {
        copySheet(srcSheet, destSheet, false);
    }

    /**
     * Copy one sheet to another with styles. Source and destination sheets can be
     * from different spreadsheets.
     *
     * @param srcSheet  source sheet
     * @param destSheet destination sheet
     */
    public static void copySheet(Sheet srcSheet, Sheet destSheet) {
        copySheet(srcSheet, destSheet, true);
    }

    /**
     * Copy cell style object.
     *
     * @param sheet related sheet
     * @param src   cell style to copy
     */
    public static CellStyle createCellStyleCopy(Sheet sheet, CellStyle src) {
        if (!(src instanceof XSSFCellStyle)) {
            return null;
        }

        StylesTable styles = ((XSSFWorkbook) sheet.getWorkbook()).getStylesSource();
        XSSFCellStyle srcStyle = (XSSFCellStyle) src;

        CTXf destXf = copyCTXf(srcStyle.getCoreXf(), styles, styles);
        CTXf destStyleXf = copyCTXf(srcStyle.getStyleXf(), styles, styles);

        int destXfIdx = styles.putCellXf(destXf) - 1;
        int destStyleXfIdx = styles.putCellStyleXf(destStyleXf) - 1;

        return new XSSFCellStyle(destXfIdx, destStyleXfIdx, styles, styles.getTheme());
    }

    /**
     * Get list of records that contains specified sheet.
     *
     * @param sheet     - a sheet to lookup at
     * @param keyValues - key value that identifies header row.
     * @return list of records where each record is a map between column name of
     * cell and its value with corresponding type (String, Double, Date
     * etc.). If header row is not found by specified values the list is
     * empty.
     */
    public static List<Map<String, Object>> getRecords(Sheet sheet, String... keyValues) {
        return getRecords(sheet, null, keyValues);
    }

    /**
     * Get list of records that contains specified sheet.
     *
     * @param sheet     - a sheet to lookup at
     * @param evaluator - workbooks formula evaluator that is used to evaluate
     *                  formulas. If it's null then a new formula evaluator will be
     *                  created if needed.
     * @param keyValues - key value that identifies header row.
     * @return list of records where each record is a map between column name of
     * cell and its value with corresponding type (String, Double, Date
     * etc.). If header row is not found by specified values the list is
     * empty.
     */
    public static List<Map<String, Object>> getRecords(Sheet sheet, FormulaEvaluator evaluator, String... keyValues) {
        return getTypedRecords(sheet, evaluator, (row, columns, eval) -> {
            Map<String, Object> record = new HashMap<>();
            int cellsCount = Math.min(columns.size(), row.getLastCellNum() + 1);

            for (int j = 0; j < cellsCount; j++) {
                Cell cell = row.getCell(j);
                String column = j <= columns.size() ? columns.get(j) : null;

                if (cell != null && column != null) {
                    record.put(column, SpreadsheetUtil.getCellValue(cell, eval));
                }
            }
            return record;
        }, keyValues);
    }

    /**
     * Get list of typed records that contains specified sheet.
     *
     * @param sheet        - a sheet to lookup at
     * @param recordMapper - a function that extracts a record of specified type for
     *                     each row.
     * @param keyValues    - key value that identifies header row.
     * @return list of records that has been returned by recordMapper. If header row
     * is not found by specified values the list is empty.
     */
    public static <T> List<T> getTypedRecords(Sheet sheet, RecordMapper<T> recordMapper, String... keyValues) {
        return getTypedRecords(sheet, null, recordMapper, keyValues);
    }

    /**
     * Get list of typed records that contains specified sheet.
     *
     * @param sheet        - a sheet to lookup at
     * @param evaluator    - workbooks formula evaluator that is used to evaluate
     *                     formulas. If it's null then a new formula evaluator will
     *                     be created if needed.
     * @param recordMapper - a function that extracts a record of specified type for
     *                     each row.
     * @param keyValues    - key value that identifies header row.
     * @return list of records that has been returned by recordMapper. If header row
     * is not found by specified values the list is empty.
     */
    public static <T> List<T> getTypedRecords(Sheet sheet, FormulaEvaluator evaluator, RecordMapper<T> recordMapper, String... keyValues) {
        List<T> records = new ArrayList<>();

        Integer headerRowNum = findRow(sheet, keyValues);

        if (headerRowNum < 0) {
            return records;
        }

        Row headerRow = sheet.getRow(headerRowNum);

        if (headerRow == null) {
            return records;
        }

        evaluator = evaluator != null ? evaluator : sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        List<String> columns = new ArrayList<>();
        for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
            Cell headerCell = headerRow.getCell(i);
            columns.add(headerCell != null ? headerCell.toString().trim() : "");
        }

        for (int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            if (row == null) {
                continue;
            }

            records.add(recordMapper.apply(row, columns, evaluator));
        }

        return records;
    }

    /**
     * Call function for each row of the specified sheet starting from row with
     * index {@code startRowNum}.
     *
     * @param sheet       - a sheet to go over.
     * @param startRowNum - index of row to start.
     * @param function    - function that accepts two arguments. First is a POI row
     *                    object and the second is a list of typed cell values.
     */
    public static void eachRow(Sheet sheet, int startRowNum, BiConsumer<Row, List<Object>> function) {
        eachRow(sheet, startRowNum, null, function);
    }

    /**
     * Call function for each row of the specified sheet starting from row with
     * index {@code startRowNum}.
     *
     * @param sheet       - a sheet to go over.
     * @param startRowNum - index of row to start.
     * @param evaluator   - workbooks formula evaluator that is used to evaluate
     *                    formulas. If it's null then a new formula evaluator will
     *                    be created if needed.
     * @param function    - function that accepts two arguments. First is a POI row
     *                    object and the second is a list of typed cell values.
     */
    public static void eachRow(Sheet sheet, int startRowNum, FormulaEvaluator evaluator, BiConsumer<Row, List<Object>> function) {
        for (int i = startRowNum; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            List<Object> rowData = new ArrayList<>();
            if (row != null) {
                for (int j = 0; j <= row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    rowData.add(cell != null ? SpreadsheetUtil.getCellValue(cell, evaluator) : null);
                }
            }
            function.accept(row, rowData);
        }
    }

    /**
     * Gets mapping of column header names to their index.
     *
     * @param sheet     - a sheet to lookup at
     * @param keyValues - set of value that is used to find header row
     * @return map where keys are column names in low case and values are their
     * index
     */
    public static Map<String, Integer> getColumnIndex(Sheet sheet, String... keyValues) {
        Map<String, Integer> columnIndex = new LinkedHashMap<>();
        Integer headerRowNum = findRow(sheet, keyValues);
        if (headerRowNum >= 0) {
            Row headerRow = sheet.getRow(headerRowNum);
            for (Cell cell : headerRow) {
                if (cell == null) {
                    continue;
                }
                String columnName = cell.toString().toLowerCase().trim();
                if (columnName.length() > 0) {
                    columnIndex.put(columnName, cell.getColumnIndex());
                }
            }
        }
        return columnIndex;
    }

    /**
     * Finds the row with all specified values.
     *
     * @param sheet  - a sheet to lookup at
     * @param values - values
     * @return row number that contains all specified values
     */
    public static Integer findRow(Sheet sheet, String... values) {
        for (Row row : sheet) {
            if (row == null) {
                continue;
            }
            boolean matchesFound = false;
            for (String key : values) {
                matchesFound = false;

                for (Cell cell : row) {
                    if (cell == null) {
                        continue;
                    }
                    matchesFound = key.equalsIgnoreCase(cell.toString());
                    if (matchesFound) {
                        break;
                    }
                }

                if (!matchesFound) {
                    break;
                }
            }

            if (matchesFound) {
                return row.getRowNum();
            }
        }
        return -1;
    }

    /**
     * Shifts up or down specified rows amount of specified sheet.
     * <p>
     * If {@code rowsCount} is positive then all sheet content is shifted down with
     * inserting new rows. If {@code rowsCount} is negative all sheet content is
     * shifted up. In this case {@code rowCount} records above {@code startRow} will
     * be removed.
     *
     * @param sheet     - sheet
     * @param startRow  - the row index to start shifting
     * @param rowsCount - the number of rows to shift
     */
    public static void shiftRows(Sheet sheet, int startRow, int rowsCount) {

        if (startRow < 0 || startRow >= sheet.getLastRowNum()) {
            return;
        }

        sheet.shiftRows(startRow, sheet.getLastRowNum(), rowsCount);

        // Shift data validation ranges separately since by default shifting of rows
        // doesn't affect position of data validation
        List<? extends DataValidation> dataValidations = sheet.getDataValidations();
        try {
            ((XSSFSheet) sheet).getCTWorksheet().unsetDataValidations();
        } catch (Exception e) {
            // do nothing
        }
        for (DataValidation dv : dataValidations) {
            CellRangeAddressList regions = dv.getRegions();
            for (int i = 0; i < regions.countRanges(); i++) {
                CellRangeAddress dvRegion = regions.getCellRangeAddress(i);
                if (dvRegion.getFirstRow() >= startRow) {
                    dvRegion.setFirstRow(dvRegion.getFirstRow() + rowsCount);
                }
                if (dvRegion.getLastRow() >= startRow) {
                    dvRegion.setLastRow(dvRegion.getLastRow() + rowsCount);
                }
            }
            sheet.addValidationData(sheet.getDataValidationHelper().createValidation(dv.getValidationConstraint(), dv.getRegions()));
        }
    }

    /**
     * Evaluate formula for the specified cell.
     *
     * @param cell - cell
     * @return evaluated cell value
     */
    public static CellValue evaluate(final Cell cell) {
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        return evaluator.evaluate(cell);
    }

    /**
     * Checks if cell is empty.
     *
     * @param cell - cell to check
     * @return true if cell type is STRING and it's values is empty or cell type is
     * BLANK or ERROR. If cell type is BOOLEAN or NUMERIC returns false.
     */
    public static boolean isCellEmpty(final Cell cell) {
        return isCellEmpty(cell, null);
    }

    /**
     * Checks if cell is empty.
     *
     * @param cell      - cell to check
     * @param evaluator - workbooks formula evaluator that is used to evaluate
     *                  formulas. If it's null then a new formula evaluator will be
     *                  created if needed.
     * @return true if cell type is STRING and it's values is empty or cell type is
     * BLANK or ERROR. If cell type is BOOLEAN or NUMERIC returns false.
     */
    public static boolean isCellEmpty(final Cell cell, FormulaEvaluator evaluator) {
        if (cell == null)
            return true;
        switch (cell.getCellType()) {
            case NUMERIC:
                return false;
            case BOOLEAN:
                return false;
            case STRING:
                return cell.getStringCellValue().isEmpty();
            case FORMULA:
                evaluator = evaluator != null ? evaluator : cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case STRING:
                        return cellValue.getStringValue().isEmpty();
                    case ERROR:
                        return true;
                    case BLANK:
                        return true;
                    default:
                        return false;
                }
            case ERROR:
                return true;
            case BLANK:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if cell has error.
     *
     * @param cell - cell to check
     * @return true if cell type or cell value is ERROR.
     */
    public static boolean hasCellError(final Cell cell) {
        return hasCellError(cell, null);
    }

    /**
     * Checks if cell has error.
     *
     * @param cell      - cell to check
     * @param evaluator - workbooks formula evaluator that is used to evaluate
     *                  formulas. If it's null then a new formula evaluator will be
     *                  created if needed.
     * @return true if cell type or cell value is ERROR.
     */
    public static boolean hasCellError(final Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return false;
        }
        switch (cell.getCellType()) {
            case ERROR:
                return true;
            case FORMULA:
                evaluator = evaluator != null ? evaluator : cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                return CellType.ERROR.equals(cellValue.getCellType());
            default:
                return false;
        }
    }

    /**
     * Gets the value of the cell as a numeric.
     *
     * @param cell - cell
     * @return numeric value of the cell or null if it's not possible to cast the
     * cell value to numeric.
     */
    public static Double getCellValueAsNumeric(final Cell cell) {
        return getCellValueAsNumeric(cell, null);
    }

    /**
     * Gets the value of the cell as a numeric.
     *
     * @param cell      - cell
     * @param evaluator - workbooks formula evaluator that is used to evaluate
     *                  formulas. If it's null then a new formula evaluator will be
     *                  created if needed.
     * @return numeric value of the cell or null if it's not possible to cast the
     * cell value to numeric.
     */
    public static Double getCellValueAsNumeric(final Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case FORMULA:
                try {
                    evaluator = evaluator != null ? evaluator : cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
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

    /**
     * Gets the value of the cell as a String.
     *
     * @param cell - cell
     * @return string value of the cell
     */
    public static String getCellValueAsString(Cell cell) {
        return getCellValueAsString(cell, defaultDataFormatter, null, null);
    }

    /**
     * Gets the value of the cell as a String.
     *
     * @param cell       - cell
     * @param dateFormat - custom date format that is used to get string
     *                   presentation of dates. If this value is null the format
     *                   specified in spreadsheet is used
     * @return string value of the cell
     */
    public static String getCellValueAsString(Cell cell, String dateFormat) {
        return getCellValueAsString(cell, defaultDataFormatter, dateFormat, null);
    }

    /**
     * Gets the value of the cell as a String.
     *
     * @param cell       - cell
     * @param formatter  - data formatter that is used to convert numeric and date
     *                   values to string.
     * @param dateFormat - custom date format that is used to get string
     *                   presentation of dates. If this value is null the format
     *                   specified in spreadsheet is used
     * @return string value of the cell
     */
    public static String getCellValueAsString(Cell cell, DataFormatter formatter, String dateFormat) {
        return getCellValueAsString(cell, formatter, dateFormat, null);
    }

    /**
     * Gets the value of the cell as a String.
     *
     * @param cell       - cell
     * @param formatter  - data formatter that is used to convert numeric and date
     *                   values to string.
     * @param dateFormat - custom date format that is used to get string
     *                   presentation of dates. If this value is null the format
     *                   specified in spreadsheet is used
     * @param evaluator  - workbooks formula evaluator that is used to evaluate
     *                   formulas. If it's null then a new formula evaluator will be
     *                   created if needed.
     * @return string value of the cell
     */
    public static String getCellValueAsString(Cell cell, DataFormatter formatter, String dateFormat, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                CellStyle cellStyle = cell.getCellStyle();
                short formatIndex = cellStyle.getDataFormat();
                String formatString = cellStyle.getDataFormatString();
                if (formatString == null) {
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
                if (DateUtil.isADateFormat(formatIndex, formatString) && dateFormat != null) {
                    formatString = dateFormat;
                }
                return formatter.formatRawCellContents(cell.getNumericCellValue(), formatIndex, formatString);
            case FORMULA:
                try {
                    evaluator = evaluator != null ? evaluator : cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            cellStyle = cell.getCellStyle();
                            formatIndex = cellStyle.getDataFormat();
                            formatString = cellStyle.getDataFormatString();
                            if (formatString == null) {
                                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                            }
                            if (DateUtil.isADateFormat(formatIndex, formatString) && dateFormat != null) {
                                formatString = dateFormat;
                            }
                            return formatter.formatRawCellContents(cellValue.getNumberValue(), formatIndex, formatString);
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
                return cell.toString();
        }
    }

    /**
     * Gets typed value of the cell.
     *
     * @param cell - cell
     * @return typed value of the cell (Date for dates, String or RichTextString for
     * strings, Double for numerics and Boolean for booleans)
     */
    public static Object getCellValue(Cell cell) {
        return getCellValue(cell, null);
    }

    /**
     * Gets typed value of the cell.
     *
     * @param cell      - cell
     * @param evaluator - workbooks formula evaluator that is used to evaluate
     *                  formulas. If it's null then a new formula evaluator will be
     *                  created if needed.
     * @return typed value of the cell (Date for dates, String or RichTextString for
     * strings, Double for numerics and Boolean for booleans)
     */
    public static Object getCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                CellStyle cellStyle = cell.getCellStyle();
                short formatIndex = cellStyle.getDataFormat();
                String formatString = cellStyle.getDataFormatString();
                if (formatString == null) {
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
                if (DateUtil.isADateFormat(formatIndex, formatString)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case FORMULA:
                try {
                    evaluator = evaluator != null ? evaluator : cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    CellValue cellValue = evaluator.evaluate(cell);
                    switch (cellValue.getCellType()) {
                        case NUMERIC:
                            cellStyle = cell.getCellStyle();
                            formatIndex = cellStyle.getDataFormat();
                            formatString = cellStyle.getDataFormatString();
                            if (formatString == null) {
                                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                            }
                            if (DateUtil.isADateFormat(formatIndex, formatString)) {
                                return new Date((long) cellValue.getNumberValue());
                            }
                            return cellValue.getNumberValue();
                        case BOOLEAN:
                            return cellValue.getBooleanValue();
                        case STRING:
                            return cellValue.getStringValue().trim();
                        case ERROR:
                            return "N/A";
                        default:
                            return cellValue.formatAsString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "N/A";
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case STRING:
                RichTextString str = cell.getRichStringCellValue();
                return str.numFormattingRuns() > 0 ? str : str.getString();
            case ERROR:
                return "N/A";
            default:
                return cell.toString();
        }
    }

    /**
     * Sets cell value by its class.
     *
     * @param cell  - cell
     * @param value - typed value to set (String, Date, Double or Boolean)
     */
    public static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellType(CellType.BLANK);

        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);

        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);

        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);

        } else if (value instanceof String && value.toString().startsWith("=")) {
            cell.setCellFormula((String) value);

        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Sets cell value by specified type.
     *
     * @param cell     - cell
     * @param cellType - type of cell value that are going to be set
     * @param value    - typed value to set (String, Date, Double or Boolean)
     */
    public static void setCellValue(Cell cell, CellType cellType, Object value) {
        if (value == null) {
            cellType = CellType.BLANK;
        }
        switch (cellType) {
            case STRING:
                cell.setCellValue(value.toString());
                break;
            case NUMERIC:
                if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else {
                    try {
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                break;
            case BOOLEAN:

                if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else {
                    try {
                        cell.setCellValue(Boolean.parseBoolean(value.toString()));
                    } catch (Exception e) {
                        // do nothing
                    }
                }
                break;
            case FORMULA:
                cell.setCellFormula(value.toString());
                break;
            case BLANK:
                cell.setCellType(cellType);
                break;
            default:
                break;
        }
    }

    /**
     * Adds external sheet data to specified workbook. Do nothing if sheet data has
     * already been added.
     */
    public static int addExternalSheet(Workbook workbook, String extWbRef, Sheet externalSheet) {
        if (!(workbook instanceof XSSFWorkbook) || !(externalSheet instanceof XSSFSheet)) {
            return -1;
        }

        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
        XSSFSheet xssExternalfSheet = (XSSFSheet) externalSheet;

        ExternalLinksTable relatedLinksTable = null;

        List<ExternalLinksTable> linksTables = xssfWorkbook.getExternalLinksTable();
        for (ExternalLinksTable linksTable : linksTables) {
            if (extWbRef.equals(linksTable.getLinkedFileName())) {
                relatedLinksTable = linksTable;
                break;
            }
        }

        if (relatedLinksTable != null) {
            CTExternalBook ctExtBook = relatedLinksTable.getCTExternalLink().getExternalBook();
            CTExternalSheetNames ctSheetNames = ctExtBook.getSheetNames();
            if (ctSheetNames == null) {
                ctSheetNames = ctExtBook.addNewSheetNames();
            }

            final String sheetName = xssExternalfSheet.getSheetName();
            if (Arrays.stream(ctSheetNames.getSheetNameArray()).noneMatch(ctSn -> ctSn.getVal().equals(sheetName))) {
                ctSheetNames.addNewSheetName().setVal(sheetName);
                try (OutputStream out = relatedLinksTable.getPackagePart().getOutputStream()) {
                    relatedLinksTable.writeTo(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return Integer.parseInt(ctExtBook.getId().replaceAll("rId", ""));
        } else {
            try {
                int wbIndx = (linksTables.size() + 1);
                String rIdExtWb = "rId" + wbIndx;
                PackagePartName partname = PackagingURIHelper.createPartName(String.format("/xl/externalLinks/externalLink%s.xml", wbIndx));
                PackagePart part = xssfWorkbook.getPackage().createPart(partname,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.externalLink+xml");

                ExternalLinkDocument link = ExternalLinkDocument.Factory.newInstance();
                CTExternalBook extBook = link.addNewExternalLink().addNewExternalBook();
                extBook.setId(rIdExtWb);
                extBook.addNewSheetNames().addNewSheetName().setVal(xssExternalfSheet.getSheetName());

                part.load(IOUtils.toInputStream(link.toString(), "UTF-8"));

                part.addRelationship(new URI(extWbRef), TargetMode.EXTERNAL,
                        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/externalLinkPath", rIdExtWb);

                relatedLinksTable = new ExternalLinksTable(part);

                String rIdExtLink = "rId" + (xssfWorkbook.getRelationParts().size() + 1);
                xssfWorkbook.addRelation(rIdExtLink, XSSFRelation.EXTERNAL_LINKS, relatedLinksTable);
                xssfWorkbook.getCTWorkbook().addNewExternalReferences().addNewExternalReference().setId(rIdExtLink);
                linksTables.add(relatedLinksTable);

                return wbIndx;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

//    /**
//     * Activates output of POI logs into console.
//     *
//     * @param logsLevel - value logs level from POILogger
//     */
//    public static void outputPOILogsToConsole(int logsLevel) {
//        try {
//            System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.SystemOutLogger");
//            System.setProperty("poi.log.level", logsLevel + "");
//            Field loggerClassNameField = POILogFactory.class.getDeclaredField("_loggerClassName");
//            loggerClassNameField.setAccessible(true);
//            loggerClassNameField.set(null, null);
//            Field loggersField = POILogFactory.class.getDeclaredField("_loggers");
//            loggersField.setAccessible(true);
//            loggersField.set(null, new HashMap<String, POILogger>());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Aligns file path separators according to system default and performs substitution of environment variables like %USERPROFILE%.
     *
     * @param path - file path to normalize.
     */
    public static String normalizeFilePath(String path) {
        if (path.contains("%")) {
            path = FilenameUtils.separatorsToSystem(path);
            Matcher matcher = Pattern.compile("%\\w+%").matcher(path);
            while (matcher.find()) {
                String var = matcher.group();
                path = path.replaceAll(var, FilenameUtils.separatorsToSystem(System.getenv(var.replaceAll("%", ""))));
            }
        }
        return FilenameUtils.separatorsToSystem(path);
    }

    /********************************************************
     * Private methods
     ********************************************************/

    private static void copySheet(Sheet srcSheet, Sheet destSheet, boolean copyStyle) {
        int maxColumnNum = 0;
        Map<Integer, XSSFCellStyle> stylesHash = new HashMap<>();
        for (int i = srcSheet.getFirstRowNum(); i >= 0 && i <= srcSheet.getLastRowNum(); i++) {
            Row srcRow = srcSheet.getRow(i);
            Row destRow = destSheet.createRow(i);
            if (srcRow != null) {
                copyRow(srcSheet, destSheet, srcRow, destRow, copyStyle, stylesHash);
                if (srcRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = srcRow.getLastCellNum();
                }
            }
        }
        for (int i = 0; i <= maxColumnNum; i++) {
            destSheet.setColumnWidth(i, srcSheet.getColumnWidth(i));
        }
    }

    private static void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow, boolean copyStyle, Map<Integer, XSSFCellStyle> stylesHash) {
        destRow.setHeight(srcRow.getHeight());
        for (int j = srcRow.getFirstCellNum(); j >= 0 && j <= srcRow.getLastCellNum(); j++) {
            Cell srcCell = srcRow.getCell(j);
            Cell destCell = destRow.getCell(j);
            if (srcCell != null) {
                if (destCell == null) {
                    destCell = destRow.createCell(j);
                }
                copyCell(srcCell, destCell, copyStyle, stylesHash);
                copyMergedRegion(srcSheet, destSheet, srcRow.getRowNum(), srcCell.getColumnIndex());
            }
        }
    }

    private static void copyCell(Cell srcCell, Cell destCell, boolean copyStyle, Map<Integer, XSSFCellStyle> stylesHash) {
        if (copyStyle) {
            copyCellStyle(srcCell, destCell, stylesHash);
        }
        switch (srcCell.getCellType()) {
            case STRING:
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            case NUMERIC:
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            case BLANK:
                destCell.setBlank();
                break;
            case BOOLEAN:
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case ERROR:
                // ToDo: fix it ???
                destCell.setCellErrorValue(srcCell.getErrorCellValue());
                break;
            case FORMULA:
                destCell.setCellFormula(srcCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    private static void copyMergedRegion(Sheet srcSheet, Sheet destSheet, int rowNum, int cellNum) {
        if (srcSheet.getNumMergedRegions() == 0) {
            return;
        }
        for (CellRangeAddress region : srcSheet.getMergedRegions()) {
            if (region.isInRange(rowNum, cellNum)) {
                boolean regionExistInDest = destSheet.getNumMergedRegions() > 0
                        && destSheet.getMergedRegions().stream().anyMatch(r -> r.getFirstColumn() == region.getFirstColumn()
                        && r.getLastColumn() == region.getLastColumn()
                        && r.getFirstRow() == region.getFirstRow()
                        && r.getLastRow() == region.getLastRow());
                if (!regionExistInDest) {
                    destSheet.addMergedRegion(region.copy());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void copyCellStyle(Cell fromCell, Cell toCell, Map<Integer, XSSFCellStyle> stylesHash) {
        try {
            StylesTable srcStyles = ((XSSFWorkbook) fromCell.getSheet().getWorkbook()).getStylesSource();
            StylesTable destStyles = ((XSSFWorkbook) toCell.getSheet().getWorkbook()).getStylesSource();


            if (srcStyles == destStyles) {
                toCell.setCellStyle(fromCell.getCellStyle());

            } else {
                XSSFCellStyle srcStyle = (XSSFCellStyle) fromCell.getCellStyle();
                XSSFCellStyle destStyle = stylesHash.get(srcStyle.hashCode());

                if (destStyle == null) {
                    CTXf destXf = copyCTXf(srcStyle.getCoreXf(), srcStyles, destStyles);
                    CTXf destStyleXf = copyCTXf(srcStyle.getStyleXf(), srcStyles, destStyles);

                    Field xfsField = destStyles.getClass().getDeclaredField("xfs");
                    xfsField.setAccessible(true);
                    Field styleXfsField = destStyles.getClass().getDeclaredField("styleXfs");
                    styleXfsField.setAccessible(true);
                    List<CTXf> destXfs = (List<CTXf>) xfsField.get(destStyles);
                    List<CTXf> destStyleXfs = (List<CTXf>) styleXfsField.get(destStyles);

                    Optional<CTXf> searchResult = destXfs.stream().filter(xf -> xf.toString().equals(destXf.toString())).findFirst();
                    int destXfIdx = searchResult.map(destXfs::indexOf).orElse(-1);
                    if (destXfIdx < 0) {
                        destXfIdx = destStyles.putCellXf(destXf) - 1;
                    }

                    searchResult = destStyleXfs.stream().filter(xf -> xf.toString().equals(destStyleXf.toString())).findFirst();
                    int destStyleXfIdx = searchResult.map(destStyleXfs::indexOf).orElse(-1);
                    if (destStyleXfIdx < 0) {
                        destStyleXfIdx = destStyles.putCellStyleXf(destStyleXf) - 1;
                    }

                    destStyle = new XSSFCellStyle(destXfIdx, destStyleXfIdx, destStyles, destStyles.getTheme());

                    stylesHash.put(srcStyle.hashCode(), destStyle);
                }

                toCell.setCellStyle(destStyle);
            }
        } catch (IllegalAccessException | NoSuchFieldException ae) {
            throw new RuntimeException("Copy cell style has failed.", ae);
        }
    }

    private static CTXf copyCTXf(CTXf sourceXf, StylesTable sourceStyles, StylesTable targetStyles) {
        short numberFormatIdx = (short) sourceXf.getNumFmtId();
        String sourceNumFmt = sourceStyles.getNumberFormatAt(numberFormatIdx);
        int targetNumFmtIdx = sourceNumFmt != null ? targetStyles.putNumberFormat(sourceNumFmt) : numberFormatIdx;

        XSSFCellBorder sourceBorder = sourceStyles.getBorderAt((int) sourceXf.getBorderId());
        int targetBorderIdx = targetStyles.getBorders().indexOf(sourceBorder);
        if (targetBorderIdx < 0) {
            targetBorderIdx = targetStyles.putBorder(new XSSFCellBorder((CTBorder) sourceBorder.getCTBorder().copy()));
        }

        XSSFFont sourceFont = sourceStyles.getFontAt((int) sourceXf.getFontId());
        int targetFontIdx = targetStyles.getFonts().indexOf(sourceFont);
        if (targetFontIdx < 0) {
            targetFontIdx = (int) (new XSSFFont((CTFont) sourceFont.getCTFont().copy()).registerTo(targetStyles));
        }

        XSSFCellFill sourceFill = sourceStyles.getFillAt((int) sourceXf.getFillId());
        int targetFillIdx = targetStyles.getFills().indexOf(sourceFill);
        if (targetFillIdx < 0) {
            XSSFCellFill cellFill = new XSSFCellFill((CTFill) sourceFill.getCTFill().copy(), sourceStyles.getIndexedColors());
            targetFillIdx = targetStyles.putFill(cellFill);
        }

        CTXf targetXf = (CTXf) sourceXf.copy();
        targetXf.setNumFmtId(targetNumFmtIdx);
        targetXf.setBorderId(targetBorderIdx);
        targetXf.setFontId(targetFontIdx);
        targetXf.setFillId(targetFillIdx);

        return targetXf;
    }
}
