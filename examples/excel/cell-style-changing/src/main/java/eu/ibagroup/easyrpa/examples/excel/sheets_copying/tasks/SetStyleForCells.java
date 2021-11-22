package eu.ibagroup.easyrpa.examples.excel.sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelCellStyle;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.style.ExcelColors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.FillPatternType;

import java.io.File;

@ApTaskEntry(name = "Set Styles for Cells")
@Slf4j
public class SetStyleForCells extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() {

        String cell1Ref = "C3";
        String cell2Ref = "C4";
        String cell3Ref = "C5";

        log.info("Set cell styles for spreadsheet document located at '{}'", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);

        //change document file path to avoid overwriting of original file after calling of save() method.
        doc.setFilePath(outputFilesDir + File.separator + FilenameUtils.getName(doc.getFilePath()));

        Sheet activeSheet = doc.getActiveSheet();

        log.info("Change style for cell '{}' of  sheet '{}'", cell1Ref, activeSheet.getName());
        Cell cell = activeSheet.getCell(cell1Ref);
        ExcelCellStyle boldRedStyle = cell.getStyle().bold(true).color(ExcelColors.RED.get());
        cell.setStyle(boldRedStyle);

        log.info("Change style for cell '{}' of  sheet '{}'", cell2Ref, activeSheet.getName());
        activeSheet.getCell(cell2Ref).getStyle().italic(true).color(ExcelColors.BLUE.get()).apply();

        log.info("Set new style for cell '{}' of  sheet '{}'", cell3Ref, activeSheet.getName());
        ExcelCellStyle newStyle = new ExcelCellStyle().fontSize(14)
                .fill(FillPatternType.SOLID_FOREGROUND).background(ExcelColors.LIGHT_GREEN.get());
        activeSheet.getCell(cell3Ref).setStyle(newStyle);

        log.info("Save changes for the document.");
        doc.save();

        log.info("Style for cell '{}' has been specified successfully.", cell1Ref);
    }
}
