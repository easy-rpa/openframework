package eu.easyrpa.examples.excel.sheet_data_reading.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.excel.ExcelDocument;
import eu.easyrpa.openframework.excel.Sheet;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "Read Range of Data")
@Slf4j
public class ReadRangeOfData extends ApTask {

    @Configuration(value = "source.spreadsheet.file")
    private String sourceSpreadsheetFile;

    @Override
    public void execute() {
        String topLeftCellRef = "C4";
        String bottomRightCellRef = "M200";

        log.info("Read range of data from spreadsheet document located at: {}", sourceSpreadsheetFile);
        ExcelDocument doc = new ExcelDocument(sourceSpreadsheetFile);
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Get data range [ {} : {} ] of sheet '{}'.", topLeftCellRef, bottomRightCellRef, activeSheet.getName());
        List<List<Object>> data = activeSheet.getRange(topLeftCellRef, bottomRightCellRef);

        log.info("Fetched data:");
        data.forEach(rec -> log.info("{}", rec));

        log.info("Cells Data D8: {}", doc.selectSheet("Cells Data").getValue("D8"));
    }
}
