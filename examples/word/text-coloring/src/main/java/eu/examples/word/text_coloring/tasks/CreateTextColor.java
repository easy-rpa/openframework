package eu.examples.word.text_coloring.tasks;

import eu.easyrpa.openframework.word.TextRange;
import eu.easyrpa.openframework.word.WordDocument;
import eu.easyrpa.openframework.word.constants.Colors;
import eu.easyrpa.openframework.word.constants.FontFamily;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Create Text Color")
@Slf4j
public class CreateTextColor extends ApTask {

    @Configuration(value = "source.document.file")
    private String sourceDocFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;


    @Override
    public void execute() throws Exception {
        log.info("Read the content of document located at: {}", sourceDocFile);
        WordDocument doc = new WordDocument(sourceDocFile);

        log.info("Find text by regexp");
        TextRange range = doc.findText(".*Java.*");

        log.info("Color word red.");
        range.format().color(Colors.RED);

        log.info("Navigate the cursor one word to the right.");
        range.expandRight();
        range.expandRight();

        log.info("Color the first word red.");
        range.format().color(Colors.RED);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + "output.docx");
        log.info("Save file to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);
    }
}
