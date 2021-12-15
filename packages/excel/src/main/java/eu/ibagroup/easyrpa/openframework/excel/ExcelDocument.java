package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.constants.MatchMethod;
import eu.ibagroup.easyrpa.openframework.excel.exceptions.VBScriptExecutionException;
import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POISaveMemoryExtension;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is a main class of EasyRPA Open Framework Excel package.
 */
public class ExcelDocument implements Iterable<Sheet>, AutoCloseable {

    /**
     * Unique Excel Document identified.
     */
    private int id = -1;

    /**
     * Path to related to this document Excel file. It's a place where the document
     * is saved when method <code>save()</code> is called.
     */
    private String filePath;

    /**
     * Reference to related POI Workbook.
     */
    private Workbook workbook;

    private Set<String> availableMacros = new HashSet<>();
    private Map<String, FormulaEvaluator> collaboratingEvaluators = new HashMap<>();

    private Pattern macroNamesExtractor = Pattern.compile("^Sub (\\w+).*$", Pattern.MULTILINE);

    /**
     * Create empty Excel Document.
     */
    public ExcelDocument() {
        initWorkbook(null, false);
    }

    /**
     * Create empty Excel Document.
     *
     * @param saveMemoryMode switch on the mode which works slowly but allows to work with large files.
     */
    public ExcelDocument(boolean saveMemoryMode) {
        initWorkbook(null, saveMemoryMode);
    }

    /**
     * Create new Excel Document. Creates and set workbook from input stream
     * specified. Set first workbook sheet as active sheet.
     *
     * @param is input stream with Excel workbook content. Creates empty document if it's null.
     */
    public ExcelDocument(InputStream is) {
        initWorkbook(is, false);
    }

    /**
     * Create new Excel Document. Creates and set workbook from input stream
     * specified. Set first workbook sheet as active sheet.
     *
     * @param is             input stream with Excel workbook content. Creates empty document if it's null.
     * @param saveMemoryMode switch on the mode which works slowly but allows to work with large files.
     */
    public ExcelDocument(InputStream is, boolean saveMemoryMode) {
        initWorkbook(is, saveMemoryMode);
    }

    /**
     * Create new Excel Document for specified file.
     *
     * @param file input Excel file that needs to accessed via this document.
     * @throws IllegalArgumentException if <code>file</code> is null or not exist.
     */
    public ExcelDocument(File file) {
        this(file, false);
    }

    /**
     * Create new Excel Document for specified file.
     *
     * @param file           input Excel file that needs to accessed via this document.
     * @param saveMemoryMode switch on the mode which works slowly but allows to work with large files.
     * @throws IllegalArgumentException if <code>file</code> is null or not exist.
     */
    public ExcelDocument(File file, boolean saveMemoryMode) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
        try {
            setFilePath(file.getAbsolutePath());
            initWorkbook(new FileInputStream(file), saveMemoryMode);
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
        this(path, false);
    }

