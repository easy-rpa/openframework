package eu.ibagroup.easyrpa.openframework.google.sheets;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.google.sheets.constants.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps cell style parameters and allows to easily apply them to different cells even from different Spreadsheet
 * documents.
 */
public class CellStyle {

    /**
     * Google cell format related this style.
     */
    private CellFormat cellFormat;

    /**
     * Recent cell that has this style.
     */
    private Cell cell;

    /**
     * Contains names of Google cell format fields that have been changed after creation or recent applying
     * to some cell.
     */
    private List<String> dirtyFields = new ArrayList<>();

    /**
     * Creates cell style object with default parameters.
     */
    public CellStyle() {
        cellFormat = new CellFormat();
    }

    /**
     * Creates cell style object with style parameters of given cell.
     *
     * @param cell object representing source cell.
     */
    public CellStyle(Cell cell) {
        CellFormat cellFormat = cell.getGCell().getUserEnteredFormat();
        this.cellFormat = cellFormat != null ? cellFormat.clone() : new CellFormat();
        this.cell = cell;
    }

    /**
     * Sets font of cells.
     *
     * @param font the {@link FontFamily} of font to apply.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle font(FontFamily font) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setFontFamily(font.getName());
        this.dirtyFields.add("userEnteredFormat.textFormat.fontFamily");
        return this;
    }

    public FontFamily getFont() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null && textFormat.getFontFamily() != null) {
            return FontFamily.getValue(textFormat.getFontFamily());
        }
        return null;
    }

    /**
     * Sets size of cells font in points.
     *
     * @param fontSize the size of font in points.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle fontSize(int fontSize) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setFontSize(fontSize);
        this.dirtyFields.add("userEnteredFormat.textFormat.fontSize");
        return this;
    }

    public Integer getFontSize() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null) {
            return textFormat.getFontSize();
        }
        return null;
    }

    /**
     * Sets whether cells font should be bold.
     *
     * @param isBold <code>true</code> to set as bold and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle bold(boolean isBold) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setBold(isBold);
        this.dirtyFields.add("userEnteredFormat.textFormat.bold");
        return this;
    }

    public boolean isBold() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null && textFormat.getBold() != null) {
            return textFormat.getBold();
        }
        return false;
    }

    /**
     * Sets whether cells font should be italic.
     *
     * @param isItalic <code>true</code> to set as italic and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle italic(boolean isItalic) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setItalic(isItalic);
        this.dirtyFields.add("userEnteredFormat.textFormat.italic");
        return this;
    }

    public boolean isItalic() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null && textFormat.getItalic() != null) {
            return textFormat.getItalic();
        }
        return false;
    }

    /**
     * Sets whether cells font should be strikeout.
     *
     * @param isStrikeout <code>true</code> to set as strikeout and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle strikeout(boolean isStrikeout) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setStrikethrough(isStrikeout);
        this.dirtyFields.add("userEnteredFormat.textFormat.strikethrough");
        return this;
    }

    public boolean isStrikeout() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null && textFormat.getStrikethrough() != null) {
            return textFormat.getStrikethrough();
        }
        return false;
    }

    /**
     * Sets whether cell font should be underlined.
     *
     * @param isUnderline <code>true</code> to set as underlined and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle underline(boolean isUnderline) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setUnderline(isUnderline);
        this.dirtyFields.add("userEnteredFormat.textFormat.underline");
        return this;
    }

    public boolean isUnderline() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null && textFormat.getUnderline() != null) {
            return textFormat.getUnderline();
        }
        return false;
    }

    /**
     * Sets color of cells font.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle color(Color color) {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat == null) {
            textFormat = new TextFormat();
            cellFormat.setTextFormat(textFormat);
        }
        textFormat.setForegroundColor(toGoogleColor(color));
        this.dirtyFields.add("userEnteredFormat.textFormat.foregroundColor");
        return this;
    }

    public Color getColor() {
        TextFormat textFormat = cellFormat.getTextFormat();
        if (textFormat != null && textFormat.getForegroundColor() != null) {
            com.google.api.services.sheets.v4.model.Color color = textFormat.getForegroundColor();
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
        return null;
    }

    /**
     * Sets cells number format.
     *
     * @param format the data format to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see NumberFormats
     */
    public CellStyle format(NumberFormats format) {
        NumberFormat numberFormat = cellFormat.getNumberFormat();
        if (numberFormat == null) {
            numberFormat = new NumberFormat();
            cellFormat.setNumberFormat(numberFormat);
        }
        numberFormat.setType(format.name());
        this.dirtyFields.add("userEnteredFormat.numberFormat.type");
        return this;
    }

