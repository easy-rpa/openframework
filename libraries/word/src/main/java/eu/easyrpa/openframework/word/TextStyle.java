package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.word.constants.FontFamily;
import eu.easyrpa.openframework.word.constants.TextAlignment;

import java.awt.*;

//TODO update javadoc

public class TextStyle {

    private TextRange relatedText;

    TextStyle(TextRange relatedText) {
        this.relatedText = relatedText;
    }

    /**
     * Sets font of cells.
     *
     * @param font the {@link FontFamily} of font to apply.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle font(FontFamily font) {
        //TODO implement this
        return this;
    }

    public FontFamily getFont() {
        //TODO implement this
        return null;
    }

    /**
     * Sets size of cells font in points.
     *
     * @param fontSize the size of font in points.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle fontSize(int fontSize) {
        //TODO implement this
        return this;
    }

    public Integer getFontSize() {
        //TODO implement this
        return null;
    }

    /**
     * Sets whether cells font should be bold.
     *
     * @param isBold {@code true} to set as bold and {@code false} otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle bold(boolean isBold) {
        //TODO implement this
        return this;
    }

    public boolean isBold() {
        //TODO implement this
        return false;
    }

    /**
     * Sets whether cells font should be italic.
     *
     * @param isItalic {@code true} to set as italic and {@code false} otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle italic(boolean isItalic) {
        //TODO implement this
        return this;
    }

    public boolean isItalic() {
        //TODO implement this
        return false;
    }

    /**
     * Sets whether cell font should be underlined.
     *
     * @param isUnderline {@code true} to set as underlined and {@code false} otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle underline(boolean isUnderline) {
        //TODO implement this
        return this;
    }

    public boolean isUnderline() {
        //TODO implement this
        return false;
    }

    /**
     * Sets color of cells font.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle color(Color color) {
        //TODO implement this
        return this;
    }

    public Color getColor() {
        //TODO implement this
        return null;
    }

    /**
     * Sets background color of cell.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextStyle background(Color color) {
        //TODO implement this
        return this;
    }

    public Color getBackground() {
        //TODO implement this
        return null;
    }

    /**
     * Sets horizontal alignment of text in the cell.
     *
     * @param align the alignment to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see TextAlignment
     */
    public TextStyle align(TextAlignment align) {
        //TODO implement this
        return this;
    }

    public TextAlignment getAlignment() {
        //TODO implement this
        return null;
    }
}
