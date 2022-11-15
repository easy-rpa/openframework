package eu.easyrpa.openframework.pdf;

import eu.easyrpa.openframework.pdf.extensions.TextInCoordinates;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.fit.pdfdom.PDFDomTree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PDFDoc {

    private final PDDocument pdfDocument;

    public PDFDoc(PDDocument document) {
        this.pdfDocument = document;
    }

    public String readPDFDocument() {
        String resultTextFromPDF = "";

        try {
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
        try {
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
                try {
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
        try (Writer output = new PrintWriter(this.pdfDocument.getDocumentInformation().getTitle() + ".html", String.valueOf(StandardCharsets.UTF_8))) {
            new PDFDomTree().writeText(pdfDocument, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    //TODO: think how to copy content from one document to another
    public void mergeAllPagesInDocument() throws IOException {
        float height = pdfDocument.getPage(0).getMediaBox().getHeight();
        float width = pdfDocument.getPage(0).getMediaBox().getWidth();
        PDRectangle rectangle = new PDRectangle(width, height * pdfDocument.getNumberOfPages());


//        PDPage page2 = pdfDocument.getPage(0);
//        COSDictionary dict = page2.getCOSObject();
//        COSDictionary dict2 = new COSDictionary(dict);
//
//        dict2.removeItem(COSName.ANNOTS);

//        PDPage page = new PDPage(dict2);
        // PDStream stream = new PDStream(pdfDocument, page2.getContents());
        PDPage page = new PDPage(rectangle);

        PDDocument document = new PDDocument();
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.newLineAtOffset(0, 0);
        contentStream.showText("TEDTOP");
        contentStream.endText();
        document.addPage(page);

        document.save("merged_in_one_page.pdf");

    }

    public String getValueInCoordinates(float x, float y) {

        try {
            TextInCoordinates printer = new TextInCoordinates(x, y);
            printer.getTextInPosition(pdfDocument);
            return printer.getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getImageFromPdf() {
        int imagesCount = 1;
        for (PDPage page : pdfDocument.getPages()) {
            System.out.println("process page " + imagesCount);
            PDResources pdResources = page.getResources();
            for (COSName cosName : pdResources.getXObjectNames()) {
                try {
                    PDXObject o = pdResources.getXObject(cosName);
                    if (o instanceof PDImageXObject) {
                        ImageIO.write(((PDImageXObject) o).getImage(), "JPEG", new File("image_" + imagesCount + ".jpeg"));
                        imagesCount++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getValueFromArea(Rectangle area, int pageNumber) {
        try {
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            PDPage page = pdfDocument.getPage(pageNumber);
            stripper.addRegion("cs", area);
            stripper.extractRegions(page);
            System.out.println(stripper.getTextForRegion("cs"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}