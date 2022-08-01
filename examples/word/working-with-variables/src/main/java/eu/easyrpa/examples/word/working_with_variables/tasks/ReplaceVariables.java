package eu.easyrpa.examples.word.working_with_variables.tasks;


import eu.easyrpa.openframework.word.WordDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.docx4j.model.datastorage.migration.VariablePrepare;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ApTaskEntry(name = "Read File Content")
@Slf4j
public class ReplaceVariables extends ApTask {

    @Configuration(value = "source.document.file")
    private String sourceDocFile;

    @Configuration(value = "output.files.dir")
    private String outputFilesDir;

    @Override
    public void execute() throws Exception {
        log.info("Read the content of document located at: {}", sourceDocFile);
        WordDocument doc = new WordDocument(sourceDocFile);

        log.info("Create map with substituted variables.");
        Map<String, String> replacerMap = new HashMap<>();
        replacerMap.put("name", "John");
        replacerMap.put("year", "1992");
        replacerMap.put("city", "Warsaw");
        replacerMap.put("color", "purple");

        log.info("Replacing variables: {}", replacerMap);
        doc.mapVariables(replacerMap);

        String outputFilePath = FilenameUtils.separatorsToSystem(outputFilesDir + File.separator + "output.docx");
        log.info("Save file to '{}'.", outputFilePath);
        doc.saveAs(outputFilePath);
    }
}
