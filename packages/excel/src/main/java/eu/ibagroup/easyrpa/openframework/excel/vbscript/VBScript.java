package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.exceptions.VBScriptExecutionException;
import eu.ibagroup.easyrpa.openframework.core.utils.FilePathUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents VB script with code and defined parameters.
 */
public class VBScript {

    /**
     * Constant to indicate vbs script was run successfully. Script must return this
     * value.
     */
    public static final String EXIT_SUCCESS = "SUCCESS";
    public static final String EXIT_FAILURE = "Error";

    private String vbsText = null;
    private final List<String> vbsParameters = new ArrayList<>();

    /**
     * New script with code.
     *
     * @param script the text of VB script or name of VBS resource ends with ".vbs".
     */
    public VBScript(String script) {
        setText(getVbsFileContent(script));
    }

    /**
     * New script with code and parameters.
     *
     * @param script the code of vb script
     * @param args   the parameters of script
     */
    public VBScript(String script, String... args) {
        this(script);
        params(args);
    }

    /**
     * Performs remote script on Excel Document.
     * <br>
     * This methods takes script text and runs its remotely for given Excel file.
     *
     * @param excelFilePath path to the target Excel file.
     * @throws VBScriptExecutionException on getting of not 'SUCCESS' output of script execution.
     * @throws RuntimeException           on error of getting script text or error on script execution
     */
    public void perform(String excelFilePath) {
        try {
            // Populate arguments
            // First argument is file path
            String escapedFilePath = FilePathUtils.normalizeFilePath(excelFilePath);
            List<String> argsList = new ArrayList<>();
            argsList.add(escapedFilePath);
            if (getParameters() != null) {
                argsList.addAll(getParameters());
            }

            // Run vbs
            runVbs(getText(), argsList.toArray(new String[0]));

        } catch (VBScriptExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error on process vbs script " + this.toString(), e);
        }
    }

    /**
     * Gets VB script as text
     *
     * @return text of this VB script
     */
    public String getText() {
        return vbsText;
    }

    /**
     * Sets script code for this VB script object
     *
     * @param vbsText the VB script code to set
     */
    public void setText(String vbsText) {
        this.vbsText = vbsText;
    }

    /**
     * Gets current parameters specified for this VB script
     *
     * @return ordered list of script parameters
     */
    public List<String> getParameters() {
        return vbsParameters;
    }

    /**
     * Sets list of ordered parameters for this VB script object.
     *
     * @param parameters list of parameters to set
     */
    public void setParameters(List<String> parameters) {
        vbsParameters.clear();
        vbsParameters.addAll(parameters);
    }

    /**
     * Sets parameters to the script and returns this VB script object
     *
     * @param args parameters to set
     * @return instance of this VB script
     */
    public VBScript params(String... args) {
        setParameters(Arrays.asList(args));
        return this;
    }

    /**
     * Checks whether specified VB script text is a path to resource ".vbs" file. If so, then reads this file
     * and retrieve it's content.
     *
     * @param vbScript text with VB script code or path to resource ".vbs" file.
     * @return text with VB script code.
     * @throws RuntimeException if reading of ".vbs" file failed.
     */
    private String getVbsFileContent(String vbScript) {
        // Check if vbScript is file name
        if (vbScript != null && vbScript.toLowerCase().endsWith(".vbs")) {
            // Get from resources
            try {
                return IOUtils.toString(
                        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(vbScript)),
                        StandardCharsets.UTF_8
                );
            } catch (Exception e) {
                throw new RuntimeException(String.format("Reading of VBS file '%s' has failed.", vbScript), e);
            }
        }
        return vbScript;
    }

    /**
     * Puts VBS code into temp file and executes it. After script execution the temp file is deleted.
     *
     * @param vbsText text with VBS code to execute.
     * @param args    VB script arguments
     * @throws RuntimeException if script output don't contains EXIT_SUCCESS string.
     */
    private static void runVbs(String vbsText, String... args) {

        File vbsFile = null;
        try {
            try {
                vbsFile = File.createTempFile("script_", ".vbs");
                FileUtils.writeStringToFile(vbsFile, vbsText, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Uploading of VBS to temp file has failed.", e);
            }

            String result = launchCScriptCommand(vbsFile.getAbsolutePath(), args);

            result = result.trim();
            if (!result.contains(EXIT_SUCCESS)) {
                throw new VBScriptExecutionException(vbsFile.getName(), result);
            }

        } finally {
            if (vbsFile != null) {
                FileUtils.deleteQuietly(vbsFile);
            }
        }
    }

    /**
     * Launch cscript command.
     *
     * @param filePath VBS file path to execute.
     * @param args     VB script arguments
     * @return execution output.
     */
    private static String launchCScriptCommand(String filePath, String... args) {
        List<String> command = new ArrayList<>();
        StringBuilder output = new StringBuilder();
        try {

            command.add("cscript");
            command.add(filePath);
            for (String argument : args) {
                command.add(String.format(inBrackets(argument) ? "%s" : "\"%s\"", argument));
            }

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process p = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                output.append(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Execution of command '%s' has failed.", String.join(" ", command)), e);
        }

        return output.toString();
    }


    /**
     * Check if string is in ""
     *
     * @param argument input string
     * @return true if in ""
     */
    private static boolean inBrackets(String argument) {
        return argument.startsWith("\"") || argument.endsWith("\"");
    }

}