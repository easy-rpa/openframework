package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.core.utils.FilePathUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class Picture{

    private R textRun;

    private byte[] binaryImage;

    private File picFile;

    //working with Picture only with File instance (another throw exception (Path, String)
    public Picture(String imagePath) {
        if (imagePath == null) {
            throw new IllegalArgumentException("Image path cannot be null.");
        }
        File file = FilePathUtils.getFile(imagePath);
        setPicFile(file);
    }

    public Picture(Path imagePath) {
        //todo
    }

    public Picture(File imageFile) {
        //TODO implement this
    }

    public Picture(InputStream imageIs) {
        //TODO implement this
    }

    Picture(R textRun) {
        this.textRun = textRun;
    }

    public String getAltText(){
        //TODO implement this
        return null;
    }

    public void replaceWith(String text) {
        //TODO Implement this.
    }

    public void replaceWith(Picture picture) {
        //TODO Implement this.
    }

    public void remove() {
        //TODO Implement this.
    }

    public byte[] getBinaryImage() {
        return binaryImage;
    }

    public void setBinaryImage(byte[] binaryImage) {
        this.binaryImage = binaryImage;
    }

    public File getPicFile() {
        return picFile;
    }

    public void setPicFile(File picFile) {
        this.picFile = picFile;
    }

    /**
     * We create an object factory and use it to create a paragraph and a run.
     * Then we add the run to the paragraph. Next we create a drawing and
     * add it to the run. Finally we add the inline object to the drawing and
     * return the paragraph.
     *
     * @param inline the instance of Graphic object that allows to convert picture to paragraph element and add it to hierarchy.
     */
    public static P addInlineImage(Inline inline) {
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
     *
     * @param file image file.
     * @throws RuntimeException if file too large or all bytes hasn't been read.
     * @throws IOException      if specific file path incorrect or missing.
     */
    public static byte[] convertFileToByteArray(File file) throws IOException {
        byte[] bytes;
        try (InputStream is = new FileInputStream(file.getAbsolutePath())) {
            long length = file.length();
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
                        + file.getName());
            }
        }
        return bytes;
    }

}
