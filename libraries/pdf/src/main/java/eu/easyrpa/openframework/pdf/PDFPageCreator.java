package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Table;

import java.io.IOException;


public class PDFPageCreator {

    private PDDocument document;
    private PDPageContentStream contentStream;

    public PDFPageCreator() {

    }

    public PDFPageCreator(PDDocument document, PDPageContentStream contentStream) {
        this.document = document;
        this.contentStream = contentStream;
    }

    public void addText(PDFText text) {
        try {
            contentStream.beginText();
            contentStream.setFont(text.getFont(), text.getSize());
            contentStream.setNonStrokingColor(text.getColor());
            contentStream.newLineAtOffset(text.getXCoordinate(), text.getYCoordinate());
            contentStream.showText(text.getText());
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PDFPageCreator text(PDFText text){
        this.addText(text);
        return this;
    }

    public void addMultilineText(PDFText[] textArray) {
        try {
            contentStream.beginText();
            for (PDFText text : textArray) {
                contentStream.setFont(text.getFont(), text.getSize());
                contentStream.setNonStrokingColor(text.getColor());
                contentStream.setLeading(text.getLeading());
                contentStream.newLineAtOffset(text.getXCoordinate(), text.getYCoordinate());
                contentStream.showText(text.getText());
                contentStream.newLine();
            }
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PDFPageCreator multilineText(PDFText[] textArray){
        this.addMultilineText(textArray);
        return this;
    }

    public void addTable(Table table, float startX, float startY) {
        TableDrawer tableDrawer = TableDrawer.builder()
                .contentStream(contentStream)
                .startX(startX)
                .startY(startY)
                .table(table)
                .build();

        tableDrawer.draw();
    }

    public PDFPageCreator table(Table table, float startX, float startY){
        this.addTable(table, startX, startY);
        return this;
    }

    public void addImage(String imagePath, int x, int y) {
        try {
            PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);
            contentStream.drawImage(image, x, y);
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PDFPageCreator image(String imagePath, int x, int y){
        this.addImage(imagePath, x, y);
        return this;
    }

    public void safePage() {
        try {
            contentStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
