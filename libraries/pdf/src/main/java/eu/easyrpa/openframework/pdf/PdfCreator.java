package eu.easyrpa.openframework.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Table;

import java.awt.*;
import java.io.IOException;


public class PdfCreator {

    private PDDocument document;
    private PDPageContentStream contentStream;

    public PdfCreator(){

    }

    public PdfCreator(PDDocument document, PDPageContentStream contentStream){
        this.document = document;
        this.contentStream = contentStream;
    }

    public void addText(String text, int x, int y, PDFont font, float fontSize, Color color){
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addMultilineText(String[] textArray, float leading ,int x, int y, PDFont font, float fontSize, Color color){
        try {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.setLeading(leading);
            contentStream.newLineAtOffset(x, y);

            for(String text: textArray){
                contentStream.showText(text);
                contentStream.newLine();
            }
            contentStream.endText();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addTable(Table table, float startX, float startY){
        TableDrawer tableDrawer = TableDrawer.builder()
                .contentStream(contentStream)
                .startX(startX)
                .startY(startY)
                .table(table)
                .build();

        tableDrawer.draw();
    }


//    public void setDocumentProperties(){
//        PDDocumentInformation documentInformation = document.getDocumentInformation();
//        documentInformation.setCreator();
//        documentInformation.setAuthor();
//        documentInformation.setCreationDate();
//        documentInformation.setProducer();
//        documentInformation.setSubject();
//        documentInformation.setKeywords();
//    }

    public void safeDocument(String saveDestination){
        try {
            contentStream.close();
            document.save(saveDestination);
            document.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("pdf created");
    }



}
