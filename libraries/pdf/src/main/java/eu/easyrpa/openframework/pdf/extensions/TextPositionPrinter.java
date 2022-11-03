package eu.easyrpa.openframework.pdf.extensions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;


public class GetCharLocationAndSize extends PDFTextStripper {

    public GetCharLocationAndSize() throws IOException {
    }


    public static void main( String[] args ) throws IOException {
        String fileName = "apache.pdf";
        try (PDDocument document = PDDocument.load(new File(fileName))) {
            PDFTextStripper stripper = new GetCharLocationAndSize();
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());

            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);
        }
    }

   
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            System.out.println(text.getUnicode()+ " [(X=" + text.getXDirAdj() + ",Y=" +
                    text.getYDirAdj() + ") height=" + text.getHeightDir() + " width=" +
                    text.getWidthDirAdj() + "]");
        }
    }
}
