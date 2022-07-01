package eu.easyrpa.openframework.word;

import org.docx4j.wml.R;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public class Picture{

    private R textRun;

    public Picture(String imagePath) {
        //TODO implement this
    }

    public Picture(Path imagePath) {
        //TODO implement this
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
}
