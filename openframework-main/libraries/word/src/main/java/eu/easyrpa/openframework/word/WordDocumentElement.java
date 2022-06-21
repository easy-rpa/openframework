package eu.easyrpa.openframework.word;

import org.jvnet.jaxb2_commons.ppp.Child;

public class WordDocumentElement {

    /**
     * Reference to parent document part.
     */

    private WordDocument document;

    private Child child;

    public WordDocumentElement() {
    }

    protected WordDocumentElement(Child child) {
        this.child = child;
    }

    public Object getElement() {
        return child;
    }

    // TODO: 6/9/2022
    public Child getElement(Class<? extends Child> toSearch) {
        return child.getClass().cast(toSearch);
    } 

    public WordDocument getDocument() {
        return document;
    }

    public void setDocument(WordDocument document) {
        this.document = document;
    }

    public Child getChild() {
        return child;
    }

    //    public Body getContent() {
//       return this.wordprocessingMLPackage.getMainDocumentPart().getJaxbElement().getBody();
//    }

    //getEl(
    // return child)
    //getEl(Class<? extend Child>)

}
