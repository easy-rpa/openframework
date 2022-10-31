package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.fit.pdfdom.PDFDomTree;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PDFDoc {

    PDDocument pdfDocument;

    public PDFDoc(PDDocument document){
        this.pdfDocument = document;
    }

    public String readPDFDocument() {
        String resultTextFromPDF = "";

        try  {
            PDFTextStripper reader = new PDFTextStripper();
            reader.setSortByPosition(false);
            resultTextFromPDF = reader.getText(pdfDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultTextFromPDF;
    }

    public int getPDFPageCount() {
        return pdfDocument.getNumberOfPages();
    }

    public void getPDFasImage() {
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
        try  {
            for (int page = 0; page < pdfDocument.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                ImageIOUtil.writeImage(bim, pdfDocument.getDocumentInformation().getSubject() + "-" + (page + 1) + ".jpeg", 300);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergePDFiles(String destinationFileName, File... documents) {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        try {
            mergerUtility.setDestinationFileName(destinationFileName);
            for (File document : documents) {
                mergerUtility.addSource(document);
            }
            mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PDDocument mergePDFiles(PDDocument destination, PDDocument... documents) {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();

        try {
            for (PDDocument document : documents) {
                try  {
                    mergerUtility.appendDocument(destination, document);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destination;
    }

    public void fromPDFtoHTMLConverter() {
        try (Writer output = new PrintWriter("pdf.html", String.valueOf(StandardCharsets.UTF_8))) {
            new PDFDomTree().writeText(pdfDocument, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void managePDFPassword( String ownerPassword, String userPassword) {
        AccessPermission accessPermission = new AccessPermission();
        StandardProtectionPolicy sp = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
        sp.setPermissions(accessPermission);
        sp.setEncryptionKeyLength(128);
        try  {
            pdfDocument.protect(sp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PDPage> getPagesBySymbol(String symbol) {
        List<PDPage> resultPages = new ArrayList<>();
        int pageNumber = 1;

        try {
            PDFTextStripper reader = new PDFTextStripper();
            for (PDPage page : pdfDocument.getPages()) {
                reader.setStartPage(pageNumber);
                reader.setEndPage(pageNumber);
                String textFromPage = reader.getText(pdfDocument);
                if (textFromPage.contains(symbol)) {
                    resultPages.add(page);
                    System.out.println(pageNumber);
                }

                pageNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultPages;
    }
}