package eu.easyrpa.openframework.word;

public abstract class WordDocElement {

    /**
     * Reference to parent document part.
     */
    private WordDocument document;

    public WordDocElement(WordDocument document) {
        this.document = document;
    }

    public WordDocument getDocument() {
        return document;
    }
}
