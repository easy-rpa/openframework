package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.word.constants.Colors;
import eu.easyrpa.openframework.word.constants.FontFamily;
import eu.easyrpa.openframework.word.constants.TextAlignment;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO update javadoc

public class TextFormat {

    private TextRange relatedText;


    TextFormat(TextRange relatedText) {
        this.relatedText = relatedText;
    }

    /**
     * Sets font of cells.
     *
     * @param font the {@link FontFamily} of font to apply.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextFormat font(FontFamily font) {
//        PhysicalFont physicalFont = PhysicalFonts.get(font.getName());

        ObjectFactory factory = new ObjectFactory();
        RFonts fonts = factory.createRFonts();
        fonts.setAscii(font.getName());

        fonts.setCs(font.getName());
        fonts.setHAnsi(font.getName());

        relatedText.getTextRuns().forEach(r -> r.getRPr().setRFonts(fonts));

        return this;
    }

    public FontFamily getFont() {
        try {
            for(R r : relatedText.getTextRuns()) {
                String asciiFont = r.getRPr().getRFonts().getAscii();
                if(!asciiFont.isEmpty()) {
                    return FontFamily.getValue(asciiFont);
                }
            }
        } catch (NullPointerException e) {
            return FontFamily.UNSPECIFIED;
        }
        return null;
    }

    /**
     * Sets size of cells font in points.
     *
     * @param fontSize the size of font in points.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextFormat fontSize(int fontSize) {
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
    public TextFormat bold(boolean isBold) {
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
    public TextFormat italic(boolean isItalic) {
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
    public TextFormat underline(boolean isUnderline) {
        ObjectFactory factory = new ObjectFactory();

        U u = factory.createU();

        //This line here will do the trick for you.
        u.setVal(UnderlineEnumeration.SINGLE);

        relatedText.getTextRuns().forEach(r -> r.getRPr().setU(u));

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
    public TextFormat color(Colors color) {
        P parent = (P) relatedText.getTextRuns().get(relatedText.getExpandIndex()).getParent();
        Text castedText = relatedText.textCast(relatedText.getTextRuns(), relatedText.getExpandIndex());
        String textValue = castedText.getValue();
        Pattern pattern = Pattern.compile(TextRange.EXPAND_RIGHT);
        if (relatedText.getStartIndex() == 0) {
            Matcher matcher = pattern.matcher(textValue);
            if (matcher.find()) {
                String coloredText = textValue.substring(0, matcher.start() - 1);
                R coloredRun = createRun(coloredText, parent);
                setWmlRColor(coloredRun, color);
                String endTextRun = textValue.substring(matcher.start());
                R endRun = createRun(endTextRun, parent);
                R whitespaceRun = createWhitespaceRun(parent);
                int indexOfColorRun = parent.getContent().indexOf(relatedText.getTextRuns().get(relatedText.getExpandIndex()));
                parent.getContent().set(indexOfColorRun, coloredRun);
                parent.getContent().add(indexOfColorRun + 1, whitespaceRun);
                parent.getContent().add(indexOfColorRun + 2, endRun);
                relatedText.getTextRuns().set(relatedText.getExpandIndex(), coloredRun);
                relatedText.getTextRuns().add(relatedText.getExpandIndex() + 1, whitespaceRun);
                relatedText.getTextRuns().add(relatedText.getExpandIndex() + 2, endRun);
                relatedText.setExpandIndex(relatedText.getExpandIndex() + 1);
                return this;
            }
        }
        String startRunText = textValue.substring(0, relatedText.getStartIndex() - 1); //first run (-1 for whitespace)
        R startRun = createRun(startRunText, parent);
        String mainRunText = textValue.substring(relatedText.getStartIndex());
        Matcher matcher = pattern.matcher(mainRunText);
        if (matcher.find()) {
            String coloredText = mainRunText.substring(0, matcher.start() - 1); // middle run
            R coloredRun = createRun(coloredText, parent);
            setWmlRColor(coloredRun, color);
            String endTextRun = mainRunText.substring(matcher.start());  // last run
            R endRun = createRun(endTextRun, parent);
            R whitespaceRun = createWhitespaceRun(parent);
            int indexOfColorRun = parent.getContent().indexOf(relatedText.getTextRuns().get(relatedText.getExpandIndex()));
            parent.getContent().set(indexOfColorRun, startRun);
            parent.getContent().add(indexOfColorRun + 1, whitespaceRun);
            parent.getContent().add(indexOfColorRun + 2, coloredRun);
            parent.getContent().add(indexOfColorRun + 3, whitespaceRun);
            parent.getContent().add(indexOfColorRun + 4, endRun);
            relatedText.getTextRuns().set(relatedText.getExpandIndex(), startRun);
            relatedText.getTextRuns().add(relatedText.getExpandIndex() + 1, whitespaceRun);
            relatedText.getTextRuns().add(relatedText.getExpandIndex() + 2, coloredRun);
            relatedText.getTextRuns().add(relatedText.getExpandIndex() + 3, whitespaceRun);
            relatedText.getTextRuns().add(relatedText.getExpandIndex() + 4, endRun);
            relatedText.setExpandIndex(relatedText.getExpandIndex() + 2);
        }
        return this;
    }

    private void addContent(List<Object> listContent, Object toAdd, int startIndex, int endValue) {
        for (int i = startIndex; i < startIndex + endValue; i++) {
            listContent.add(startIndex, toAdd);
        }
    }

    private R createRun(String text, P parent) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        QName qName = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "t", "");
        Text t = factory.createText();
        t.setValue(text);
        R run = factory.createR();
        run.setParent(parent);
        JAXBElement<?> element = new JAXBElement(qName, Text.class, R.class, t);
        run.getContent().add(element);
        return run;
    }

    private R createWhitespaceRun(P parent) {
        ObjectFactory factory = Context.getWmlObjectFactory();
        QName qName = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "t", "");
        Text text = factory.createText();
        text.setValue(" ");
        text.setSpace("preserve");
        R run = factory.createR();
        run.setParent(parent);
        JAXBElement<?> element = new JAXBElement(qName, Text.class, R.class, text);
        run.getContent().add(element);
        return run;
    }

    private void setWmlRColor(R run, Colors color) {
        RPr colorRunRPr = run.getRPr();
        if (colorRunRPr == null) {
            colorRunRPr = new RPr();
            run.setRPr(colorRunRPr);
        }
        org.docx4j.wml.Color wmlColor = Context.getWmlObjectFactory().createColor();
        wmlColor.setVal(color.name());
        colorRunRPr.setColor(wmlColor);
        run.setRPr(colorRunRPr);
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
    public TextFormat background(Color color) {
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
    public TextFormat align(TextAlignment align) {
        //TODO implement this
        return this;
    }

    public TextAlignment getAlignment() {
        //TODO implement this
        return null;
    }
}