    public NumberFormats getFormatType() {
        NumberFormat numberFormat = cellFormat.getNumberFormat();
        if (numberFormat != null && numberFormat.getType() != null) {
            return NumberFormats.valueOf(numberFormat.getType());
        }
        return null;
    }

    /**
     * Sets cells custom number format using pattern.
     * <p>
     * See the <a href="https://developers.google.cn/sheets/api/guides/formats?hl=id">Date and Number Formats guide</a>
     * for more information about the supported patterns.
     *
     * @param pattern the string with necessary format to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle format(String pattern) {
        NumberFormat numberFormat = cellFormat.getNumberFormat();
        if (numberFormat == null) {
            numberFormat = new NumberFormat();
            cellFormat.setNumberFormat(numberFormat);
        }
        numberFormat.setPattern(pattern);
        this.dirtyFields.add("userEnteredFormat.numberFormat.pattern");
        return this;
    }

    public String getFormatPattern() {
        NumberFormat numberFormat = cellFormat.getNumberFormat();
        if (numberFormat != null) {
            return numberFormat.getPattern();
        }
        return null;
    }

    /**
     * Sets background color of cell.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle background(Color color) {
        cellFormat.setBackgroundColor(toGoogleColor(color));
        this.dirtyFields.add("userEnteredFormat.backgroundColor");
        return this;
    }

    public Color getBackground() {
        com.google.api.services.sheets.v4.model.Color color = cellFormat.getBackgroundColor();
        if (color != null) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
        return null;
    }

    /**
     * Sets horizontal alignment of text in the cell.
     *
     * @param hAlign the alignment to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see HorizontalAlignment
     */
    public CellStyle hAlign(HorizontalAlignment hAlign) {
        cellFormat.setHorizontalAlignment(hAlign.name());
        this.dirtyFields.add("userEnteredFormat.horizontalAlignment");
        return this;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        if (cellFormat.getHorizontalAlignment() != null) {
            return HorizontalAlignment.valueOf(cellFormat.getHorizontalAlignment());
        }
        return null;
    }

    /**
     * Sets vertical alignment of text in the cell.
     *
     * @param vAlign the alignment to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see VerticalAlignment
     */
    public CellStyle vAlign(VerticalAlignment vAlign) {
        cellFormat.setVerticalAlignment(vAlign.name());
        this.dirtyFields.add("userEnteredFormat.verticalAlignment");
        return this;
    }

    public VerticalAlignment getVerticalAlignment() {
        if (cellFormat.getVerticalAlignment() != null) {
            return VerticalAlignment.valueOf(cellFormat.getVerticalAlignment());
        }
        return null;
    }

