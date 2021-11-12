package eu.ibagroup.easyrpa.examples.excel.sheets_copying.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.excel.style.ExcelCellStyle;
import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import eu.ibagroup.easyrpa.openframework.excel.Sheet;
import eu.ibagroup.easyrpa.openframework.excel.style.ExcelColors;
import lombok.extern.slf4j.Slf4j;

@ApTaskEntry(name = "Set Styles for Cells")
@Slf4j
public class SetStyleForCells extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {

        String cellRef = "C3";

        log.info("Set cell styles for spreadsheet document located at '{}'", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Set style for cell '{}' of  sheet '{}'", cellRef, activeSheet.getName());
        activeSheet.getCell(cellRef).setStyle(new ExcelCellStyle().bold().italic().color(ExcelColors.RED));

        log.info("Save changes for the document.");
        doc.save();

        log.info("Style for cell '{}' has been specified successfully.", cellRef);
    }
}
