package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.word.constants.Colors;
import eu.easyrpa.openframework.word.constants.FontFamily;
import eu.easyrpa.openframework.word.constants.TextAlignment;
import eu.easyrpa.openframework.word.util.Docx4jUtils;
import org.docx4j.wml.*;

import java.awt.Color;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.easyrpa.openframework.word.util.Docx4jUtils.*;

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
            for (R r : relatedText.getTextRuns()) {
                String asciiFont = r.getRPr().getRFonts().getAscii();
                if (!asciiFont.isEmpty()) {
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
    public TextFormat fontSize(String fontSize) {
        HpsMeasure size = new HpsMeasure();

        size.setVal(new BigInteger(fontSize));

        relatedText.getTextRuns().forEach(r -> {
            if(r.getRPr() == null) {
                RPr rPr = new RPr();
                r.setRPr(rPr);
            }
            r.getRPr().setSz(size);
            r.getRPr().setSzCs(size);
        });

        return this;
    }

    public Integer getFontSize() {
        R run = relatedText.getTextRuns().get(0);
        if(run.getRPr().getSzCs() != null) {
            return run.getRPr().getSzCs().getVal().intValue();
        } else return 0;
    }

    /**
     * Sets whether cells font should be bold.
     *
     * @param isBold {@code true} to set as bold and {@code false} otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextFormat bold(boolean isBold) {
        BooleanDefaultTrue b = new BooleanDefaultTrue();

        b.setVal(isBold);

        relatedText.getTextRuns().forEach(r -> {
            if(r.getRPr() == null) {
                RPr rPr = new RPr();
                r.setRPr(rPr);
            }
            r.getRPr().setB(b);
        });
        return this;
    }

    public boolean isBold() {
        R run = relatedText.getTextRuns().get(0);
        if(run.getRPr().getB() != null) {
            return run.getRPr().getB().isVal();
        } else return false;
    }

    /**
     * Sets whether cells font should be italic.
     *
     * @param isItalic {@code true} to set as italic and {@code false} otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextFormat italic(boolean isItalic) {
        BooleanDefaultTrue i = new BooleanDefaultTrue();

        i.setVal(isItalic);

        relatedText.getTextRuns().forEach(r -> {
            if(r.getRPr() == null) {
                RPr rPr = new RPr();
                r.setRPr(rPr);
            }
            r.getRPr().setI(i);
        });
        return this;
    }

    public boolean isItalic() {
        R run = relatedText.getTextRuns().get(0);
        if(run.getRPr().getI() != null) {
            return run.getRPr().getI().isVal();
        } else return false;
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
        R run = relatedText.getTextRuns().get(relatedText.getExpandIndex());

        return run.getRPr().getU() != null;
    }

    /**
     * Sets color of cells font.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public TextFormat color(Colors color) {
        P parent = (P) relatedText.getTextRuns().get(relatedText.getExpandIndex()).getParent();
        Text castedText = Docx4jUtils.getText(relatedText.getTextRuns(), relatedText.getExpandIndex());
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
     * @param hAlign the alignment to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see TextAlignment
     */
    public TextFormat align(JcEnumeration hAlign) {
        if(hAlign != null) {
            Jc align = new Jc();
            align.setVal(hAlign);
            if(relatedText.getTextRuns().get(0).getParent() instanceof P) {
                P parent = (P) relatedText.getTextRuns().get(0).getParent();
                parent.getPPr().setJc(align);
            }
        }
        return this;
    }

    public JcEnumeration getAlignment() {
        if(relatedText.getTextRuns().get(0).getParent() instanceof P) {
            P parent = (P) relatedText.getTextRuns().get(0).getParent();
            return parent.getPPr().getJc().getVal();
        }
        return null;
    }
}