    /**
     * Sets how text in the cell should wrap.
     *
     * @param strategy necessary {@link WrapStrategy} to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle wrapStrategy(WrapStrategy strategy) {
        cellFormat.setWrapStrategy(strategy.name());
        this.dirtyFields.add("userEnteredFormat.wrapStrategy");
        return this;
    }

    public WrapStrategy getWrapStrategy() {
        if (cellFormat.getWrapStrategy() != null) {
            return WrapStrategy.valueOf(cellFormat.getWrapStrategy());
        }
        return null;
    }

    /**
     * Sets degree of rotation for the text in the cell.
     *
     * @param rotation degree of rotation to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle rotation(int rotation) {
        TextRotation textRotation = cellFormat.getTextRotation();
        if (textRotation == null) {
            textRotation = new TextRotation();
            cellFormat.setTextRotation(textRotation);
        }
        textRotation.setAngle(rotation);
        this.dirtyFields.add("userEnteredFormat.textRotation.angle");
        return this;
    }

    public Integer getRotation() {
        TextRotation textRotation = cellFormat.getTextRotation();
        if (textRotation != null) {
            return textRotation.getAngle();
        }
        return null;
    }

    /**
     * Sets whether the text in the cell should be displayed vertically.
     *
     * @param isVertical <code>true</code> to display text vertically and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle vertical(boolean isVertical) {
        TextRotation textRotation = cellFormat.getTextRotation();
        if (textRotation == null) {
            textRotation = new TextRotation();
            cellFormat.setTextRotation(textRotation);
        }
        textRotation.setVertical(isVertical);
        this.dirtyFields.add("userEnteredFormat.textRotation.vertical");
        return this;
    }

    public boolean isVertical() {
        TextRotation textRotation = cellFormat.getTextRotation();
        if (textRotation != null && textRotation.getVertical() != null) {
            return textRotation.getVertical();
        }
        return false;
    }

    /**
     * Sets styles of all cell borders (top, right, bottom and left).
     * <p>
     * It accepts single value (one style for all borders of the cell) or 4 values where each value
     * correspond to specific border in the following order:
     * <pre>
     *     {@code (<top>, <right>, <bottom>, <left>)}
     * </pre>
     *
     * @param styles styles of borders to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public CellStyle borders(BorderStyle... styles) {
        BorderStyle[] borderStyles = styles.length == 4
                ? styles : new BorderStyle[]{styles[0], styles[0], styles[0], styles[0]};

        Borders borders = cellFormat.getBorders();
        if (borders == null) {
            borders = new Borders();
            cellFormat.setBorders(borders);
        }

        if (borderStyles[0] != null) {
            Border topBorder = borders.getTop();
            if (topBorder == null) {
                topBorder = new Border();
                borders.setTop(topBorder);
            }
            topBorder.setStyle(borderStyles[0].name());
            this.dirtyFields.add("userEnteredFormat.borders.top.style");
        }

        if (borderStyles[1] != null) {
            Border rightBorder = borders.getRight();
            if (rightBorder == null) {
                rightBorder = new Border();
                borders.setRight(rightBorder);
            }
            rightBorder.setStyle(borderStyles[1].name());
            this.dirtyFields.add("userEnteredFormat.borders.right.style");
        }

        if (borderStyles[2] != null) {
            Border bottomBorder = borders.getBottom();
            if (bottomBorder == null) {
                bottomBorder = new Border();
                borders.setBottom(bottomBorder);
            }
            bottomBorder.setStyle(borderStyles[2].name());
            this.dirtyFields.add("userEnteredFormat.borders.bottom.style");
        }

        if (borderStyles[3] != null) {
            Border leftBorder = borders.getLeft();
            if (leftBorder == null) {
                leftBorder = new Border();
                borders.setLeft(leftBorder);
            }
            leftBorder.setStyle(borderStyles[3].name());
            this.dirtyFields.add("userEnteredFormat.borders.left.style");
        }

        return this;
    }

    public BorderStyle[] getBorders() {
        BorderStyle[] borderStyles = new BorderStyle[4];
        Borders borders = cellFormat.getBorders();
        if (borders != null) {
            if (borders.getTop() != null && borders.getTop().getStyle() != null) {
                borderStyles[0] = BorderStyle.valueOf(borders.getTop().getStyle());
            }
            if (borders.getRight() != null && borders.getRight().getStyle() != null) {
                borderStyles[1] = BorderStyle.valueOf(borders.getRight().getStyle());
            }
            if (borders.getBottom() != null && borders.getBottom().getStyle() != null) {
                borderStyles[2] = BorderStyle.valueOf(borders.getBottom().getStyle());
            }
            if (borders.getLeft() != null && borders.getLeft().getStyle() != null) {
                borderStyles[3] = BorderStyle.valueOf(borders.getLeft().getStyle());
            }
        }
        return borderStyles;
    }

    /**
     * Sets colors for all cell borders  (top, right, bottom and left).
     * <p>
     * It accepts single value (one color for all borders of the cell) or 4 values where each value
     * correspond to specific border in the following order:
     * <pre>
     *     {@code (<top>, <right>, <bottom>, <left>)}
     * </pre>
     *
     * @param colors colors to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle borderColors(Color... colors) {
        Color[] borderColors = colors.length == 4
                ? colors : new Color[]{colors[0], colors[0], colors[0], colors[0]};

        Borders borders = cellFormat.getBorders();
        if (borders == null) {
            borders = new Borders();
            cellFormat.setBorders(borders);
        }

        if (borderColors[0] != null) {
            Border topBorder = borders.getTop();
            if (topBorder == null) {
                topBorder = new Border();
                borders.setTop(topBorder);
            }
            topBorder.setColor(toGoogleColor(borderColors[0]));
            this.dirtyFields.add("userEnteredFormat.borders.top.color");
        }

        if (borderColors[1] != null) {
            Border rightBorder = borders.getRight();
            if (rightBorder == null) {
                rightBorder = new Border();
                borders.setRight(rightBorder);
            }
            rightBorder.setColor(toGoogleColor(borderColors[1]));
            this.dirtyFields.add("userEnteredFormat.borders.right.color");
        }

        if (borderColors[2] != null) {
            Border bottomBorder = borders.getBottom();
            if (bottomBorder == null) {
                bottomBorder = new Border();
                borders.setBottom(bottomBorder);
            }
            bottomBorder.setColor(toGoogleColor(borderColors[2]));
            this.dirtyFields.add("userEnteredFormat.borders.bottom.color");
        }

        if (borderColors[3] != null) {
            Border leftBorder = borders.getLeft();
            if (leftBorder == null) {
                leftBorder = new Border();
                borders.setLeft(leftBorder);
            }
            leftBorder.setColor(toGoogleColor(borderColors[3]));
            this.dirtyFields.add("userEnteredFormat.borders.left.color");
        }

        return this;
    }

    public Color[] getBorderColors() {
        Color[] borderColors = new Color[4];
        Borders borders = cellFormat.getBorders();
        if (borders != null) {
            if (borders.getTop() != null && borders.getTop().getColor() != null) {
                com.google.api.services.sheets.v4.model.Color c = borders.getTop().getColor();
                borderColors[0] = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            }
            if (borders.getRight() != null && borders.getRight().getColor() != null) {
                com.google.api.services.sheets.v4.model.Color c = borders.getRight().getColor();
                borderColors[1] = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            }
            if (borders.getBottom() != null && borders.getBottom().getColor() != null) {
                com.google.api.services.sheets.v4.model.Color c = borders.getBottom().getColor();
                borderColors[2] = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            }
            if (borders.getLeft() != null && borders.getLeft().getColor() != null) {
                com.google.api.services.sheets.v4.model.Color c = borders.getLeft().getColor();
                borderColors[3] = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
            }
        }
        return borderColors;
    }

    /**
     * Sets amounts of padding around the cell, in pixels.
     * <p>
     * It accepts single value (one amount for all sides of the cell) or 4 values where each value
     * correspond to amount of padding for specific side in the following order:
     * <pre>
     *     {@code (<top>, <right>, <bottom>, <left>)}
     * </pre>
     *
     * @param padding padding to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public CellStyle padding(int... padding) {
        int[] paddings = padding.length == 4
                ? padding : new int[]{padding[0], padding[0], padding[0], padding[0]};

        Padding cellPadding = cellFormat.getPadding();
        if (cellPadding == null) {
            cellPadding = new Padding();
            cellFormat.setPadding(cellPadding);
        }
        cellPadding.setTop(paddings[0]);
        cellPadding.setRight(paddings[1]);
        cellPadding.setBottom(paddings[2]);
        cellPadding.setLeft(paddings[3]);
        this.dirtyFields.add("userEnteredFormat.padding");
        return this;
    }

    public Integer[] getPadding() {
        Integer[] padding = new Integer[4];
        Padding cellPadding = cellFormat.getPadding();
        if (cellPadding != null) {
            padding[0] = cellPadding.getTop();
            padding[1] = cellPadding.getRight();
            padding[2] = cellPadding.getBottom();
            padding[3] = cellPadding.getLeft();
        }
        return padding;
    }

    public CellFormat getCellFormat() {
        return cellFormat;
    }

    /**
     * Applies this style to the current cell.
     */
    public void apply() {
        if (cell != null && !dirtyFields.isEmpty()) {
            applyTo(cell, false);
        }
    }

