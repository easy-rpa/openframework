package eu.easyrpa.openframework.excel.vbscript;

/**
 * Executes macro from target Excel Document.
 * <p>
 * This class uses runMacro.vbs script.
 */
public class MacroRunner extends VBScript {

    /**
     * Path to resource '.vbs' file related to this VB script.
     */
    public static final String VBS_FILE_PATH = "vbscript/runMacro.vbs";

    /**
     * Construct empty MacroRunner script.
     * <p>
     * Methods to add parameters must be used before perform().
     */
    public MacroRunner() {
        this("");
    }

    /**
     * Construct new instance of VB script runner with name of macro to execute.
     *
     * @param macroName - the name of macro to execute.
     */
    public MacroRunner(String macroName) {
        super(VBS_FILE_PATH);
        params(macroName);
    }

    /**
     * Set macro name to execute.
     *
     * @param macroName - the name of macro
     * @return instance of this macro runner.
     */
    public MacroRunner macroName(String macroName) {
        getParameters().set(0, macroName);
        return this;
    }
}
