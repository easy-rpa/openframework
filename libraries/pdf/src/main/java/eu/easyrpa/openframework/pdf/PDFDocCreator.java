package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.IOException;
import java.util.Calendar;

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

    public void managePDFPassword(String ownerPassword, String userPassword) {
        AccessPermission accessPermission = new AccessPermission();
        StandardProtectionPolicy sp = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
        sp.setPermissions(accessPermission);
        sp.setEncryptionKeyLength(128);
        try {
            pdfDocument.protect(sp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDocumentProperties(String creator, String author, String producer, String subject, String keywords, Calendar creationDate) {
        PDDocumentInformation documentInformation = pdfDocument.getDocumentInformation();
        documentInformation.setCreator(creator);
        documentInformation.setAuthor(author);
        documentInformation.setCreationDate(creationDate);
        documentInformation.setProducer(producer);
        documentInformation.setSubject(subject);
        documentInformation.setKeywords(keywords);
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
