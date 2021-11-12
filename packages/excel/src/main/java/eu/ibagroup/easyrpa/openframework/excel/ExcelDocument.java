package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.vbscript.VBScript;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDocument {

    private InputStream inputStream;

    private Workbook workbook;

    private String filePath = null;
    private String fileName = null;

    private FormulaEvaluator formulaEvaluator;
    private Map<String, FormulaEvaluator> collaboratingEvaluators = new HashMap<String, FormulaEvaluator>();

    /**
     * Create empty spreadsheet document.
     */
    public ExcelDocument() {
        this((InputStream) null);
    }

    /**
     * Create empty spreadsheet document.
     *
     * @param fileName the name of the workbook. Will used to set name of the file
     *                 on Agent. Will use temp name with .xlsx extension if not
     *                 specified.
     */
    public ExcelDocument(String fileName) {
        this(null, fileName);
    }

    /**
     * Create new Spreadsheet Document. Creates and set workbook from input stream
     * specified. Set first workbook sheet as active sheet.
     *
     * @param is input stream with workbook contents. Creates empty workbook if is
     *           is null.
     */
    public ExcelDocument(InputStream is) {
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
    public ExcelDocument(InputStream is, String fileName) {
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
    public ExcelDocument(InputStream is, boolean onlyVBSMode) {
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
    public ExcelDocument(InputStream is, String fileName, boolean onlyVBSMode) {
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
     */
    public void save() {
        //TODO implement this
        //keep file path, not only file name, and then used it here to save.
    }

    /**
     * Write workbook to the file. This will create parent folders if do not exist and create a file if not exists and
     * throw a exception if file object is a directory or cannot be written to.
     *
     * @param filePath the path of the file to write.
     */
    public void saveAs(String filePath) {
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
        //TODO Implement this
        int wbId = -1;
//        if (ref != null) {
//            wbId = SpreadsheetUtil.addExternalSheet(workbook, ref, externalSheet);
//            if (!collaboratingEvaluators.containsKey(ref)) {
//                collaboratingEvaluators.put(ref, externalSheet.getWorkbook().getCreationHelper().createFormulaEvaluator());
//                CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(collaboratingEvaluators);
//            }
//        }
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
        //TODO Implement this
        return null;
//        checkWorkbook();
//        String name = WorkbookUtil.createSafeSheetName(sheetName);
//        Sheet activeSheet = workbook.getSheet(name);
//        if (activeSheet == null) {
//            activeSheet = workbook.createSheet(name);
//        }
//        workbook.setActiveSheet(workbook.getSheetIndex(activeSheet));
//        return activeSheet;
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
        //TODO Implement this
        return null;
//        checkWorkbook();
//        Sheet source = workbook.getSheet(sheetName);
//        return source != null ? workbook.cloneSheet(workbook.getSheetIndex(source)) : null;
    }

    /**
     * Copy the content of sheet {@code sheetName} to another sheet with styles.
     * Destination sheet can be located in another spreadsheet.
     *
     * @param sheetName - name of the sheet to copy
     * @param destSheet - destination sheet
     */
    public void copySheet(String sheetName, Sheet destSheet) {
        //TODO Implement this
//        checkWorkbook();
//        Sheet source = workbook.getSheet(sheetName);
//        if (source == null) {
//            throw new IllegalArgumentException(String.format("Sheet with name '%s' is not found to copy.", sheetName));
//        }
//        SpreadsheetUtil.copySheet(source, destSheet);
    }

    /**
     * Copy the content of sheet {@code sheetName} to another sheet without styles.
     * Destination sheet can be located in another spreadsheet.
     *
     * @param sheetName - name of the sheet to copy
     * @param destSheet - destination sheet
     */
    public void copySheetWithoutStyles(String sheetName, Sheet destSheet) {
        //TODO Implement this
//        checkWorkbook();
//        Sheet source = workbook.getSheet(sheetName);
//        if (source == null) {
//            throw new IllegalArgumentException(String.format("Sheet with name '%s' is not found to copy.", sheetName));
//        }
//        SpreadsheetUtil.copySheetWithoutStyles(source, destSheet);
    }

    /**
     * Get names of all sheets
     *
     * @return List of sheet names
     */
    public List<String> getSheetNames() {
        //TODO Implement this
        return null;
//        checkWorkbook();
//        List<String> sheetNames = new ArrayList<>();
//        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//            sheetNames.add(workbook.getSheetName(i));
//        }
//        return sheetNames;
    }

    /**
     * Sets the order of appearance for a given sheet.
     *
     * @param sheetName the name of the sheet to reorder
     * @param newPos    the position that we want to move the sheet into (0 based)
     */
    public void moveSheet(String sheetName, int newPos) {
        //TODO Implement this
//        checkWorkbook();
//        workbook.setSheetOrder(sheetName, newPos);
    }

    /**
     * Removes sheet with the given name. Does nothing if sheet with given name not
     * found.
     *
     * @param sheetName of the sheet to remove
     */
    public void removeSheet(String sheetName) {
        //TODO Implement this
//        checkWorkbook();
//        int index = workbook.getSheetIndex(sheetName);
//        if (index < 0) {
//            index = workbook.getSheetIndex(WorkbookUtil.createSafeSheetName(sheetName));
//        }
//        if (index >= 0) {
//            workbook.removeSheetAt(index);
//        }
    }

    /**
     * Returns the current active sheet.
     *
     * @return
     */
    public Sheet getActiveSheet() {
        //TODO Implement this
        return null;
//        checkWorkbook();
//        return workbook.getSheetAt(workbook.getActiveSheetIndex());
    }

    /**
     * Change the name of given sheet.
     *
     * @param newName
     */
    public void renameSheet(Sheet sheet, String newName) {
        //TODO Implement this
//        checkWorkbook();
//        int index = workbook.getSheetIndex(sheet);
//        workbook.setSheetName(index, newName);
    }

    /**
     * Set the sheet with given index as active and return
     *
     * @param index
     * @return
     */
    public Sheet selectSheet(int index) {
        //TODO Implement this
        return null;
//        workbook.setActiveSheet(index);
//        return workbook.getSheetAt(index);
    }

    /**
     * Set the sheet with given name as active and return. Returns null if sheet not
     * found.
     */
    public Sheet selectSheet(String sheetName) {
        //TODO Implement this
        return null;
//        checkWorkbook();
//        int i = workbook.getSheetIndex(sheetName);
//        if (i < 0)
//            return null;
//        return selectSheet(i);
    }

    /**
     * Set the sheet with a row that contains all given values as active and return
     * it. Returns null if sheet not found.
     */
    public Sheet findSheet(String... values) {
        //TODO Implement this
        return null;
//        checkWorkbook();
//        for (Sheet sheet : workbook) {
//            if (SpreadsheetUtil.findRow(sheet, values) >= 0) {
//                workbook.setActiveSheet(workbook.getSheetIndex(sheet));
//                return sheet;
//            }
//        }
//        return null;
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
        //TODO Implement this
//        if (!isMacro()) {
//            throw new RuntimeException("This document must be .xlsm file to run Macro.");
//        }
//        VBScriptProcessor processor = new VBScriptProcessor(this);
//        for (String macroName : macros) {
//            processor.addScript(new MacroRunner(macroName));
//        }
//        processor.process();
    }

    /**
     * Run VB script for the spreadsheet
     *
     * @param vbsFilePath
     */
    public void runScript(String vbsFilePath) {
        //TODO Implement this
//        new VBScriptProcessor(this).addScript(script).process();
    }

    /**
     * Run VB script for the spreadsheet
     *
     * @param script
     */
    public void runScript(VBScript script) {
        //TODO Implement this
//        new VBScriptProcessor(this).addScript(script).process();
    }


    public void createPivotTable(String pivotTableName, PivotTableParams ptParams) {
        //TODO Implement this
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
