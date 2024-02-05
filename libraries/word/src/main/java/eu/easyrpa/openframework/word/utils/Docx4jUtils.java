package eu.easyrpa.openframework.word.utils;

import eu.easyrpa.openframework.word.constants.Colors;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Docx4jUtils {

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

    public static Text getText(List<R> textRuns, int index) {
        try {
            return (Text) ((JAXBElement<?>) ((ArrayListWml<?>) textRuns.get(index).getContent()).get(nestedIndexValidation(textRuns.get(index)))).getValue();
        } catch (IndexOutOfBoundsException | ClassCastException exc) {
            return null;
        }        
    }

    private static int nestedIndexValidation(R run) {
        int nestedWmlContentIndex = 0;
        for (int i = 0; i < run.getContent().size(); i++) {
            try {
                Text text = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) run.getContent()).get(nestedWmlContentIndex)).getValue();
                if(!text.getValue().isEmpty())
                    return nestedWmlContentIndex;
            } catch (ClassCastException e) {
                nestedWmlContentIndex++;
            }
        }
        return nestedWmlContentIndex;
    }

    public static R createRun(String text, P parentP) {
        ObjectFactory factory = new ObjectFactory();
        R run = factory.createR();
        parentP.getContent().add(run);
        Text textEl = factory.createText();
        textEl.setValue(text);
        run.getContent().add(textEl);
        return run;
    }

    public static void setWmlRColor(R coloredRun, Colors color) {
        //TODO
    }

    public static R createWhitespaceRun(P parent) {
        ObjectFactory factory = new ObjectFactory();
        R run = factory.createR();
        parent.getContent().add(run);
        Text textEl = factory.createText();
        textEl.setValue(" ");
        run.getContent().add(textEl);
        return run;
    }
}
