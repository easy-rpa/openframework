package eu.ibagroup.easyrpa.openframework.excel.vbscript;

/**
 * VB script to export specified sheet of Excel file into PDF.
 * <br>
 * This class uses <code>exportToPDF.vbs</code> script.
 * <br><br>
 * Example of cscript command that this class initiate:
 * <br><br>
 * <code>cscript "C:/scripts/exportToPDF.vbs" "C:/Users/user1/AppData/Local/Temp/User Report.xlsx" "Sheet0" "C:/Users/user1/Sheet0.pdf"</code>
 * <ul>
 *     <li>Argument 0: Excel file path to proceed</li>
 *     <li>Argument 1: name of sheet to export</li>
 *     <li>Argument 2: output PDF file path</li>
 * </ul>
 */
public class ExportToPDF extends VBScript {

    public static final String VBS_FILE_PATH = "vbscript/exportToPDF.vbs";

    public ExportToPDF(String sheetName, String pdfFilePath) {
        super(VBS_FILE_PATH);
        params(sheetName, pdfFilePath);
    }

    /**
     * Sets value of Argument 1 that contains sheet name to export.
     *
     * @param sheetName - name of sheet to export.
     * @return instance of this VBScript
     */
    public ExportToPDF sheetName(String sheetName) {
        getParameters().set(0, sheetName);
        return this;
    }

    /**
     * Sets value of Argument 2 that contains path to PDF file where sheet content should be exported.
     *
     * @param filePath - output PDF file path.
     * @return instance of this VBScript
     */
    public ExportToPDF pdfFilePath(String filePath) {
        getParameters().set(1, filePath);
        return this;
    }
}
