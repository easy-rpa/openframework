package eu.easyrpa.openframework.word;

import org.docx4j.wml.R;

import java.util.List;
import java.util.function.Function;

public class TextRange {

    private List<R> textRuns;

    private int startIndex;

    private int endIndex;

    TextRange(List<R> textRuns, int startIndex, int endIndex) {
        this.textRuns = textRuns;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public TextFormat format() {
        //TODO implement this
        return new TextFormat(this);
    }

    public String text() {
        //TODO Implement this.
        // Reading of all text of this range.
        return null;
    }

    public List<TextRange> words() {
        //TODO Implement this.
        // Split into words.
        return null;
    }

    public void words(Function<TextRange, Boolean> handler) {
        //TODO Implement this.
        // Split into words and iterate over them as text ranges. If handler returns false
        // the iterating is interrupted.
    }

    public TextRange subRange(String startRegexp, String endRegexp){
        //TODO Implement this.
        // startRegexp and endRegexp can be null
        // startRegexp - search first from start
        // endRegexp - search first from end
        return null;
    }

    public boolean expandLeft(){
        //TODO Implement this.
        // Expand current range to one word from the left/
        // return false of the start of paragraph
        return false;
    }

    public boolean expandRight(){
        //TODO Implement this.
        // Expand current range to one word from the right
        // return false of the end of paragraph
        return false;
    }

    public void addBefore(String text) {
        //TODO Implement this.
    }

    public void addBefore(Picture picture) {
        //TODO Implement this.
    }

    public void addAfter(String text) {
        //TODO Implement this.
    }

    public void addAfter(Picture picture) {
        //TODO Implement this.
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
