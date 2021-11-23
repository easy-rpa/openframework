package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.ExcelDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Execute batch of scripts on spreadsheet excel file
 */
public class VBScriptProcessor {

    private final List<VBScript> scripts = new ArrayList<>();
    private ExcelDocument document;

    /**
     * Construct new empty script processor.
     */
    public VBScriptProcessor() {
    }

    /**
     * Construct new script processor with target Excel Document specified.
     *
     * @param document - instance of Excel Document
     */
    public VBScriptProcessor(ExcelDocument document) {
        this.document = document;
    }

    /**
     * Run set of scripts on Excel Document.
     *
     * @throws RuntimeException if document not specified.
     * @throws RuntimeException if uploading the document to temp file failed.
     * @throws RuntimeException if updating the document with content from temp file failed.
     */
    public void process() {
        if (document == null)
            throw new RuntimeException("ExcelDocument must be specified!");

        // Upload document to temp file
        File tmpDocFile = null;
        try {
            try {

                tmpDocFile = File.createTempFile(FilenameUtils.getBaseName(document.getFileName()), document.getExtension());
                FileUtils.copyInputStreamToFile(document.getInputStream(), tmpDocFile);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Uploading of Excel Document '%s' to temp file has failed.", document.getFileName()), e);
            }
            // Execute scripts
            for (VBScript script : scripts) {
                script.perform(tmpDocFile.getAbsolutePath());
            }

            // Download document from temp file
            try {
                document.update(new FileInputStream(tmpDocFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(String.format("Reading of updated Excel Document '%s' from temp file has failed.", document.getFileName()), e);
            }
        } finally {
            if (tmpDocFile != null) {
                FileUtils.deleteQuietly(tmpDocFile);
            }
        }
    }

    /**
     * Add script to the batch.
     */
    public VBScriptProcessor addScript(VBScript script) {
        scripts.add(script);
        return this;
    }

    /**
     * Set target Excel Document.
     */
    public VBScriptProcessor document(ExcelDocument document) {
        this.document = document;
        return this;
    }
}
