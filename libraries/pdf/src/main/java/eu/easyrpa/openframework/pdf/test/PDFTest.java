package eu.easyrpa.openframework.pdf.test;

import eu.easyrpa.openframework.pdf.PDFDocCreator;
import eu.easyrpa.openframework.pdf.PDFPageCreator;
import eu.easyrpa.openframework.pdf.PDFText;
import eu.easyrpa.openframework.pdf.PDFUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PDFTest {

    public static void main(String[] args) throws IOException {
        //loading pdf files
        PDDocument document = PDDocument.load(new File("C:\\Users\\Miadzvedzeu_AA\\Downloads\\02.2022 Гомель_Парк.pdf"));
        // PDDocument document1 = PDDocument.load(new File("your_path"));
        //PDDocument document5 = PDDocument.load(new File("your_path"));
        PDFUtils pdfUtils = new PDFUtils(document);

//        PDDocument document6 = new PDDocument();
//        PDPage page = new PDPage(PDRectangle.A4);
//        document6.addPage(page);
//        PDPageContentStream contentStream = new PDPageContentStream(document6,page);
//        contentStream.beginText();
//        contentStream.setLeading(16.0f);
//        contentStream.setFont(PDType1Font.TIMES_ITALIC,14);
//        contentStream.setNonStrokingColor(Color.BLACK);
//        float ty = page.getTrimBox().getHeight()-20f;
//        contentStream.newLineAtOffset(14f,ty);
//
//        contentStream.showText("This is lkajfpjf fpoijpfjs poaiupoafjspf");
//        contentStream.endText();
//        contentStream.close();
//        document6.save("merged_in_one_page.pdf");
//        document6.close();
////
////        //reads all test from file
//        System.out.println(pdfUtils.readPDFDocument());
//
//        //saves pdf pages as images
//        pdfUtils.getPDFasImage();
//
//        //return amount of pages in pdf document
//        System.out.println(pdfUtils.getPDFPageCount() + " " + pdfUtils.getPDFPageCount());
//
//        //merges several pdf files in one
//        PDDocument document3 = pdfUtils.mergePDFiles(document, document1);
//        document3.save("bruh.pdf");
//        document3.close();
//
//        //return pages where the given symbol appears
//        System.out.println(pdfUtils.getPagesBySymbol("система"));
//
//        //Creating your own pdf file
//        String saveDestination = "";
//        PDDocument document2 = new PDDocument();
//        PDPage page = new PDPage(PDRectangle.A4);
//        document2.addPage(page);
//        PDPageContentStream contentStream = new PDPageContentStream(document2, page);
//
//        PdfCreator pageDrawer = new PdfCreator(document2, contentStream);
//        pageDrawer.addText("Hello world", 10, 10, PDType1Font.TIMES_ITALIC, 18f, Color.BLACK);
//
        Table myTable = Table.builder()
                .addColumnsOfWidth(200, 200)
                .padding(2)
                .addRow(Row.builder()
                        .add(TextCell.builder().text("One One").borderWidth(4).font(PDType1Font.TIMES_ITALIC).borderColorLeft(Color.MAGENTA).backgroundColor(Color.WHITE).build())
                        .add(TextCell.builder().text("One Two").borderWidth(0).backgroundColor(Color.YELLOW).build())
                        .build())
                .addRow(Row.builder()
                        .padding(10)
                        .add(TextCell.builder().text("Two One").textColor(Color.RED).build())
                        .add(TextCell.builder().text("Two Two")
                                .borderWidthRight(1f)
                                .borderStyleRight(BorderStyle.DOTTED)
                                .horizontalAlignment(HorizontalAlignment.RIGHT)
                                .build())
                        .build())
                .build();
//
//        pageDrawer.addTable(myTable, 20f, 20f);
//        pageDrawer.safeDocument(saveDestination);
//
//        //saving images from pdf
//       SaveImagesInPdf saveImagesInPdf = new SaveImagesInPdf();
//        saveImagesInPdf.getImagesFromPDF(document);
//
//        //Custom size of a page
//        PDDocument document4 = new PDDocument();
//        PDPage page3 = new PDPage(new PDRectangle(1700, 2000));
//        document4.addPage(page3);
//        document4.save("C:\\bruh49944.pdf");
//        document4.close();
//
//        pdfUtils.mergeAllPagesInDocument();
//
//        pdfUtils.getImageFromPdf();
//        System.out.println(pdfUtils.getValueInCoordinates(54.99998f, 38.622375f));
//        Rectangle area = new Rectangle();
//        pdfUtils.getValueFromArea(area, 0);

        PDFDocCreator doc = new PDFDocCreator();
        PDPage page = new PDPage(PDRectangle.A4);

        PDFText text1 = new PDFText();
        PDFText text2 = new PDFText();
        PDFText[] texts = new PDFText[] {text1, text2};
        PDFPageCreator pageCreator = doc.addPage(page);
        pageCreator.addText(text1);
        pageCreator.addText(text2);
        pageCreator.addMultilineText(texts);
        pageCreator.addImage("imagePath", 34, 56);
        pageCreator.addTable(myTable, 435,45);
        pageCreator.safePage();

        doc.safeDocument("bruh.pdf");

    }

}
