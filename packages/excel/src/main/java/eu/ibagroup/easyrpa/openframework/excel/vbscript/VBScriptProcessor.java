package eu.ibagroup.easyrpa.openframework.excel.vbscript;

import eu.ibagroup.easyrpa.openframework.excel.SpreadsheetDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Execute batch of scripts on spreadsheet excel file specified
 */
public class VBScriptProcessor {

    private final List<VBScript> scripts = new ArrayList<>();
    private SpreadsheetDocument document;

    /**
     * Construct new empty script processor.
     */
    public VBScriptProcessor() {
    }

    /**
     * Construct new script processor with spreadsheet document object specified.
     *
     * @param document - document
     */
    public VBScriptProcessor(SpreadsheetDocument document) {
        this.document = document;
    }

    /**
     * Run set of scripts on spreadsheet document.
     *
     * @throws RuntimeException if document not specified.
     * @throws RuntimeException if can't upload the document to temp file.
     * @throws RuntimeException if can't update the document with content from temp
     *                          file.
     */
    public void process() {
        if (document == null)
            throw new RuntimeException("SpreadsheetDocument must be defined!");

        // Upload document to temp file
        File tmpDocFile = null;
        try {
            try {
                tmpDocFile = File.createTempFile(FilenameUtils.getBaseName(document.getFileName()), document.getExtension());
                FileUtils.copyInputStreamToFile(document.getInputStream(), tmpDocFile);
            } catch (IOException ioe) {
                throw new RuntimeException(String.format("Cannot upload spreadsheet '%s' to temp file.", document.getFileName()), ioe);
            }
            // Execute scripts
            for (VBScript script : scripts) {
                script.perform(tmpDocFile.getAbsolutePath());
            }

            // Download document from temp file
            try {
                document.updateInputStream(new FileInputStream(tmpDocFile));
            } catch (FileNotFoundException fnfe) {
                throw new RuntimeException(String.format("Cannot get updated spreadsheet '%s' from temp file.", document.getFileName()), fnfe);
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
     * Set spreadsheet.
     */
    public VBScriptProcessor document(SpreadsheetDocument spreadsheetDocument) {
        document = spreadsheetDocument;
        return this;
    }
}
