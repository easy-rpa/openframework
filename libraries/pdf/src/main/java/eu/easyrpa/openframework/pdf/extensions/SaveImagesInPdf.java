package eu.easyrpa.openframework.pdf.extensions;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SaveImagesInPdf extends PDFStreamEngine {

    public SaveImagesInPdf()  {
    }

    public int imageNumber = 1;

    public void getImagesFromPDF(PDDocument document){
        try (document) {
            SaveImagesInPdf printer = new SaveImagesInPdf();
            int pageNum = 1;
            for (PDPage page : document.getPages()) {
                System.out.println("Processing page: " + pageNum);
                printer.processPage(page);
                pageNum++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        if ("Do".equals(operation)) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xObject = getResources().getXObject(objectName);
            if (xObject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xObject;


                BufferedImage bImage = image.getImage();
                ImageIO.write(bImage, "PNG", new File("image_" + imageNumber + ".png"));
                System.out.println("Image saved.");
                imageNumber++;

            } else if (xObject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xObject;
                showForm(form);
            }
        } else {
            super.processOperator(operator, operands);
        }
    }

}