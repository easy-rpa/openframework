package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.SpreadsheetUtil;
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
 * Implement script with code and parameters defined.
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
     * @param script the text of VB script or name of the vbs resource ends with
     *               .vbs
     */
    public VBScript(String script) {
        setText(getVbsFileContent(script));
    }

    /**
     * New script with code and parameters defined.
     *
     * @param script the code of vb script
     * @param args   the parameters of script
     */
    public VBScript(String script, String... args) {
        this(script);
        params(args);
    }

    /**
     * Perform remote script on Excel Document. This methods get script code from
     * system resources by the script name (getScriptName()), and run this script
     * remotely using excelFilePath and script args as parameters.
     *
     * @param excelFilePath remote file path of the excel spreadsheet
     * @throws RuntimeException on error of getting script code or error on script
     *                          execution
     */
    public void perform(String excelFilePath) {
        try {

            // Populate arguments
            // First argument is file path
            String escapedFilePath = SpreadsheetUtil.normalizeFilePath(excelFilePath);
            List<String> argsList = new ArrayList<>();
            argsList.add(escapedFilePath);
            if (getParameters() != null) {
                argsList.addAll(getParameters());
            }

            // Run vbs
            runVbs(getText(), argsList.toArray(new String[0]));

        } catch (Exception e) {
            throw new RuntimeException("Error on process vbs script " + this.toString(), e);
        }
    }

    public String getText() {
        return vbsText;
    }

    /**
     * @param vbsText the vb script code to set
     */
    public void setText(String vbsText) {
        this.vbsText = vbsText;
    }

    /**
     * @return the parameters
     */
    public List<String> getParameters() {
        return vbsParameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<String> parameters) {
        vbsParameters.clear();
        vbsParameters.addAll(parameters);
    }

    /**
     * Set parameters to the script and return this script
     *
     * @param args the parameters to set
     */
    public VBScript params(String... args) {
        setParameters(Arrays.asList(args));
        return this;
    }

    private String getVbsFileContent(String vbScript) {
        String vbsContent;
        // Check if vbScript is file nameb
        if (vbScript != null && vbScript.endsWith(".vbs")) {
            // Get from resources
            try {
                // vbsContent = IOUtils.resourceToString(resourceName,
                // Charset.forName("UTF-8"));

                vbsContent = IOUtils.toString(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(vbScript)), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Getting of vbs text from '%s' has failed.", vbScript), e);
            }

        } else {
            // Work with vbScript as vb script text
            vbsContent = vbScript;
        }

        if (vbsContent == null) {
            throw new RuntimeException("Can't load text for script: " + vbScript);
        }
        return vbsContent;
    }

    /**
     * Store vbs code to the file on Agent, run it and delete the file. Method will
     * throw RuntimeException if script output don't contains EXIT_SUCCESS string.
     *
     * @param vbsText - text
     * @param args    -
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
                throw new RuntimeException(String.format("Execution of VBS '%s' has failed with error '%s'", vbsFile.getName(), result));
            }

        } finally {
            if (vbsFile != null) {
                FileUtils.deleteQuietly(vbsFile);
            }
        }
    }

    /**
     * Launch cscript command with file name and args on Agent using GroovyScript.
     *
     * @param fileName The full name of the vbs script file.
     * @param args     -
     * @return text of script output
     */
    private static String launchCScriptCommand(String fileName, String... args) {
        List<String> command = new ArrayList<>();
        StringBuilder output = new StringBuilder();
        try {

            command.add("cscript");
            command.add(fileName);
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
     * @param argument -
     * @return -
     */
    private static boolean inBrackets(String argument) {
        return argument.startsWith("\"") || argument.endsWith("\"");
    }

}