package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * VBS macro remote runner. This class uses runMacro.vbs script to run macro
 * from excel file. Macros is applying to the Excel file from the filePath
 * specified in perform() method. The excelFilePath for the call @perform(String
 * excelFilePath) must end with .xlsm.
 */
public class MacroRunner extends VBScript {

    /**
     * Name of the vbs macros - runMacro.vbs Can be place to the class path as
     * resource, for example to the RPA\rpa-grid\dependency\resources.
     */
    public static final String VBS_FILE_PATH = "vbscript/runMacro.vbs";

    /**
     * Construct empty MacroRunner script. Methods to add parameters must be used
     * before perform();
     */
    public MacroRunner() {
        this("");
    }

    /**
     * New instance of VB script runner with name of macro to be executed. Note: The
     * excel file with macros must have .xlsm extension to be executed;
     *
     * @param macroName the name of macro to run
     */
    public MacroRunner(String macroName) {
        super(VBS_FILE_PATH);
        params(macroName);
    }

    /**
     * Set macro name to the script
     *
     * @param macroName -
     * @return -
     */
    public MacroRunner macroName(String macroName) {
        getParameters().set(0, macroName);
        return this;
    }

}