    /**
     * Applies this style to given cell.
     *
     * @param cell      object representing target cell.
     * @param updateAll <code>true</code> to force updating of all cell format fields. Otherwise only dirty fields
     *                  will be updated.
     */
    public void applyTo(Cell cell, boolean updateAll) {
        if (cell != null && (updateAll || !dirtyFields.isEmpty())) {
            List<String> fieldsToUpdate = updateAll ? Collections.singletonList("userEnteredFormat") : dirtyFields;
            cell.getDocument().batchUpdate(r -> {
                CellData gCell = cell.getGCell();
                gCell.setUserEnteredFormat(cellFormat);
                r.addUpdateCellRequest(gCell, cell.getRowIndex(), cell.getColumnIndex(),
                        cell.getSheet().getId(), fieldsToUpdate);
            });
            dirtyFields.clear();
            this.cellFormat = cellFormat.clone();
            this.cell = cell;
        }
    }

    private com.google.api.services.sheets.v4.model.Color toGoogleColor(Color color) {
        return new com.google.api.services.sheets.v4.model.Color()
                .setRed((float) color.getRed() / 255)
                .setGreen((float) color.getGreen() / 255)
                .setBlue((float) color.getBlue() / 255)
                .setAlpha((float) color.getAlpha() / 255);
    }
}
