package eu.easyrpa.examples.google.sheets.cells_style_changing.tasks;

import eu.easyrpa.openframework.google.drive.GoogleDrive;
import eu.easyrpa.openframework.google.drive.model.GFileId;
import eu.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.easyrpa.openframework.google.sheets.*;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

@ApTaskEntry(name = "Set Styles for Cells")
@Slf4j
public class SetStyleForCells extends ApTask {

    private static final String RESULT_FILE_POSTFIX = "_STYLING_RESULT";

    @Configuration(value = "source.spreadsheet.file.id")
    private String sourceSpreadsheetFileId;

    @Inject
    private GoogleDrive googleDrive;

    @Inject
    private GoogleSheets googleSheets;

    @Override
    public void execute() {

        String cell1Ref = "C3";
        String cell2Ref = "C4";
        String cell3Ref = "C5";

        GFileInfo sourceSpreadsheetFile = createCopyOfSourceFile(sourceSpreadsheetFileId);
        if (sourceSpreadsheetFile == null) return;

        log.info("Set cell styles for spreadsheet with ID: {}", sourceSpreadsheetFile.getId());
        SpreadsheetDocument doc = googleSheets.getSpreadsheet(sourceSpreadsheetFile.getId());
        Sheet activeSheet = doc.getActiveSheet();

        log.info("Change style for cell '{}' of  sheet '{}'", cell1Ref, activeSheet.getName());
        Cell cell = activeSheet.getCell(cell1Ref);
        CellStyle boldRedStyle = cell.getStyle().bold(true).color(Color.RED);
        cell.setStyle(boldRedStyle);

        log.info("Change style for cell '{}' of  sheet '{}'", cell2Ref, activeSheet.getName());
        activeSheet.getCell(cell2Ref).getStyle().italic(true).color(Color.BLUE).apply();

        log.info("Set new style for cell '{}' of  sheet '{}'", cell3Ref, activeSheet.getName());
        CellStyle newStyle = new CellStyle().fontSize(14).background(Color.GREEN);
        activeSheet.getCell(cell3Ref).setStyle(newStyle);

        log.info("Styles have been changed successfully.");
    }

    private GFileInfo createCopyOfSourceFile(String sourceFileId) {
        log.info("Create copy of source spreadsheet file with ID '{}'.", sourceFileId);
        Optional<GFileInfo> sourceFile = googleDrive.getFileInfo(GFileId.of(sourceFileId));
        if (!sourceFile.isPresent()) {
            log.warn("Source spreadsheet file with ID '{}' not found.", sourceFileId);
            return null;
        }
        String nameOfCopy = sourceFile.get().getName() + RESULT_FILE_POSTFIX;
        Optional<GFileInfo> sourceCopy = googleDrive.copy(sourceFile.get(), nameOfCopy);
        if (!sourceCopy.isPresent()) {
            log.warn("Creating a copy of source spreadsheet file has failed by some reasons.");
            return null;
        }
        return sourceCopy.get();
    }
}
