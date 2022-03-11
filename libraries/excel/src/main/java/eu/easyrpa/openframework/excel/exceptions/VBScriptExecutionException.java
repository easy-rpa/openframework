package eu.easyrpa.openframework.excel.exceptions;

/**
 * VB script execution exception. Throws in case of not 'SUCCESS' output of VBS file execution
 * using <code>cscript</code> command.
 */
public class VBScriptExecutionException extends RuntimeException {

    /**
     * VBS file name
     */
    private String vbsFileName;

    /**
     * Error description
     */
    private String outputError;

    /**
     * Constructs new instance of VB script execution exception
     *
     * @param vbsFileName - name of VBS file where an error is occurred.
     * @param outputError - text of <code>cscript</code> command execution output with error description.
     */
    public VBScriptExecutionException(String vbsFileName, String outputError) {
        super(String.format("Execution of VBS '%s' has failed with error: '%s'", vbsFileName, outputError));
        this.vbsFileName = vbsFileName;
        this.outputError = outputError;
    }

    /**
     * Get name of VBS file where an error is occurred.
     *
     * @return name of VBS file
     */
    public String getVbsFileName() {
        return vbsFileName;
    }

    /**
     * Get error description that has been returned as output of <code>cscript</code> command execution.
     *
     * @return text with error description. Contains error code after '#' with follows description.
     */
    public String getVBScriptError() {
        return outputError;
    }
}
