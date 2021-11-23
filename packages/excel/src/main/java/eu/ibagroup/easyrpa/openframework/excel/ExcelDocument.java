package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod;
import eu.ibagroup.easyrpa.openframework.excel.exceptions.VBScriptExecutionException;
import eu.ibagroup.easyrpa.openframework.excel.internal.PoiElementsCache;
import eu.ibagroup.easyrpa.openframework.excel.utils.FilePathUtils;
import eu.ibagroup.easyrpa.openframework.excel.vbscript.MacroRunner;
import eu.ibagroup.easyrpa.openframework.excel.vbscript.VBScript;
import eu.ibagroup.easyrpa.openframework.excel.vbscript.VBScriptProcessor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.poifs.macros.Module;
import org.apache.poi.poifs.macros.VBAMacroReader;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExcelDocument implements Iterable<Sheet>, AutoCloseable {

    private int id = -1;

    private String filePath;

    private Workbook workbook;

    private Set<String> availableMacros = new HashSet<>();
    private Map<String, FormulaEvaluator> collaboratingEvaluators = new HashMap<>();

    private Pattern macroNamesExtractor = Pattern.compile("^Sub (\\w+).*$", Pattern.MULTILINE);

    /**
     * Create empty Excel Document.
     */
    public ExcelDocument() {
        this((InputStream) null);
    }

    /**
     * Create new Excel Document. Creates and set workbook from input stream
     * specified. Set first workbook sheet as active sheet.
     *
     * @param is input stream with Excel workbook content. Creates empty document if it's null.
     */
    public ExcelDocument(InputStream is) {
        initWorkbook(is);
    }

    /**
     * Create new Excel Document for specified file.
     *
     * @param file input Excel file that needs to accessed via this document.
     * @throws IllegalArgumentException if <code>file</code> is null or not exist.
     */
    public ExcelDocument(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        try {
            setFilePath(file.getAbsolutePath());
            initWorkbook(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("File '%s' is not exist.", file.getAbsolutePath()), e);
        }
    }

    /**
     * Create new Excel Document for file specified using path.
     *
     * @param path the path to input Excel file that needs to accessed via this document.
     * @throws IllegalArgumentException if <code>path</code> is null or point to nonexistent file.
     */
    public ExcelDocument(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        try {
            setFilePath(path.toAbsolutePath().toString());
            initWorkbook(Files.newInputStream(path));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to read file '%s'. Perhaps is's not exist.", path), e);
        }
    }

    /**
     * Create new Excel Document for specified file.
     *
     * @param filePath the path to input Excel file that needs to accessed via this document.
     * @throws IllegalArgumentException if <code>filePath</code> is null or point to nonexistent file.
     */
    public ExcelDocument(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null.");
        }
        File file = FilePathUtils.getFile(filePath);
        try {
            setFilePath(file.getAbsolutePath());
            initWorkbook(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("File '%s' is not exist.", filePath), e);
        }
    }

    /**
     * @return the filePath if specified. Otherwise <code>null</code>.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Set file path for this Excel Document. The Excel Document is saved to file defined by this path when
     * method <code>save()</code> is called.
     *
     * @param filePath the absolute path to file where excel document is saved when method <code>save()</code> is called.
     */
    public void setFilePath(String filePath) {
        this.filePath = FilePathUtils.normalizeFilePath(filePath);
    }

    /**
     * Get file name for this Excel Document.
     *
     * @return name of file from file path if it's specified. Otherwise returns default name with
     * extension corresponding to this Excel Document type.
     */
    public String getFileName() {
        if (filePath != null) {
            return FilenameUtils.getName(filePath);
        }
        return "spreadsheet" + getExtension();
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
        if (hasMacros()) {
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
        if (hasMacros()) {
            return ".xlsm";
        }
        return workbook != null ? (workbook instanceof XSSFWorkbook ? ".xlsx" : ".xls") : ".xlsx";
    }

    /**
     * Check whether current Excel Document has macros that can be executed.
     *
     * @return <code>true</code> if document has macros.
     */
    public boolean hasMacros() {
        return availableMacros.size() > 0;
    }

    /**
     * Gets actual content of this Excel Document as input stream.
     *
     * @return byte array input stream with Excel Document content.
     */
    public InputStream getInputStream() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Overwrite the original file specified by <code>filePath</code> with actual content of this Excel Document.
     */
    public void save() {
        if (filePath != null) {
            saveAs(filePath);
        }
    }

    /**
     * Save this Excel Document to specified file. This will create parent folders if do not exist and
     * create a file if not exists and throw a exception if file object is a directory or cannot be written to.
     *
     * @param filePath the path of the file to write.
     */
    public void saveAs(String filePath) {
        try {
            filePath = FilePathUtils.normalizeFilePath(filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new RuntimeException(String.format("Failed to create a new file at '%s'. Something went wrong.", filePath));
                }
            }
            try (FileOutputStream out = new FileOutputStream(file, false)) {
                workbook.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to save excel document to file located at '%s'.", filePath), e);
        }
    }

    /**
     * Gets Apache POI workbook of this Excel Document.
     *
     * @return workbook instance.
     */
    public Workbook getWorkbook() {
        return workbook;
    }

    /**
     * Update content of this Excel Document. Invokes Apache POI workbook reinitialization.
     *
     * @param is - input stream with contents.
     */
    public void update(InputStream is) {
        initWorkbook(is);
    }

    /**
     * Links external document to allow using of it in cell formulas.
     */
    public void linkExternalDocument(String ref, ExcelDocument externalDoc) {
        if (ref != null && externalDoc != null) {
            workbook.linkExternalWorkbook(ref, externalDoc.getWorkbook());
            collaboratingEvaluators.put(ref, externalDoc.getWorkbook().getCreationHelper().createFormulaEvaluator());
            CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(collaboratingEvaluators);
        }
    }

    /**
     * Sets custom data formatter for this Excel Document.
     *
     * @param formatter - instance of specific formatter
     * @see org.apache.poi.ss.usermodel.DataFormatter
     */
    public void setDataFormatter(DataFormatter formatter) {
        PoiElementsCache.setDataFormatter(id, formatter);
    }

    /**
     * Gets currently used data formatter for this Excel Document.
     *
     * @return instance of data formatter
     * @see org.apache.poi.ss.usermodel.DataFormatter
     */
    public DataFormatter getDataFormatter() {
        return PoiElementsCache.getDataFormatter(id);
    }


    /********************************************************
     * Methods to work with sheets
     ********************************************************/

    /**
     * Create a new sheet for this Excel Document and return the high level
     * representation. New sheet will set as active sheet. Will set existing sheet
     * as active sheet and return it if sheet with name specified is exist already
     *
     * @param sheetName The name to set for the sheet. Use 'null' if null.
     * @return Sheet representing the new sheet.
     */
    public Sheet createSheet(String sheetName) {
        String name = WorkbookUtil.createSafeSheetName(sheetName);
        org.apache.poi.ss.usermodel.Sheet activeSheet = workbook.getSheet(name);
        if (activeSheet == null) {
            activeSheet = workbook.createSheet(name);
        }
        int sheetIndex = workbook.getSheetIndex(activeSheet);
        workbook.setActiveSheet(sheetIndex);
        return new Sheet(this, sheetIndex);
    }

    /**
     * Create an Sheet from an existing sheet in the Excel Document. This new sheet will
     * be placed next to the source sheet.
     *
     * @param sheetName the name of sheet to clone.
     * @return Sheet representing the cloned sheet. Returns null if specified sheet not found.
     */
    public Sheet cloneSheet(String sheetName) {
        org.apache.poi.ss.usermodel.Sheet source = workbook.getSheet(sheetName);
        if (source != null) {
            org.apache.poi.ss.usermodel.Sheet clone = workbook.cloneSheet(workbook.getSheetIndex(source));
            return new Sheet(this, workbook.getSheetIndex(clone));
        }
        return null;
    }

    /**
     * Copy the content of sheet {@code sheetName} to another sheet with styles.
     * Destination sheet can be located in another Excel Document.
     *
     * @param sheetName - name of the sheet to copy
     * @param destSheet - destination sheet
     */
    public void copySheet(String sheetName, Sheet destSheet) {
        org.apache.poi.ss.usermodel.Sheet source = workbook.getSheet(sheetName);
        if (source == null) {
            throw new IllegalArgumentException(String.format("Sheet with name '%s' is not found to copy.", sheetName));
        }
        //TODO Rewrite based on ExcelCellStyle implementation
        SpreadsheetUtil.copySheet(source, destSheet.getPoiSheet());
    }

    /**
     * Copy the content of sheet {@code sheetName} to another sheet without styles.
     * Destination sheet can be located in another Excel Document.
     *
     * @param sheetName - name of the sheet to copy
     * @param destSheet - destination sheet
     */
    public void copySheetWithoutStyles(String sheetName, Sheet destSheet) {
        org.apache.poi.ss.usermodel.Sheet source = workbook.getSheet(sheetName);
        if (source == null) {
            throw new IllegalArgumentException(String.format("Sheet with name '%s' is not found to copy.", sheetName));
        }
        //TODO Rewrite based on ExcelCellStyle implementation
        SpreadsheetUtil.copySheetWithoutStyles(source, destSheet.getPoiSheet());
    }

    /**
     * Get names of all sheets
     *
     * @return List of sheet names
     */
    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }

    /**
     * Move specified sheet to a new position.
     *
     * @param sheetName the name of the sheet to reorder
     * @param newPos    the position that we want to move the sheet into (0 based)
     */
    public void moveSheet(String sheetName, int newPos) {
        workbook.setSheetOrder(sheetName, newPos);
    }

    /**
     * Removes sheet with the given name. Does nothing if sheet with given name not
     * found.
     *
     * @param sheetName of the sheet to remove
     */
    public void removeSheet(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        if (index < 0) {
            index = workbook.getSheetIndex(WorkbookUtil.createSafeSheetName(sheetName));
        }
        if (index >= 0) {
            workbook.removeSheetAt(index);
        }
    }

    /**
     * @return current active sheet.
     */
    public Sheet getActiveSheet() {
        return new Sheet(this, workbook.getActiveSheetIndex());
    }

    /**
     * Change the name of given sheet.
     *
     * @param sheet   - instance of Sheet that needs to renamed
     * @param newName - a new name for specified sheet
     */
    public void renameSheet(Sheet sheet, String newName) {
        int index = workbook.getSheetIndex(sheet.getPoiSheet());
        workbook.setSheetName(index, newName);
    }

    /**
     * Set the sheet with given index as active and return it.
     *
     * @param index - index of sheet that need to be activated.
     * @return instance of activated sheet.
     */
    public Sheet selectSheet(int index) {
        workbook.setActiveSheet(index);
        return new Sheet(this, index);
    }

    /**
     * Set the sheet with given name as active and return it.
     *
     * @param sheetName - name of sheet that need to be activated.
     * @return instance of activated sheet or <code>null</code> if sheet not found.
     */
    public Sheet selectSheet(String sheetName) {
        int i = workbook.getSheetIndex(sheetName);
        if (i < 0)
            return null;
        return selectSheet(i);
    }

    /**
     * Finds the sheet with a row that contains all given values and active it.
     *
     * @param values - list of values to match.
     * @return instance of found and activated sheet or <code>null</code> if sheet not found.
     */
    public Sheet findSheet(String... values) {
        return findSheet(MatchMethod.EXACT, values);
    }

    /**
     * Finds the sheet with a row that contains all given values and active it.
     *
     * @param matchMethod - method that defines how passed values are matched with each row values.
     * @param values      - list of values to match.
     * @return instance of found and activated sheet or <code>null</code> if sheet not found.
     * @see eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod
     */
    public Sheet findSheet(MatchMethod matchMethod, String... values) {
        int sheetIndex = 0;
        for (Sheet sheet : this) {
            Row row = sheet.findRow(matchMethod, values);
            if (row != null) {
                workbook.setActiveSheet(sheetIndex);
                return sheet;
            }
            sheetIndex++;
        }
        return null;
    }

    /***************************************************************
     * Methods to perform specific Excel functionality using VBS
     ***************************************************************/

    /**
     * Run the set of macros from this Excel Document.
     *
     * @param macros list of macros to execute.
     */
    public void runMacro(String... macros) {
        List<String> absentMacros = Arrays.asList(macros);
        if (hasMacros()) {
            absentMacros = Arrays.stream(macros).filter(m -> !availableMacros.contains(m)).collect(Collectors.toList());
            if (absentMacros.isEmpty()) {
                VBScriptProcessor processor = new VBScriptProcessor(this);
                for (String macroName : macros) {
                    processor.addScript(new MacroRunner(macroName));
                }
                processor.process();
            }
        }
        if (absentMacros.size() > 0) {
            throw new RuntimeException(String.format(
                    "Following macros are absent in Excel Document and cannot be executed: %s",
                    String.join(", ", absentMacros)
            ));
        }
    }

    /**
     * Run VB script for this Excel Document
     *
     * @param script - text of VB script or path to resource '.vbs' file
     * @throws VBScriptExecutionException with error description if execution of VB script failed.
     */
    public void runScript(String script) {
        new VBScriptProcessor(this).addScript(new VBScript(script)).process();
    }

    /**
     * Run VB script for this Excel Document
     *
     * @param script - instance of VBScript to run
     * @throws VBScriptExecutionException with error description if execution of VB script failed.
     */
    public void runScript(VBScript script) {
        new VBScriptProcessor(this).addScript(script).process();
    }


    public void createPivotTable(String pivotTableName, PivotTableParams ptParams) {
        //TODO Implement this
    }

    /**
     * @return Excel Document sheets iterator
     */
    @Override
    public Iterator<Sheet> iterator() {
        return new SheetIterator(workbook.getNumberOfSheets());
    }

    /**
     * Frees up all allocated resources.
     */
    @Override
    public void close() {
        if (id > 0) {
            PoiElementsCache.unregister(id);
        }
    }

    /***************************************************************
     * Protected methods
     ***************************************************************/
    protected int getId() {
        return id;
    }

    /***************************************************************
     * Private methods
     ***************************************************************/

    /**
     * Creates and set workbook from input stream specified. Set first workbook
     * sheet as active sheet.
     *
     * @param is - input stream with workbook contents. Creates workbook with empty
     *           sheet if is is null.
     */
    private void initWorkbook(InputStream is) {
        try {
//            POIXMLTypeLoader.DEFAULT_XML_OPTIONS.setLoadUseXMLReader(SAXParserFactory.newInstance().newSAXParser().getXMLReader());
            if (is == null) {
                workbook = new XSSFWorkbook();
                // New workbook doesn't have a sheet.
                // Create new one
                workbook.createSheet();
            } else {
                workbook = WorkbookFactory.create(is);
                workbook.setActiveSheet(0);
            }

            extractAvailableMacros(is);

            if (id > 0) {
                PoiElementsCache.unregister(id);
            } else {
                id = generateId();
            }

            PoiElementsCache.register(id, workbook);

            collaboratingEvaluators.clear();
            collaboratingEvaluators.put(FilenameUtils.getName(getFilePath()), PoiElementsCache.getEvaluator(id));

            // For debug propose
//            SpreadsheetUtil.outputPOILogsToConsole(1);
//            formulaEvaluator.setDebugEvaluationOutputForNextEval(true);

        } catch (Exception e) {
            throw new RuntimeException(String.format("Initializing of workbook for spreadsheet '%s' has failed.", getFilePath()), e);
        }
    }

    /**
     * Reads content of input stream and looks up modules with macros. Then extract names of available macros
     * from them using regexp.
     *
     * @param is - Excel file input stream
     */
    private void extractAvailableMacros(InputStream is) throws IOException {
        availableMacros.clear();
        if (is != null) {
            if (is instanceof FileInputStream) {
                ((FileInputStream) is).getChannel().position(0);
            }
            try (VBAMacroReader reader = new VBAMacroReader(is)) {
                List<Module> modules = reader.readMacroModules().values().stream()
                        .filter(m -> m.geModuleType() == Module.ModuleType.Module)
                        .collect(Collectors.toList());

                for (Module module : modules) {
                    Matcher matcher = macroNamesExtractor.matcher(module.getContent());
                    while (matcher.find()) {
                        availableMacros.add(matcher.group(1));
                    }
                }
            } catch (Exception e) {
                //do nothing
            }
        }
    }

    /**
     * @return unique Id for this Excel Document.
     */
    private int generateId() {
        return Integer.parseInt((int) (Math.random() * 100) + "" + (System.currentTimeMillis() % 1000000));
    }

    private class SheetIterator implements Iterator<Sheet> {

        private int index = 0;
        private int sheetsCount;

        public SheetIterator(int sheetsCount) {
            this.sheetsCount = sheetsCount;
        }

        @Override
        public boolean hasNext() {
            return index < sheetsCount;
        }

        @Override
        public Sheet next() {
            return new Sheet(ExcelDocument.this, index++);
        }
    }
}
