package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * Exports specified sheet of Excel file into PDF
 * <p>
 * Example of script execution : 'cscript "C:/scripts/exportToPDF.vbs"
 * "C:/Users/user1/AppData/Local/Temp/User Report.xlsx" "Sheet0"
 * "C:/Users/user1/Sheet0.pdf"
 * <p>
 * Argument 0: Excel file to proceed Argument 1: Sheet name (Tab name) Argument
 * 2: output PDF file path
 */
public class ExportToPDF extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/exportToPDF.vbs";

    public ExportToPDF(String sheetName, String pdfFilePath) {
        super(VBS_FILE_PATH);
        params(sheetName, pdfFilePath);
    }

    public ExportToPDF sheetName(String sheetName) {
        getParameters().set(0, sheetName);
        return this;
    }

    public ExportToPDF pdfFilePath(String filePath) {
        getParameters().set(1, filePath);
        return this;
    }
}
