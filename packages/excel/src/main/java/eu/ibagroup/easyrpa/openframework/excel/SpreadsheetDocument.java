package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.function.RecordMapper;
import eu.ibagroup.easyrpa.openframework.excel.vbscript.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Wrapper class for Excel Spreadsheet.
 */
public class SpreadsheetDocument {

    private InputStream inputStream;

    private Workbook workbook;

    private String fileName = null;

    private FormulaEvaluator formulaEvaluator;
    private Map<String, FormulaEvaluator> collaboratingEvaluators = new HashMap<String, FormulaEvaluator>();
    private DataFormatter dataFormatter = new DataFormatter();
    private String forcedDateFormat;

    /**
     * Create empty spreadsheet document.
     */
    public SpreadsheetDocument() {
        this((InputStream) null);
    }

    /**
     * Create empty spreadsheet document.
     *
     * @param fileName the name of the workbook. Will used to set name of the file
     *                 on Agent. Will use temp name with .xlsx extension if not
     *                 specified.
     */
    public SpreadsheetDocument(String fileName) {
        this(null, fileName);
    }

    /**
     * Create new Spreadsheet Document. Creates and set workbook from input stream
     * specified. Set first workbook sheet as active sheet.
     *
     * @param is input stream with workbook contents. Creates empty workbook if is
     *           is null.
     */
    public SpreadsheetDocument(InputStream is) {
        initWorkbook(is);
    }

    /**
     * Create new Spreadsheet Document. Creates and set workbook from input stream
     * specified. Set first workbook sheet as active sheet.
     *
     * @param is       input stream with workbook contents. Creates empty workbook
     *                 if is is null.
     * @param fileName the name of the workbook. Will used to set name of the file
     *                 on Agent. Will use temp name with .xlsx extension if not
     *                 specified.
     */
    public SpreadsheetDocument(InputStream is, String fileName) {
        this(is);
        setFileName(fileName);
    }

    /**
     * Create new Spreadsheet Document
     *
     * @param is          - input stream with spreadsheet content.
     * @param onlyVBSMode - if the value is true then Apache POI workbook won't be
     *                    initialized. In this case only VBS methods work.
     */
    public SpreadsheetDocument(InputStream is, boolean onlyVBSMode) {
        if (onlyVBSMode) {
            this.inputStream = is;
        } else {
            initWorkbook(is);
        }
    }

    /**
     * Create new Spreadsheet Document
     *
     * @param is          - input stream with spreadsheet content.
     * @param fileName    - the name of the workbook. Will used to set name of the
     *                    file on Agent. Will use temp name with .xlsx extension if
     *                    not specified.
     * @param onlyVBSMode - if the value is true then Apache POI workbook won't be
     *                    initialized. In this case only VBS methods work.
     */
    public SpreadsheetDocument(InputStream is, String fileName, boolean onlyVBSMode) {
        if (onlyVBSMode) {
            this.inputStream = is;
        } else {
            initWorkbook(is);
        }
        setFileName(fileName);
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return String.format("%s%s", fileName != null ? FilenameUtils.getBaseName(fileName) : "spreadsheet", getExtension());
    }

    /**
     * set file name of the excel workbook
     *
     * @param fileName the name of the workbook file. Will store the file name only,
     *                 without path.
     */
    public void setFileName(String fileName) {
        this.fileName = FilenameUtils.getName(fileName);
    }

    /**
     * Get content type (MIME type) of the workbook. May be used to pass this
     * document by HTTP
     *
     * @return for macro: application/vnd.ms-excel.sheet.macroEnabled.12 xlsx:
     * application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
     * other: application/vnd.ms-excel
     */
    public String getContentType() {
        if (isMacro()) {
            return "application/vnd.ms-excel.sheet.macroEnabled.12";
        }
        return workbook instanceof XSSFWorkbook ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "application/vnd.ms-excel";
    }

    /**
     * Get file extension based on type of the workbook.
     *
     * @return spreadsheet file extension.
     */
    public String getExtension() {
        if (isMacro()) {
            return ".xlsm";
        }
        return workbook != null ? (workbook instanceof XSSFWorkbook ? ".xlsx" : ".xls") : ".xlsx";
    }

