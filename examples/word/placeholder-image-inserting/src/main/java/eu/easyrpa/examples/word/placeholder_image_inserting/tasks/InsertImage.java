package eu.easyrpa.examples.word.placeholder_image_inserting.tasks;

import eu.easyrpa.openframework.word.Picture;
import eu.easyrpa.openframework.word.WordDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@ApTaskEntry(name = "Insert Image In Placeholder")
@Slf4j
public class InsertImage extends ApTask {

    @Configuration(value = "source.document.file")
    private String sourceDocFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Configuration(value = "source.picture.file")
    private String sourcePicFile;

    private static final String PLACEHOLDER = "InsertImageHere";

    @Override
    public void execute() throws Exception {
        log.info("Read the content of document located at: {}", sourceDocFile);
        WordDocument doc = new WordDocument(sourceDocFile);

        log.info("Create picture instance.");
        Picture picture = new Picture(sourcePicFile);


        log.info("First, find text which will be replaced. And then replace with an image.");
        doc.findText(PLACEHOLDER).replaceWith(picture, doc.getOpcPackage());

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + "output.docx");
        log.info("Save file to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);
    }
}
