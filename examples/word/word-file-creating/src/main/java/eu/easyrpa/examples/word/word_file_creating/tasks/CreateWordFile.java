package eu.easyrpa.examples.word.word_file_creating.tasks;

import eu.easyrpa.openframework.word.Picture;
import eu.easyrpa.openframework.word.WordDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Create Word File")
@Slf4j
public class CreateWordFile extends ApTask {

    @Configuration(value = "sample.picture.file")
    private String samplePictureFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() throws Exception {
        log.info("Create new Word document.");
        WordDocument wordDocument = new WordDocument();

        log.info("Create picture instance.");
        Picture picture = new Picture(samplePictureFile);

        log.info("Add picture to the end of document: {}", picture.getPicFile());
        wordDocument.append(picture);

        log.info("Add text to the end of document.");
        wordDocument.append("Hello Java-world");

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + "output.docx");
        log.info("Save file to '{}'.", outputFilePath);
        wordDocument.saveAs(outputFilePath);
    }
}