    /**
     * Document contains macro if file name is null or have ".xlsm" extension
     *
     * @return
     */
    public boolean isMacro() {
        return fileName != null && "xlsm".equalsIgnoreCase(FilenameUtils.getExtension(fileName));
    }

    /**
     * Gets input stream of spreadsheet document.
     *
     * @return input stream or null on error occurs.
     */
    public InputStream getInputStream() {
        try {
            if (workbook != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                workbook.write(bos);
                return new ByteArrayInputStream(bos.toByteArray());
            } else {
                return inputStream;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write workbook to the file. This will create parent folders if do not exist and create a file if not exists and
     * throw a exception if file object is a directory or cannot be written to.
     *
     * @param filePath the path of the file to write.
     */
    public void writeToFile(String filePath) {
        try {
            File file = new File(filePath);
            file.createNewFile(); // if file already exists will do nothing
            if (workbook != null) {
                try (FileOutputStream out = new FileOutputStream(file, false)) {
                    workbook.write(out);
                }
            } else {
                FileUtils.copyInputStreamToFile(inputStream, file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets workbook of the spreadsheet.
     *
     * @return workbook instance.
     */
    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * Update input stream that contains content of current spreadsheet document. If
     * Apache POI is used then Apache POI Workbook will be reinitialized.
     *
     * @param is - input stream with contents.
     */
    public void updateInputStream(InputStream is) {
        if (workbook != null) {
            initWorkbook(is);
        } else {
            this.inputStream = is;
        }
    }

    /**
     * Links sheet from external document to allow using of it in cell formulas.
     */
    public int linkExternalSheet(String ref, Sheet externalSheet) {
        int wbId = -1;
        if (ref != null) {
            wbId = SpreadsheetUtil.addExternalSheet(workbook, ref, externalSheet);
            if (!collaboratingEvaluators.containsKey(ref)) {
                collaboratingEvaluators.put(ref, externalSheet.getWorkbook().getCreationHelper().createFormulaEvaluator());
                CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(collaboratingEvaluators);
            }
        }
        return wbId;
    }


    /********************************************************
     * Methods to work with sheets
     ********************************************************/

    /**
     * Create a new sheet for this Workbook and return the high level
     * representation. New sheet will set as active sheet. Will set existing sheet
     * as active sheet and return it if sheet with name specified is exist already
     *
     * @param sheetName The name to set for the sheet. Use 'null' if null.
     * @return Sheet representing the new sheet.
     */
    public Sheet createSheet(String sheetName) {
        checkWorkbook();
        String name = WorkbookUtil.createSafeSheetName(sheetName);
        Sheet activeSheet = workbook.getSheet(name);
        if (activeSheet == null) {
            activeSheet = workbook.createSheet(name);
        }
        workbook.setActiveSheet(workbook.getSheetIndex(activeSheet));
        return activeSheet;
    }

    /**
     * Create an Sheet from an existing sheet in the Workbook. This new sheet will
     * be placed next to the source sheet.
     *
     * @param sheetName the name of sheet to clone.
     * @return Sheet representing the cloned sheet. Returns null if sheet specified
     * not found.
     */
    public Sheet cloneSheet(String sheetName) {
        checkWorkbook();
        Sheet source = workbook.getSheet(sheetName);
        return source != null ? workbook.cloneSheet(workbook.getSheetIndex(source)) : null;
    }

    /**
     * Copy the content of sheet {@code sheetName} to another sheet with styles.
     * Destination sheet can be located in another spreadsheet.
     *
     * @param sheetName - name of the sheet to copy
     * @param destSheet - destination sheet
     */
    public void copySheet(String sheetName, Sheet destSheet) {
        checkWorkbook();
        Sheet source = workbook.getSheet(sheetName);
        if (source == null) {
            throw new IllegalArgumentException(String.format("Sheet with name '%s' is not found to copy.", sheetName));
        }
        SpreadsheetUtil.copySheet(source, destSheet);
    }

    /**
     * Copy the content of sheet {@code sheetName} to another sheet without styles.
     * Destination sheet can be located in another spreadsheet.
     *
     * @param sheetName - name of the sheet to copy
     * @param destSheet - destination sheet
     */
    public void copySheetWithoutStyles(String sheetName, Sheet destSheet) {
        checkWorkbook();
        Sheet source = workbook.getSheet(sheetName);
        if (source == null) {
            throw new IllegalArgumentException(String.format("Sheet with name '%s' is not found to copy.", sheetName));
        }
        SpreadsheetUtil.copySheetWithoutStyles(source, destSheet);
    }

    /**
     * Get names of all sheets
     *
     * @return List of sheet names
     */
    public List<String> getSheetNames() {
        checkWorkbook();
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }

    /**
     * Sets the order of appearance for a given sheet.
     *
     * @param sheetName the name of the sheet to reorder
     * @param newPos    the position that we want to move the sheet into (0 based)
     */
    public void moveSheet(String sheetName, int newPos) {
        checkWorkbook();
        workbook.setSheetOrder(sheetName, newPos);
    }

    /**
     * Removes sheet with the given name. Does nothing if sheet with given name not
     * found.
     *
     * @param sheetName of the sheet to remove
     */
    public void removeSheet(String sheetName) {
        checkWorkbook();
        int index = workbook.getSheetIndex(sheetName);
        if (index < 0) {
            index = workbook.getSheetIndex(WorkbookUtil.createSafeSheetName(sheetName));
        }
        if (index >= 0) {
            workbook.removeSheetAt(index);
        }
    }

    /**
     * Returns the current active sheet.
     *
     * @return
     */
    public Sheet getActiveSheet() {
        checkWorkbook();
        return workbook.getSheetAt(workbook.getActiveSheetIndex());
    }

    /**
     * Change the name of given sheet.
     *
     * @param newName
     */
    public void renameSheet(Sheet sheet, String newName) {
        checkWorkbook();
        int index = workbook.getSheetIndex(sheet);
        workbook.setSheetName(index, newName);
    }

    /**
     * Set the sheet with given index as active and return
     *
     * @param index
     * @return
     */
    public Sheet selectSheet(int index) {
        workbook.setActiveSheet(index);
        return workbook.getSheetAt(index);
    }

    /**
     * Set the sheet with given name as active and return. Returns null if sheet not
     * found.
     */
    public Sheet selectSheet(String sheetName) {
        checkWorkbook();
        int i = workbook.getSheetIndex(sheetName);
        if (i < 0)
            return null;
        return selectSheet(i);
    }

    /**
     * Set the sheet with a row that contains all given values as active and return
     * it. Returns null if sheet not found.
     */
    public Sheet selectSheetByRowValues(String... values) {
        checkWorkbook();
        for (Sheet sheet : workbook) {
            if (SpreadsheetUtil.findRow(sheet, values) >= 0) {
                workbook.setActiveSheet(workbook.getSheetIndex(sheet));
                return sheet;
            }
        }
        return null;
    }

    /**
     * Gets mapping of column header names to their index within active sheet.
     *
     * @param keyValues - set of value that is used to find header row
     * @return map where keys are column names in low case and values are their
     * index
     */
    public Map<String, Integer> getColumnIndex(String... keyValues) {
        return SpreadsheetUtil.getColumnIndex(getActiveSheet(), keyValues);
    }

    /**
     * Gets mapping of column header names to their index.
     *
     * @param sheetIndx - index of sheet to lookup at
     * @param keyValues - set of value that is used to find header row
     * @return map where keys are column names in low case and values are their
     * index
     */
    public Map<String, Integer> getColumnIndex(int sheetIndx, String... keyValues) {
        checkWorkbook();
        return SpreadsheetUtil.getColumnIndex(workbook.getSheetAt(sheetIndx), keyValues);
    }

    /**
     * Get list of records that contains active sheet.
     *
     * @param keyValues - values that identifies header row.
     * @return list of records where each record is a map between column name of
     * cell and its value with corresponding type (String, Double, Date
     * etc.). If header row is not found by specified values the list is
     * empty.
     */
    public List<Map<String, Object>> getRecords(String... keyValues) {
        return SpreadsheetUtil.getRecords(getActiveSheet(), formulaEvaluator, keyValues);
    }

    /**
     * Get list of records that contains specified sheet.
     *
     * @param sheetIndx - index of sheet to lookup at
     * @param keyValues - key value that identifies header row.
     * @return list of records where each record is a map between column name of
     * cell and its value with corresponding type (String, Double, Date
     * etc.). If header row is not found by specified values the list is
     * empty.
     */
    public List<Map<String, Object>> getRecords(int sheetIndx, String... keyValues) {
        checkWorkbook();
        return SpreadsheetUtil.getRecords(workbook.getSheetAt(sheetIndx), formulaEvaluator, keyValues);
    }

    /**
     * Get list of typed records that contains active sheet.
     *
     * @param recordMapper - a function that extracts a record of specified type for
     *                     each row.
     * @param keyValues    - values that identifies header row.
     * @return list of records where each record is a map between column name of
     * cell and its value with corresponding type (String, Double, Date
     * etc.). If header row is not found by specified values the list is
     * empty.
     */
    public <T> List<T> getTypedRecords(RecordMapper<T> recordMapper, String... keyValues) {
        return SpreadsheetUtil.getTypedRecords(getActiveSheet(), formulaEvaluator, recordMapper, keyValues);
    }

    /**
     * Get list of typed records that contains specified sheet.
     *
     * @param sheetIndx    - index of sheet to lookup at
     * @param recordMapper - a function that extracts a record of specified type for
     *                     each row.
     * @param keyValues    - values that identifies header row.
     * @return list of records where each record is a map between column name of
     * cell and its value with corresponding type (String, Double, Date
     * etc.). If header row is not found by specified values the list is
     * empty.
     */
    public <T> List<T> getTypedRecords(int sheetIndx, RecordMapper<T> recordMapper, String... keyValues) {
        return SpreadsheetUtil.getTypedRecords(workbook.getSheetAt(sheetIndx), formulaEvaluator, recordMapper, keyValues);
    }

    /**
     * Call function for each row of the active sheet starting from row with index
     * {@code startRowNum}.
     *
     * @param startRowNum - index of row to start.
     * @param function    - function that accepts two arguments. First is a POI row
     *                    object and the second is a list of typed cell values.
     */
    public void eachRow(int startRowNum, BiConsumer<Row, List<Object>> function) {
        SpreadsheetUtil.eachRow(getActiveSheet(), startRowNum, formulaEvaluator, function);
    }

    /**
     * Call function for each row of the specified sheet starting from row with
     * index {@code startRowNum}.
     *
     * @param sheetName   - name of sheet to go over.
     * @param startRowNum - index of row to start.
     * @param function    - function that accepts two arguments. First is a POI row
     *                    object and the second is a list of typed cell values.
     */
    public void eachRow(String sheetName, int startRowNum, BiConsumer<Row, List<Object>> function) {
        checkWorkbook();
        SpreadsheetUtil.eachRow(workbook.getSheet(sheetName), startRowNum, formulaEvaluator, function);
    }

    /**
     * Call function for each row of the specified sheet starting from row with
     * index {@code startRowNum}.
     *
     * @param sheetIndex  - index of sheet to go over.
     * @param startRowNum - index of row to start.
     * @param function    - function that accepts two arguments. First is a POI row
     *                    object and the second is a list of typed cell values.
     */
    public void eachRow(int sheetIndex, int startRowNum, BiConsumer<Row, List<Object>> function) {
        checkWorkbook();
        SpreadsheetUtil.eachRow(workbook.getSheetAt(sheetIndex), startRowNum, formulaEvaluator, function);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Gets data as list of rows from specified spreadsheet range. Every row is list
     * of typed cell values.
     *
     * @param startRef - reference to top left cell of the range to get data from.
     * @param endRef   - reference to bottom right cell of the range to get data
     *                 from.
     * @return data as list of lists
     */
    public List<List<Object>> getRange(String startRef, String endRef) {

        List<List<Object>> retData = new ArrayList<>();
        CellReference refStart = new CellReference(startRef);
        CellReference refEnd = new CellReference(endRef);

        int r1 = Math.min(refStart.getRow(), refEnd.getRow());
        int r2 = Math.max(refStart.getRow(), refEnd.getRow());
        int c1 = Math.min(refStart.getCol(), refEnd.getCol());
        int c2 = Math.max(refStart.getCol(), refEnd.getCol());

        for (int row = r1; row <= r2; row++) {
            List<Object> rowList = new ArrayList<>();
            for (int col = c1; col <= c2; col++) {
                rowList.add(getCellValue(new CellReference(row, col).formatAsString()));
            }
            retData.add(rowList);
        }

        return retData;
    }

    /**
     * Transfer data from list to the specified range of spreadsheet.
     *
     * @param startRef - left upper corner to start data transfer
     * @param data
     */
    public void putRange(String startRef, List<List<Object>> data) {

        CellReference refStart = new CellReference(startRef);
        int row = refStart.getRow();
        int col = refStart.getCol();

        for (List<Object> rowlist : data) {
            for (Object cellValue : rowlist) {
                String cellRef = new CellReference(row, col).formatAsString();
                SpreadsheetUtil.setCellValue(findCell(cellRef), cellValue);
                col++;
            }
            col = refStart.getCol();
            row++;
        }
    }

    /********************************************************
     * Methods to work with rows
     ********************************************************/

    /**
     * Finds the row with all specified values within active sheet.
     *
     * @param values
     * @return row number that contains all specified values
     */
    public Integer findRow(String... values) {
        return SpreadsheetUtil.findRow(getActiveSheet(), values);
    }

    /**
     * Finds the row with all specified values within specified sheet.
     *
     * @param sheetIndx
     * @param values
     * @return row number that contains all specified values
     */
    public Integer findRow(int sheetIndx, String... values) {
        checkWorkbook();
        return SpreadsheetUtil.findRow(workbook.getSheetAt(sheetIndx), values);
    }

    /**
     * Shifts up or down specified rows amount of currently active sheet.
     * <p>
     * If {@code rowsCount} is positive then all sheet content is shifted down with
     * inserting new rows. If {@code rowsCount} is negative all sheet content is
     * shifted up. In this case {@code rowCount} records above {@code startRow} will
     * be removed.
     *
     * @param startRow  - the row index to start shifting
     * @param rowsCount - the number of rows to shift
     */
    public void shiftRows(int startRow, int rowsCount) {
        SpreadsheetUtil.shiftRows(getActiveSheet(), startRow, rowsCount);
    }

    /**
     * Shifts up or down specified rows amount of specified sheet.
     * <p>
     * If {@code rowsCount} is positive then all sheet content is shifted down with
     * inserting new rows. If {@code rowsCount} is negative all sheet content is
     * shifted up. In this case {@code rowCount} records above {@code startRow} will
     * be removed.
     *
     * @param sheetName
     * @param startRow  - the row index to start shifting
     * @param rowsCount - the number of rows to shift
     */
    public void shiftRows(String sheetName, int startRow, int rowsCount) {
        checkWorkbook();
        SpreadsheetUtil.shiftRows(workbook.getSheet(sheetName), startRow, rowsCount);
    }

    /**
     * Shifts up or down specified rows amount of specified sheet.
     * <p>
     * If {@code rowsCount} is positive then all sheet content is shifted down with
     * inserting new rows. If {@code rowsCount} is negative all sheet content is
     * shifted up. In this case {@code rowCount} records above {@code startRow} will
     * be removed.
     *
     * @param sheetIndx
     * @param startRow  - the row index to start shifting
     * @param rowsCount - the number of rows to shift
     */
    public void shiftRows(int sheetIndx, int startRow, int rowsCount) {
        checkWorkbook();
        SpreadsheetUtil.shiftRows(workbook.getSheetAt(sheetIndx), startRow, rowsCount);
    }

    /********************************************************
     * Methods to work with cells
     ********************************************************/

    /**
     * Finds cell by its reference.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @return cell or null if cell not found
     */
    public Cell findCell(String cellRef) {
        checkWorkbook();
        CellReference ref = new CellReference(cellRef);
        String sheetName = ref.getSheetName();
        Sheet sheet = sheetName != null ? workbook.getSheet(sheetName) : workbook.getSheetAt(workbook.getActiveSheetIndex());
        Row row = sheet.getRow(ref.getRow());
        if (row != null) {
            return row.getCell(ref.getCol());
        }
        return null;
    }

    /**
     * Checks if cell is empty.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @return true if cell type is STRING and it's values is empty or cell type is
     * BLANK or ERROR. If cell type is BOOLEAN or NUMERIC returns false.
     */
    public boolean isCellEmpty(String cellRef) {
        return SpreadsheetUtil.isCellEmpty(findCell(cellRef), formulaEvaluator);
    }

    /**
     * Checks if cell is empty.
     *
     * @param cell - cell
     * @return true if cell type is STRING and it's values is empty or cell type is
     * BLANK or ERROR. If cell type is BOOLEAN or NUMERIC returns false.
     */
    public boolean isCellEmpty(Cell cell) {
        return SpreadsheetUtil.isCellEmpty(cell, formulaEvaluator);
    }

    /**
     * Checks if cell has error.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @return true if cell type or cell value is ERROR.
     */
    public boolean hasCellError(String cellRef) {
        return SpreadsheetUtil.hasCellError(findCell(cellRef), formulaEvaluator);
    }

    /**
     * Checks if cell has error.
     *
     * @param cell - cell
     * @return true if cell type or cell value is ERROR.
     */
    public boolean hasCellError(Cell cell) {
        return SpreadsheetUtil.hasCellError(cell, formulaEvaluator);
    }

    /**
     * Gets the value of the cell as a numeric.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @return numeric value of the cell or null if it's not possible to cast the
     * cell value to numeric or cell not found
     */
    public Double getCellValueAsNumeric(String cellRef) {
        return SpreadsheetUtil.getCellValueAsNumeric(findCell(cellRef), formulaEvaluator);
    }

    /**
     * Gets the value of the cell as a numeric.
     *
     * @param cell - cell
     * @return numeric value of the cell or null if it's not possible to cast the
     * cell value to numeric or cell not found
     */
    public Double getCellValueAsNumeric(Cell cell) {
        return SpreadsheetUtil.getCellValueAsNumeric(cell, formulaEvaluator);
    }

    /**
     * Gets the value of the cell as a String.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @return string value of the cell
     */
    public String getCellValueAsString(String cellRef) {
        return SpreadsheetUtil.getCellValueAsString(findCell(cellRef), dataFormatter, forcedDateFormat, formulaEvaluator);
    }

    /**
     * Gets the value of the cell as a String.
     *
     * @param cell
     * @return string value of the cell
     */
    public String getCellValueAsString(Cell cell) {
        return SpreadsheetUtil.getCellValueAsString(cell, dataFormatter, forcedDateFormat, formulaEvaluator);
    }

    /**
     * Sets specific locale for data formatter that is used to convert numeric and
     * date values to string in case of using getCellValueAsString method.
     *
     * @param locale
     */
    public void setDataFormatterLocal(Locale locale) {
        dataFormatter = new DataFormatter(locale);
    }

    /**
     * Sets specific date format that is used to convert date values to string in
     * case of using getCellValueAsString method.
     *
     * @param forcedDateFormat
     */
    public void forceDateFormat(String forcedDateFormat) {
        this.forcedDateFormat = forcedDateFormat;
    }

    /**
     * Sets cell value as BLANK
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     */
    public void clearCell(String cellRef) {
        SpreadsheetUtil.setCellValue(findCell(cellRef), null);
    }

    /**
     * Gets typed value of the cell.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @return typed value of the cell (Date for dates, String or RichTextString for
     * strings, Double for numerics and Boolean for booleans)
     */
    public Object getCellValue(String cellRef) {
        return SpreadsheetUtil.getCellValue(findCell(cellRef), formulaEvaluator);
    }

    /**
     * Gets typed value of the cell.
     *
     * @param cell
     * @return typed value of the cell (Date for dates, String or RichTextString for
     * strings, Double for numerics and Boolean for booleans)
     */
    public Object getCellValue(Cell cell) {
        return SpreadsheetUtil.getCellValue(cell, formulaEvaluator);
    }

    /**
     * Sets cell value.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @param value   - value to set
     */
    public void setCellValue(String cellRef, Object value) {
        SpreadsheetUtil.setCellValue(findCell(cellRef), value);
    }

    /**
     * Sets date cell value as STRINNG using specified format.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @param value   - date value to set
     * @param format  - date format to convert date to String. If format is null
     *                value.toString() is used.
     */
    public void setCellValue(String cellRef, Date value, DateFormat format) {
        String dateStr;
        if (format != null)
            dateStr = format.format(value);
        else
            dateStr = value.toString();
        SpreadsheetUtil.setCellValue(findCell(cellRef), dateStr);
    }

    /**
     * Sets a formula for the cell.
     *
     * @param cellRef - cell reference (A1, Sheet!B7 etc.)
     * @param formula - formula to set
     */
    public void setCellFormula(String cellRef, String formula) {
        SpreadsheetUtil.setCellValue(findCell(cellRef), CellType.FORMULA, formula);
    }

    /**
     * Gets formula evaluator that is currently used.
     *
     * @return instance of formula evaluator or null if this SpreadsheetDocument is
     * working in VBS mode without workbook.
     */
    public FormulaEvaluator getFormulaEvaluator() {
        return formulaEvaluator;
    }

    /***************************************************************
     * Methods to perform specific Excel functionality using VBS
     ***************************************************************/

    /**
     * Run the set of macros from this spreadsheet document.
     *
     * @param macros list of macros from this excel document to be executed
     */
    public void runMacro(String... macros) {
        // Check if this spreadsheet is macro enabled
        if (!isMacro()) {
            throw new RuntimeException("This document must be .xlsm file to run Macro.");
        }
        VBScriptProcessor processor = new VBScriptProcessor(this);
        for (String macroName : macros) {
            processor.addScript(new MacroRunner(macroName));
        }
        processor.process();
    }

    /**
     * Run VB script for the spreadsheet
     *
     * @param script
     */
    public void runScript(VBScript script) {
        new VBScriptProcessor(this).addScript(script).process();
    }

    /**
     * Extracts content of active sheet using exporting into CSV file. Usually it
     * used in case of large spreadsheets that are not possible to read using Apache
     * POI workbook.
     *
     * @return list of maps that present extracted records
     */
    public List<Map<String, Object>> runExtractDataViaCSV() {
        return runExtractDataViaCSV(null);
    }

    /**
     * Extracts content of specified sheet using exporting into CSV file. Usually it
     * used in case of large spreadsheets that are not possible to read using Apache
     * POI workbook.
     *
     * @param sheetName - name of sheet to extract data from
     * @return list of maps that present extracted records
     */
    public List<Map<String, Object>> runExtractDataViaCSV(String sheetName) {
        List<Map<String, Object>> data = new ArrayList<>();
        File csvFile = null;
        try {
            csvFile = new File(runSaveAsCSV(sheetName));

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8)) {
                Iterable<CSVRecord> csvRecords = CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader().parse(reader);
                for (CSVRecord csvRec : csvRecords) {
                    Map<String, Object> rec = new HashMap<>();
                    csvRec.toMap().forEach(rec::put);
                    data.add(rec);
                }
            }

            return data;
        } catch (Exception e) {
            throw new RuntimeException(sheetName != null
                    ? String.format("Extracting of data from '%s' sheet has failed.", sheetName)
                    : "Extracting of data from spreadsheet has failed", e);
        } finally {
            if (csvFile != null) {
                FileUtils.deleteQuietly(csvFile);
            }
        }
    }

    /**
     * Saves content of specified sheet as CSV file
     *
     * @param sheetName - name of sheet to export to CSV
     * @return path of output CSV file
     */
    public String runSaveAsCSV(String sheetName) {
        SaveAs saveAs = new SaveAs(sheetName, SaveAs.FileFormat.CSV);
        new VBScriptProcessor(this).addScript(saveAs).process();
        return saveAs.getOutputFilePath();
    }

    /**
     * Exports active sheet to PDF
     *
     * @param pdfFilePath
     */
    public void runExportToPDF(String pdfFilePath) {
        runScript(new ExportToPDF(getActiveSheet().getSheetName(), pdfFilePath));
    }

    /**
     * Apply CategoryFilter to active sheet
     *
     * @param category
     * @param filterColRange
     * @param fieldIndex
     */
    public void runCategoryFilter(String category, String filterColRange, String fieldIndex) {
        runScript(new CategoryFilter(getActiveSheet().getSheetName(), category, filterColRange, fieldIndex));
    }

    /**
     * Apply Filter to active sheet
     *
     * @param filterPattern
     * @param filterColRange
     * @param fieldIndex
     */
    public void runFilter(String filterPattern, String target, String filterColRange, String fieldIndex) {
        runScript(new Filter(getActiveSheet().getSheetName(), filterPattern, target, filterColRange, fieldIndex));
    }

    /**
     * Apply FilterPivotTable to active sheet
     *
     * @param pivotTableName
     * @param filterPattern
     */
    public void runFilterPivotTable(String pivotTableName, String filterPattern) {
        runScript(new FilterPivotTable().tabName(getActiveSheet().getSheetName()).tableName(pivotTableName).pattern(filterPattern));
    }

    /**
     * Insert empty column before colRef. Columns in colRef will shift to the right.
     *
     * @param colRef column reference letter
     */
    public void runInsertColumn(String colRef) {
        runScript(new InsertColumn().sheetName(getActiveSheet().getSheetName()).colRef(colRef));
    }

    public void runMultiFilterPivotTable(String tabName, String pivotTableName, String pivotTableField, String mode, String select, String unselect) {
        runScript(new MultiFilterPivotTable().tabName(getActiveSheet().getSheetName()).tableName(pivotTableName)
                .tableField(pivotTableField).mode(mode).select(select).unselect(unselect));
    }

    public void runPivotTable(PivotTableBuilder pivotTableBuilder) {
        runScript(pivotTableBuilder);
    }

    /**
     * Sort active sheet using header cell as column to sort
     *
     * @param headerCellRef
     * @param sortType
     */
    public void runSort(String headerCellRef, Sorter.SortingType sortType) {
        runScript(new Sorter(getActiveSheet().getSheetName(), headerCellRef, sortType, this));
    }

    /**
     * Converts xls to xlsx
     */
    public void runConvertToXlsx() {
        File convertedFile = null;
        try {
            SaveAs saveAs = new SaveAs(SaveAs.FileFormat.XLSX);
            new VBScriptProcessor(this).addScript(saveAs).process();
            convertedFile = new File(saveAs.getOutputFilePath());
            updateInputStream(new FileInputStream(convertedFile));
        } catch (Exception e) {
            throw new RuntimeException("Convertion of XLS spreadsheet to XLSX has failed.", e);
        } finally {
            if (convertedFile != null) {
                FileUtils.deleteQuietly(convertedFile);
            }
        }
    }

    /***************************************************************
     * Private methods
     ***************************************************************/

    private void checkWorkbook() {
        if (workbook == null) {
            throw new IllegalStateException("This function cannot be used in only VBS mode");
        }
    }

    /**
     * Creates and set workbook from input stream specified. Set first workbook
     * sheet as active sheet.
     *
     * @param is - input stream with workbook contents. Creates workbook with empty
     *           sheet if is is null.
     */
    private void initWorkbook(InputStream is) {
        try {
            if (is == null) {
                workbook = new XSSFWorkbook();
                // New workbook doesn't have a sheet.
                // Create new one
                workbook.createSheet();
            } else {
                workbook = WorkbookFactory.create(is);
                workbook.setActiveSheet(0);
            }

            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            collaboratingEvaluators.clear();
            collaboratingEvaluators.put(getFileName(), formulaEvaluator);

            // For debug propose
//            SpreadsheetUtil.outputPOILogsToConsole(1);
//            formulaEvaluator.setDebugEvaluationOutputForNextEval(true);

        } catch (Exception e) {
            throw new RuntimeException(String.format("Initializing of workbook for spreadsheet '%s' has failed.", getFileName()), e);
        }
    }
}
