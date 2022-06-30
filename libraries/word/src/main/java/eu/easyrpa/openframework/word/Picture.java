package eu.easyrpa.openframework.word;

import org.docx4j.wml.R;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public class Picture extends WordDocElement {

    private R textRun;

    public Picture(String imagePath) {
        super(null);
        //TODO implement this
    }

    public Picture(Path imagePath) {
        super(null);
        //TODO implement this
    }

    public Picture(File imageFile) {
        super(null);
        //TODO implement this
    }

    public Picture(InputStream imageIs) {
        super(null);
        //TODO implement this
    }

    Picture(WordDocument document, R textRun) {
        super(document);
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
