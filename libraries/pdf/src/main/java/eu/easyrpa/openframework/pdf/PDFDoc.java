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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PDFDoc {

    public String readPDFDocument(PDDocument pdfDocument) {
        String resultTextFromPDF = "";

        try (pdfDocument) {
            PDFTextStripper reader = new PDFTextStripper();
            reader.setSortByPosition(false);
            resultTextFromPDF = reader.getText(pdfDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultTextFromPDF;
    }

    public int getPDFPageCount(PDDocument pdfDocument) {
        return pdfDocument.getNumberOfPages();
    }

    public void getPDFasImage(PDDocument pdfDocument) {
        PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
        try (pdfDocument) {
            for (int page = 0; page < pdfDocument.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                ImageIOUtil.writeImage(bim, pdfDocument.getDocumentInformation().getSubject() + "-" + (page + 1) + ".jpeg", 300);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergePDFiles(String destinationFileName, File... documents) throws IOException {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        mergerUtility.setDestinationFileName(destinationFileName);
        for (File document : documents) {
            mergerUtility.addSource(document);
        }
        mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    }

    public PDDocument mergePDFiles(PDDocument destination, PDDocument... documents) {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();

        try {
            for (PDDocument document : documents) {
                try (document) {
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

    public void fromPDFtoHTMLConverter(PDDocument document) throws IOException {
        Writer output = new PrintWriter("pdf.html", String.valueOf(StandardCharsets.UTF_8));
        new PDFDomTree().writeText(document, output);

        output.close();
    }

    public void managePDFPassword(PDDocument document) throws IOException {
        AccessPermission accessPermission = new AccessPermission();
        StandardProtectionPolicy sp = new StandardProtectionPolicy("11111", "hello", accessPermission);
        sp.setPermissions(accessPermission);
        sp.setEncryptionKeyLength(128);
        document.protect(sp);
    }

    public List<PDPage> getPagesBySymbol(String symbol, PDDocument document) throws IOException {
        List<PDPage> resultPages = new ArrayList<>();
        PDFTextStripper reader = new PDFTextStripper();

        int pageNumber = 1;
        for (PDPage page : document.getPages()) {
            reader.setStartPage(pageNumber);
            reader.setEndPage(pageNumber);
            String textFromPage = reader.getText(document);
            if (textFromPage.contains(symbol)) {
                resultPages.add(page);
                System.out.println(pageNumber);
            }

            pageNumber++;
        }
        return resultPages;
    }


}