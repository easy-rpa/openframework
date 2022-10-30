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




        return null;
    }

    public void replaceWith(String text) {



    }

    public void replaceWith(Picture picture) {



    }

    public void remove() {



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


}
