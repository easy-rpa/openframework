package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class PDFTest {

    private static PDFDoc pdfDoc;

    public static void main( String[] args) throws IOException {
        PDDocument document =  PDDocument.load(new File("C:\\Users\\Miadzvedzeu_AA\\Downloads\\2010_10_15.pdf"));
        PDDocument document1 = PDDocument.load(new File("C:\\Users\\Miadzvedzeu_AA\\Downloads\\bruh_file.pdf"));
        pdfDoc = new PDFDoc();


        //readPdfText(document1);
        //readPdfText(document);
        //getPDFasImage(document);
      //  System.out.println(pdfDoc.getPDFPageCount(document) + " " + pdfDoc.getPDFPageCount(document1));
//       PDDocument document3 =  pdfDoc.mergePDFiles(document,document1);
//        document3.save("bruh.pdf");
//        document3.close();

        System.out.println(pdfDoc.getPagesBySymbol("система", document1));

    }

    public static void readPdfText(PDDocument document)  {
        System.out.println(pdfDoc.readPDFDocument(document));
    }

    public static  void getPDFasImage(PDDocument document){
        pdfDoc.getPDFasImage(document);
    }
}
