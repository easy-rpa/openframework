package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class PDFDocCreator {

    private PDDocument pdfDocument;

    public PDFDocCreator() {
        this.pdfDocument = new PDDocument();
    }

    public PDFDocCreator(PDDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public PDDocument getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(PDDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public PDFPageCreator addPage(PDPage page) throws IOException {
        return new PDFPageCreator(pdfDocument, new PDPageContentStream(pdfDocument, page));
    }

    public void safeDocument(String fileName) {
        try {
            pdfDocument.close();
            pdfDocument.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
