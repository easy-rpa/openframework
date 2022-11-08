package eu.easyrpa.openframework.pdf.extensions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;


public class TextPositionPrinter extends PDFTextStripper {

    private final float xCoordinate;
    private final float yCoordinate;

    public TextPositionPrinter(float xCoordinate, float yCoordinate) throws IOException {
        super();
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public void getTextInPosition(PDDocument document) {
        try (document) {
            this.setSortByPosition(true);
            this.setStartPage(0);
            this.setEndPage(document.getNumberOfPages());

            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            this.writeText(document, dummy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {
        for (TextPosition text : textPositions) {
            if (this.xCoordinate == text.getX() && this.yCoordinate == text.getY()) {
                System.out.println(text.getUnicode() + " [(X=" + text.getX() + ",Y=" +
                        text.getY() + ") height=" + text.getHeightDir() + " width=" +
                        text.getWidthDirAdj() + "]");
            }

        }
    }
}
