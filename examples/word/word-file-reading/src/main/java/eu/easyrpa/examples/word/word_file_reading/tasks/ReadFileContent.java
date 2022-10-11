package eu.easyrpa.examples.word.word_file_reading.tasks;

import eu.easyrpa.openframework.word.WordDocument;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApTaskEntry(name = "Read File Content")
@Slf4j
public class ReadFileContent extends ApTask {

    @Configuration(value = "source.document.file")
    private String sourceDocFile;

    @Override
    public void execute() throws Exception {
        log.info("Read the content of document located at: {}", sourceDocFile);
        WordDocument doc = new WordDocument(sourceDocFile);

        log.info("Getting all objects from document.");
        List<Object> objectList = doc.read();

        for (Object o : objectList) {
            log.info("This is a object : '{}'", o);
        }

    }
}
