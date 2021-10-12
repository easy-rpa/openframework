package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import org.apache.commons.io.FilenameUtils;

/**
 * Saves spreadsheet document as specific file format using 'saveAs.vbs'
 * VBScript.
 */
public class SaveAs extends VBScript {

    private static final String VBS_FILE_PATH = "vbscript/saveAs.vbs";

    private String sheetName;
    private FileFormat outputFileFormat;
    private String outputFilePath;

    public enum FileFormat {
        CSV(".csv", "62"), XLSX(".xlsx", "51");

        private String extension;
        private String code;

        private FileFormat(String extension, String code) {
            this.extension = extension;
            this.code = code;
        }

        public String getExtension() {
            return extension;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * @param outputFileFormat - the format of output file
     */
    public SaveAs(FileFormat outputFileFormat) {
        super(VBS_FILE_PATH);
        fileFormat(outputFileFormat);
    }

    /**
     * @param sheetName        - name of sheet to save as
     * @param outputFileFormat - the format of output file
     */
    public SaveAs(String sheetName, FileFormat outputFileFormat) {
        super(VBS_FILE_PATH);
        sourceSheet(sheetName).fileFormat(outputFileFormat);
    }

    public void perform(String filePath) {

        // set name of the target file by adding .xlsx to the source file name
        String baseName = FilenameUtils.getBaseName(filePath);
        String fullPath = FilenameUtils.getFullPath(filePath);
        outputFilePath = FilenameUtils.separatorsToUnix(fullPath + baseName + outputFileFormat.getExtension());

        params(outputFilePath, outputFileFormat.getCode());
        if (sheetName != null) {
            getParameters().add(sheetName);
        }

        super.perform(filePath);
    }

    public SaveAs fileFormat(FileFormat fileFormat) {
        if (fileFormat == null) {
            throw new IllegalArgumentException("The taget file format must be specifed.");
        }
        outputFileFormat = fileFormat;
        return this;
    }

    public SaveAs sourceSheet(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }
}