    /**
     * Create new Excel Document for file specified using path.
     *
     * @param path           the path to input Excel file that needs to accessed via this document.
     * @param saveMemoryMode switch on the mode which works slowly but allows to work with large files.
     * @throws IllegalArgumentException if <code>path</code> is null or point to nonexistent file.
     */
    public ExcelDocument(Path path, boolean saveMemoryMode) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }
        try {
            setFilePath(path.toAbsolutePath().toString());
            initWorkbook(Files.newInputStream(path), saveMemoryMode);
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
        this(filePath, false);
    }

    /**
     * Create new Excel Document for specified file.
     *
     * @param filePath       the path to input Excel file that needs to accessed via this document.
     * @param saveMemoryMode switch on the mode which works slowly but allows to work with large files.
     * @throws IllegalArgumentException if <code>filePath</code> is null or point to nonexistent file.
     */
    public ExcelDocument(String filePath, boolean saveMemoryMode) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null.");
        }
        File file = FilePathUtils.getFile(filePath);
        try {
            setFilePath(file.getAbsolutePath());
            initWorkbook(new FileInputStream(file), saveMemoryMode);
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
     * @return for
     * <tr><td>macro:</td><td><code>application/vnd.ms-excel.sheet.macroEnabled.12</code></td></tr>
     * <tr><td>xlsx:</td><td><code>application/vnd.openxmlformats-officedocument.spreadsheetml.sheet</code></td></tr>
     * <tr><td>other:</td><td><code>application/vnd.ms-excel</code></td></tr>
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
     * Overwrites the original file specified by <code>filePath</code> with actual content of this Excel Document.
     */
    public void save() {
        if (filePath != null) {
            saveAs(filePath);
        }
    }

    /**
     * Saves this Excel Document to specified file.
     * <p>
     * Overwrites the content of specified file if it's exist and creates new one otherwise.
     * Also it will create all necessary parent folders if they do not exist either.
     *
     * @param filePath the path of the file to write.
     * @throws RuntimeException if specified file is a directory or cannot be written to.
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
     * @param is input stream with contents.
     */
    public void update(InputStream is) {
        initWorkbook(is, false);
    }

    /**
     * Links external Excel Document to allow using it's data in formulas.
     * <p>
     * The file name of specified <code>externalDoc</code> is used as name of this document in formulas.
     *
     * @param externalDoc instance of external Excel Document that should be linked.
     */
    public void linkExternalDocument(ExcelDocument externalDoc) {
        if (externalDoc != null) {
            linkExternalDocument(externalDoc.getFileName(), externalDoc);
        }
    }

    /**
     * Links external Excel Document to allow using it's data in formulas.
     * <p>
     * In order for formulas such as "[MyOtherDoc.xlsx]Sheet3!$A$5" to be evaluated for this document,
     * related "MyOtherDoc" Excel Document must be linked using this method. Each external document needs linking
     * only once.
     *
     * @param name        the name of external Excel Document that will be used in cell formulas. E.g. "MyOtherDoc.xlsx"
     * @param externalDoc instance of external Excel Document that should be linked.
     */
    public void linkExternalDocument(String name, ExcelDocument externalDoc) {
        if (name != null && externalDoc != null) {
            try {
                name = new URI(null, null, name, null).toString();
            } catch (URISyntaxException e) {
                throw new RuntimeException(String.format("Name '%s' is invalid to be used as reference to external excel document.", name), e);
            }
            workbook.linkExternalWorkbook(name, externalDoc.getWorkbook());
            collaboratingEvaluators.put(name, externalDoc.getWorkbook().getCreationHelper().createFormulaEvaluator());
            CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(collaboratingEvaluators);
        }
    }

    /**
     * Sets custom data formatter for this Excel Document.
     *
     * @param formatter instance of specific formatter
     * @see org.apache.poi.ss.usermodel.DataFormatter
     */
    public void setDataFormatter(DataFormatter formatter) {
        POIElementsCache.setDataFormatter(id, formatter);
    }

    /**
     * Gets currently used data formatter for this Excel Document.
     *
     * @return instance of data formatter
     * @see org.apache.poi.ss.usermodel.DataFormatter
     */
    public DataFormatter getDataFormatter() {
        return POIElementsCache.getDataFormatter(id);
    }


    /*--------------------------------------------------------
                Methods to work with sheets
     ---------------------------------------------------------*/

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
     * Create a new sheet for this Excel Document and return the high level
     * representation. New sheet will set as active sheet. Will set existing sheet
     * as active sheet and return it if sheet with name specified is exist already.
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
     * Removes given sheet.
     *
     * @param sheet sheet to remove
     */
    public void removeSheet(Sheet sheet) {
        if (sheet.getDocument() == this) {
            workbook.removeSheetAt(sheet.getIndex());
        }
    }

    /**
     * @return current active sheet.
     */
    public Sheet getActiveSheet() {
        return new Sheet(this, workbook.getActiveSheetIndex());
    }

    /**
     * Set the sheet with given index as active and return it.
     *
     * @param index index of sheet that need to be activated.
     * @return instance of activated sheet.
     */
    public Sheet selectSheet(int index) {
        workbook.setActiveSheet(index);
        return new Sheet(this, index);
    }

    /**
     * Set the sheet with given name as active and return it.
     *
     * @param sheetName name of sheet that need to be activated.
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
     * @param values list of values to match.
     * @return instance of found and activated sheet or <code>null</code> if sheet not found.
     */
    public Sheet findSheet(String... values) {
        return findSheet(MatchMethod.EXACT, values);
    }

    /**
     * Finds the sheet with a row that contains all given values and active it.
     *
     * @param matchMethod method that defines how passed values are matched with each row values.
     * @param values      list of values to match.
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

     /*--------------------------------------------------------
      Methods to perform specific Excel functionality using VBS
     ---------------------------------------------------------*/

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
     * @param script text of VB script or path to resource '.vbs' file
     * @throws VBScriptExecutionException with error description if execution of VB script failed.
     */
    public void runScript(String script) {
        new VBScriptProcessor(this).addScript(new VBScript(script)).process();
    }

    /**
     * Run VB script for this Excel Document
     *
     * @param script instance of VBScript to run
     * @throws VBScriptExecutionException with error description if execution of VB script failed.
     */
    public void runScript(VBScript script) {
        new VBScriptProcessor(this).addScript(script).process();
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
            POIElementsCache.unregister(id);
        }
    }

     /*--------------------------------------------------------
                         Protected methods
     ---------------------------------------------------------*/

    protected int getId() {
        return id;
    }

    /*--------------------------------------------------------
                         Private methods
     ---------------------------------------------------------*/

    /**
     * Creates and set workbook from input stream specified. Set first workbook
     * sheet as active sheet.
     *
     * @param is             input stream with workbook contents. Creates workbook with empty
     *                       sheet if is is null.
     * @param saveMemoryMode switch on the mode which works slowly but allows to work with large files.
     */
    private void initWorkbook(InputStream is, boolean saveMemoryMode) {
        try {
            if (saveMemoryMode) {
                POISaveMemoryExtension.init();
            }

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
                POIElementsCache.unregister(id);
            } else {
                id = POIElementsCache.generateExcelDocumentId();
            }

            POIElementsCache.register(id, workbook);

            collaboratingEvaluators.clear();
            collaboratingEvaluators.put(FilenameUtils.getName(getFilePath()), POIElementsCache.getEvaluator(id));

        } catch (Exception e) {
            throw new RuntimeException(String.format("Initializing of workbook for spreadsheet '%s' has failed.", getFilePath()), e);
        }
    }

    /**
     * Reads content of input stream and looks up modules with macros. Then extract names of available macros
     * from them using regexp.
     *
     * @param is Excel file input stream
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
     * Sheets iterator. Allows iteration over all sheets present in Excel Document using "for" loop.
     */
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
