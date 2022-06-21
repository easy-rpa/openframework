package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.word.constants.MatchMethod;
import eu.easyrpa.openframework.word.constants.ParagraphStyles;
import org.docx4j.dml.Graphic;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.PropertyResolver;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.*;
import org.jvnet.jaxb2_commons.ppp.Child;

import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Represents specific paragraph of Word document and provides functionality to work with it.
 */
public class Paragraph implements Child {

    private P docxParagraph;

    public Paragraph() {
    }

    protected Paragraph(P p) {
        this.docxParagraph = p;
    }

    /**
     * Gets object of docx4j P (Paragraph representation by Jaxb).
     *
     * @return P-object.
     */
    public P getDocxParagraph() {
        return docxParagraph;
    }


    /**
     * Insert text value in paragraph. With adding it to the document.
     * After manipulation call save() or saveAs() in Word Document.
     * @param p the instance in which you want to insert the text.
     * @param text string value of text.
     */
    public void insertText(P p, String text) {
        if (text != null) {
            ObjectFactory factory = Context.getWmlObjectFactory();
            Text t = factory.createText();
            t.setValue(text);
            R run = factory.createR();
            run.getContent().add(t);
            p.getContent().add(run);
            this.docxParagraph = p;
        }
    }

    /**
     * Changes the style of text value in the object of class P, giving it a specific style (Title, Heading 1, etc.).
     * With adding it to the document.
     *
     * @param p the instance in which you want to insert the text.
     * @param resolver instance of PropertyResolver class to add specific JaxbElement to tree hierarchy of Docx4j Document Model.
     * @param style represented all available styles interpretation by ParagraphStyle's enum.
     */
    public void insertStyledText(P p, PropertyResolver resolver, ParagraphStyles style) {
        if (resolver.activateStyle(style.toString())) {
            ObjectFactory factory = Context.getWmlObjectFactory();
            PPr pPr = factory.createPPr();
            PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
            pPr.setPStyle(pStyle);
            pStyle.setVal(style.toString());
            p.setPPr(pPr);
            this.docxParagraph = p;
        }
    }


    /**
     * Replaces the specified text passed as a parameter.
     * @param p the instance where you want to replace text.
     * @param method the way how the given values will be matched with value of cells. If <code>matchMethod</code>
     *               is {@code null} the {@link MatchMethod#EXACT} is used as default.
     * @param toFind string representation of desired value.
     * @param replacer string representation of replaceable value.
     * @throws ClassCastException if {@code P} not contain R class value (it's means that it's blank or non-text format)
     *                            cast to {@code R}.
     * @see MatchMethod
     */
    public void replaceText(P p, MatchMethod method, String toFind, String replacer) {
        try {
            Text text = (Text) ((JAXBElement<?>) ((R) p.getContent().get(0)).getContent().get(0)).getValue();
            if (method.match(text.getValue(), toFind)) {
                text.setValue(replacer);
                this.docxParagraph = p;
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Text in paragraph is empty or damaged.", e);
        }
    }


    /**
     * Adds an image at the end of the document without picture settings.
     * @param wordPackage the instance of base WordprocessingMLPackage witch allow generating byte array picture
     *                    representation to picture value.
     * @param path the path to input picture file.
     */
    public void addImage(WordprocessingMLPackage wordPackage, Path path) throws Exception {
        byte[] bytes = convertImageToByteArray(path);
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordPackage, bytes);
        Inline inline = imagePart.createImageInline("Filename hint",
                "Alternative text", 1, 2, false);
        this.docxParagraph = addInlineImage(inline);
        wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().add(docxParagraph);
    }

    /**
     * Adds an image at the end of the document with picture settings.
     * @param wordPackage the instance of base WordprocessingMLPackage witch allow generating byte array picture
     *                    representation to picture value.
     * @param path the path to input picture file.
     * @param fileNameHint filename setting, which will be reflected in a separate field in Word document tree hierarchy.
     * @param altText alternative text setting with addition information in file description.
     *                Which will be reflected in a separate field in Word document tree hierarchy.
     * <p>These parameters are specific and are used to identify an image as an element when several identical images are added.</p>
     */
    public void addImage(WordprocessingMLPackage wordPackage, Path path, String fileNameHint, String altText) throws Exception {
        byte[] bytes = convertImageToByteArray(path);
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordPackage, bytes);
        Inline inline = imagePart.createImageInline(fileNameHint,
                altText, 1, 2, false);
        this.docxParagraph = addInlineImage(inline);
        wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().add(docxParagraph);
    }

    /**
     * Adds an image To a certain place where P element located without picture settings.
     * @param p the instance where you want to insert picture.
     * @param wordPackage the instance of base WordprocessingMLPackage witch allow generating byte array picture
     *                    representation to picture value.
     * @param path the path to input picture file.
     */
    public void addImage(P p, WordprocessingMLPackage wordPackage, Path path) throws Exception {
        byte[] bytes = convertImageToByteArray(path);
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordPackage, bytes);
        int docPrId = 1;
        int cNvPrId = 2;
        Inline inline = imagePart.createImageInline("Filename hint",
                "Alternative text", docPrId, cNvPrId, false);
        this.docxParagraph = addInlineImage(inline);
        int index = wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().indexOf(p);
        wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().set(index, docxParagraph);
    }

    /**
     * Get an image byte array presentation.
     * @param p the instance where you want to extract picture.
     * @param wordPackage the instance of base WordprocessingMLPackage represents the main package
     *                   for working with all jaxb elements in the form of a tree-like hierarchy
     */
    public byte[] getImage(P p, WordprocessingMLPackage wordPackage) {
        JAXBElement<?> jaxbElement = (JAXBElement<?>) ((R) p.getContent().get(0)).getContent().get(0);
        try {
            Drawing drawing = (Drawing) jaxbElement.getValue();
            ArrayListWml<?> drawingContent = (ArrayListWml<?>) drawing.getAnchorOrInline();
            Inline inline = (Inline) drawingContent.get(0);
            Graphic graphic = inline.getGraphic();
            return BinaryPartAbstractImage.getImage(wordPackage, graphic);
        } catch (ClassCastException e) {
            throw new RuntimeException("Provided parameter element doesn't contain image.", e);
        }
    }

    /**
     * Remove all text from p object with WordprocessingMLPackage. Delete the p-object from tree-hierarchy.
     * @param p the instance where you want to remove text.
     * @param wordPackage the instance of base WordprocessingMLPackage represents the main package
     *                   for working with all jaxb elements in the form of a tree-like hierarchy
     * @throws ClassCastException if you want to remove the text, but it does not exist in this object.
     * For example, it's contains Graphic, Table object, etc.
     */
    public void removeText(P p, WordprocessingMLPackage wordPackage) {
        if (!p.getContent().isEmpty()) {
            try {
                wordPackage.getMainDocumentPart().getJaxbElement().getBody().getContent().remove(p);
            } catch (ClassCastException e) {
                throw new RuntimeException("Cannot remove non-text values.", e);
            }
        }
    }

    /**
     * Helper method to convert inline object to jaxb paragraph element (P).
     * @param inline the instance of Graphic object that allows to convert picture to paragraph element and add it to hierarchy.
     */
    private P addInlineImage(Inline inline) {
        // Now add the in-line image to a paragraph
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }

    /**
     * Helper method to convert file to byte array.
     * @param path path to image file.
     * @throws RuntimeException if file too large or all bytes hasn't been read.
     * @throws IOException if specific file path incorrect or missing.
     */
    private byte[] convertImageToByteArray(Path path) throws IOException {
        byte[] bytes;
        try (InputStream is = new FileInputStream(path.toAbsolutePath().toString())) {
            long length = path.toFile().length();
            // You cannot create an array using a long, it needs to be an int.
            if (length > Integer.MAX_VALUE) {
                throw new RuntimeException("File too large.");
            }
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // Ensure all the bytes have been read
            if (offset < bytes.length) {
                throw new RuntimeException("Could not completely read file "
                        + path.toFile().getName());
            }
        }
        return bytes;
    }

    @Override
    public P getParent() {
        return docxParagraph;
    }

    @Override
    public void setParent(Object parent) {
        this.docxParagraph = (P) parent;
    }
//    //getP(
//    //getCHild()
//    // return P;
//    // )

    //        paragraph.insertStyledText((P) objectList.get(3),  //example to insert Styled Text
//                document.getPropertyResolver(),
//                ParagraphStyles.HEADING_1);

//        paragraph.insertText((P) objectList.get(0), "EXAMPLE"); //example to insert Text

    //        paragraph.replaceText((P) objectList.get(2), MatchMethod.EXACT, "Hello", "Welcome"); // example to replace text

}
