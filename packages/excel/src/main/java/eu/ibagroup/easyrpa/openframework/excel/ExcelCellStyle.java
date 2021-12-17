package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import eu.ibagroup.easyrpa.openframework.excel.style.DataFormats;
import eu.ibagroup.easyrpa.openframework.excel.style.ExcelColors;
import eu.ibagroup.easyrpa.openframework.excel.style.FontOffsetType;
import eu.ibagroup.easyrpa.openframework.excel.style.FontUnderlineStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;

/**
 * Keeps cell style parameters and allows to easily apply them to different cells even from different Excel documents.
 * <p>
 * Implementation of this class analyzes existing styles in Excel document and reuse them whenever is possible.
 */
public class ExcelCellStyle {

    /**
     * Name of cells font.
     */
    private String fontName = XSSFFont.DEFAULT_FONT_NAME;

    /**
     * Size of cells font in points.
     */
    private short fontSize = XSSFFont.DEFAULT_FONT_SIZE;

    /**
     * Shows whether cells font is bold.
     */
    private boolean fontBold = false;

    /**
     * Shows whether cells font is italic.
     */
    private boolean fontItalic = false;

    /**
     * Shows whether cells font is strikeout.
     */
    private boolean fontStrikeout = false;

    /**
     * Cells font offset type.
     */
    private FontOffsetType fontOffset = FontOffsetType.NORMAL;

    /**
     * Cells font underline style.
     */
    private FontUnderlineStyle fontUnderline = FontUnderlineStyle.NONE;

    /**
     * Cells font color
     */
    private ExcelColor fontColor = ExcelColors.AUTOMATIC.get();

    /**
     * Cells data format.
     */
    private DataFormat dataFormat = DataFormats.GENERAL.get();

    /**
     * Background color of cell.
     */
    private ExcelColor bgColor = ExcelColors.AUTOMATIC.get();

    /**
     * Type of filling background with color.
     */
    private FillPatternType bgFill = FillPatternType.NO_FILL;

    /**
     * Horizontal alignment of text in the cell.
     */
    private HorizontalAlignment hAlign = HorizontalAlignment.GENERAL;

    /**
     * Vertical alignment of text in the cell.
     */
    private VerticalAlignment vAlign = VerticalAlignment.BOTTOM;

    /**
     * Shows whether long-text in the cell is wrap into multiple lines.
     */
    private boolean wrapText = false;

    /**
     * Degree of rotation for the text in the cell.
     */
    private short rotation = 0;

    /**
     * Type of top border of the cell.
     */
    private BorderStyle topBorder = BorderStyle.NONE;

    /**
     * Type of right border of the cell.
     */
    private BorderStyle rightBorder = BorderStyle.NONE;

    /**
     * Type of bottom border of the cell.
     */
    private BorderStyle bottomBorder = BorderStyle.NONE;

    /**
     * Type of left border of the cell.
     */
    private BorderStyle leftBorder = BorderStyle.NONE;

    /**
     * Color of top border of the cell.
     */
    private ExcelColor topBorderColor = ExcelColors.BLACK.get();

    /**
     * Color of right border of the cell.
     */
    private ExcelColor rightBorderColor = ExcelColors.BLACK.get();

    /**
     * Color of bottom border of the cell.
     */
    private ExcelColor bottomBorderColor = ExcelColors.BLACK.get();

    /**
     * Color of left border of the cell.
     */
    private ExcelColor leftBorderColor = ExcelColors.BLACK.get();

    /**
     * Shows whether the cell is hidden
     */
    private boolean hidden = false;

    /**
     * Shows whether the cell is locked
     */
    private boolean locked = false;

    /**
     * Number of spaces to indent the text in the cell
     */
    private short indention = 0;

    /**
     * Shows whether this style has been changed after creation or recent applying to some cell.
     */
    private boolean isDirty = false;

    /**
     * Recent id of Excel document where this style is present
     */
    private int parentDocumentId;

    /**
     * Index of this style in styles table of Excel document with id <code>parentDocumentId</code>
     */
    private Short poiCellStyleIndex;

    /**
     * Recent cell that has this style
     */
    private Cell cell;

    /**
     * Creates cell style object with default parameters.
     */
    public ExcelCellStyle() {
    }

    /**
     * Creates cell style object with style parameters of given cell.
     *
     * @param cell object representing source cell.
     */
    protected ExcelCellStyle(Cell cell) {
        org.apache.poi.ss.usermodel.Cell poiCell = cell.getPoiCell();
        Workbook workbook = poiCell.getSheet().getWorkbook();
        CellStyle cellStyle = poiCell.getCellStyle();
        Font font = workbook.getFontAt(cellStyle.getFontIndex());
        fontName = font.getFontName();
        fontSize = font.getFontHeightInPoints();
        fontBold = font.getBold();
        fontItalic = font.getItalic();
        fontStrikeout = font.getStrikeout();
        fontUnderline = FontUnderlineStyle.valueOf(font.getUnderline());
        fontOffset = FontOffsetType.valueOf(font.getTypeOffset());
        fontColor = new ExcelColor(font.getColor());
        dataFormat = new DataFormat(cellStyle.getDataFormat(), cellStyle.getDataFormatString());
        bgColor = new ExcelColor(cellStyle.getFillForegroundColorColor());
        bgFill = cellStyle.getFillPattern();
        hAlign = cellStyle.getAlignment();
        vAlign = cellStyle.getVerticalAlignment();
        wrapText = cellStyle.getWrapText();
        rotation = cellStyle.getRotation();
        topBorder = cellStyle.getBorderTop();
        rightBorder = cellStyle.getBorderRight();
        bottomBorder = cellStyle.getBorderBottom();
        leftBorder = cellStyle.getBorderLeft();
        if (cellStyle instanceof XSSFCellStyle) {
            XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
            topBorderColor = new ExcelColor(xssfCellStyle.getTopBorderXSSFColor());
            rightBorderColor = new ExcelColor(xssfCellStyle.getRightBorderXSSFColor());
            bottomBorderColor = new ExcelColor(xssfCellStyle.getBottomBorderXSSFColor());
            leftBorderColor = new ExcelColor(xssfCellStyle.getLeftBorderXSSFColor());
        } else {
            topBorderColor = new ExcelColor(cellStyle.getTopBorderColor());
            rightBorderColor = new ExcelColor(cellStyle.getRightBorderColor());
            bottomBorderColor = new ExcelColor(cellStyle.getBottomBorderColor());
            leftBorderColor = new ExcelColor(cellStyle.getLeftBorderColor());
        }
        hidden = cellStyle.getHidden();
        locked = cellStyle.getLocked();
        indention = cellStyle.getIndention();

        parentDocumentId = cell.getDocument().getId();
        poiCellStyleIndex = cellStyle.getIndex();

        this.cell = cell;
    }

    /**
     * Sets name of cells font.
     *
     * @param fontName the name of font to apply.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle font(String fontName) {
        this.fontName = fontName;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets size of cells font in points.
     *
     * @param fontSize the size of font in points.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle fontSize(int fontSize) {
        this.fontSize = (short) fontSize;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether cells font should be bold.
     *
     * @param isBold <code>true</code> to set as bold and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle bold(boolean isBold) {
        this.fontBold = isBold;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether cells font should be italic.
     *
     * @param isItalic <code>true</code> to set as italic and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle italic(boolean isItalic) {
        this.fontItalic = isItalic;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether cells font should be strikeout.
     *
     * @param isStrikeout <code>true</code> to set as strikeout and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle strikeout(boolean isStrikeout) {
        this.fontStrikeout = isStrikeout;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether cell font should be underlined and defines its style.
     *
     * @param underlineStyle the underline style to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see FontUnderlineStyle
     */
    public ExcelCellStyle underline(FontUnderlineStyle underlineStyle) {
        this.fontUnderline = underlineStyle;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets offset type of cells font (normal, superscript or subscript).
     *
     * @param offsetType the type of font offset to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see FontOffsetType
     */
    public ExcelCellStyle fontOffset(FontOffsetType offsetType) {
        this.fontOffset = offsetType;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color of cells font.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle color(ExcelColor color) {
        this.fontColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets cells data format.
     *
     * @param format the data format to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see DataFormat
     */
    public ExcelCellStyle format(DataFormat format) {
        this.dataFormat = format;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets cells data format.
     *
     * @param format the string with necessary format to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see DataFormat
     */
    public ExcelCellStyle format(String format) {
        int fInx = BuiltinFormats.getBuiltinFormat(format);
        this.dataFormat = fInx >= 0 ? new DataFormat((short) fInx, format) : new DataFormat(format);
        this.isDirty = true;
        return this;
    }

    /**
     * Sets background color of cell.
     * <p>
     * Also initiate setting of <code>fill</code> to {@link FillPatternType#SOLID_FOREGROUND} if it
     * equals to {@link FillPatternType#NO_FILL}.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     * @see #fill(FillPatternType)
     */
    public ExcelCellStyle background(ExcelColor color) {
        this.bgColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets type of filling background with color.
     *
     * @param fillPattern the fill type to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see FillPatternType
     */
    public ExcelCellStyle fill(FillPatternType fillPattern) {
        this.bgFill = fillPattern;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets horizontal alignment of text in the cell.
     *
     * @param hAlign the alignment to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see HorizontalAlignment
     */
    public ExcelCellStyle hAlign(HorizontalAlignment hAlign) {
        this.hAlign = hAlign;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets vertical alignment of text in the cell.
     *
     * @param vAlign the alignment to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see VerticalAlignment
     */
    public ExcelCellStyle vAlign(VerticalAlignment vAlign) {
        this.vAlign = vAlign;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether long-text in the cell should wrap into multiple lines.
     *
     * @param wrapText <code>true</code> to set as wrap into multiple lines and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle wrapText(boolean wrapText) {
        this.wrapText = wrapText;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets degree of rotation for the text in the cell.
     *
     * @param rotation degree of rotation to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle rotation(short rotation) {
        this.rotation = rotation;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of all cell borders (top, right, bottom and left).
     *
     * @param style style of border to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle borders(BorderStyle style) {
        this.topBorder = this.rightBorder = this.bottomBorder = this.leftBorder = style;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of all cell borders by pairs: top-bottom and left-right.
     *
     * @param topBottom style of top and bottom borders.
     * @param rightLeft style of left and right borders.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle borders(BorderStyle topBottom, BorderStyle rightLeft) {
        this.topBorder = topBottom;
        this.rightBorder = rightLeft;
        this.bottomBorder = topBottom;
        this.leftBorder = rightLeft;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of cell borders.
     *
     * @param top    style of top border.
     * @param right  style of right border.
     * @param bottom style of bottom border.
     * @param left   style of left border.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle borders(BorderStyle top, BorderStyle right, BorderStyle bottom, BorderStyle left) {
        this.topBorder = top;
        this.rightBorder = right;
        this.bottomBorder = bottom;
        this.leftBorder = left;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color for all cell borders  (top, right, bottom and left).
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle bordersColor(ExcelColor color) {
        this.topBorderColor = this.rightBorderColor = this.bottomBorderColor = this.leftBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color for cell border pairs: top-bottom and right-left.
     *
     * @param topBottom the color of top and bottom borders.
     * @param rightLeft the color of right and left borders.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle bordersColor(ExcelColor topBottom, ExcelColor rightLeft) {
        this.topBorderColor = topBottom;
        this.rightBorderColor = rightLeft;
        this.bottomBorderColor = topBottom;
        this.leftBorderColor = rightLeft;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets cell border colors.
     *
     * @param top    the color of top border.
     * @param right  the color of right border.
     * @param bottom the color of bottom border.
     * @param left   the color of left border.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle bordersColor(ExcelColor top, ExcelColor right, ExcelColor bottom, ExcelColor left) {
        this.topBorderColor = top;
        this.rightBorderColor = right;
        this.bottomBorderColor = bottom;
        this.leftBorderColor = left;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of top border.
     *
     * @param style the style to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle topBorder(BorderStyle style) {
        this.topBorder = style;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color of top border.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle topBorder(ExcelColor color) {
        this.topBorderColor = color;
        return this;
    }

    /**
     * Sets style and color of top border.
     *
     * @param style the style to set.
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     * @see ExcelColor
     */
    public ExcelCellStyle topBorder(BorderStyle style, ExcelColor color) {
        this.topBorder = style;
        this.topBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of right border.
     *
     * @param style the style to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle rightBorder(BorderStyle style) {
        this.rightBorder = style;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color of right border.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle rightBorder(ExcelColor color) {
        this.rightBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style and color of right border.
     *
     * @param style the style to set.
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     * @see ExcelColor
     */
    public ExcelCellStyle rightBorder(BorderStyle style, ExcelColor color) {
        this.rightBorder = style;
        this.rightBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of bottom border.
     *
     * @param style the style to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle bottomBorder(BorderStyle style) {
        this.bottomBorder = style;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color of bottom border.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle bottomBorder(ExcelColor color) {
        this.bottomBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style and color of bottom border.
     *
     * @param style the style to set.
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     * @see ExcelColor
     */
    public ExcelCellStyle bottomBorder(BorderStyle style, ExcelColor color) {
        this.bottomBorder = style;
        this.bottomBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style of left border.
     *
     * @param style the style to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     */
    public ExcelCellStyle leftBorder(BorderStyle style) {
        this.leftBorder = style;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets color of left border.
     *
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see ExcelColor
     */
    public ExcelCellStyle leftBorder(ExcelColor color) {
        this.leftBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets style and color of left border.
     *
     * @param style the style to set.
     * @param color the color to set.
     * @return this cell style object to allow joining of methods calls into chain.
     * @see BorderStyle
     * @see ExcelColor
     */
    public ExcelCellStyle leftBorder(BorderStyle style, ExcelColor color) {
        this.leftBorder = style;
        this.leftBorderColor = color;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether the cell should be hidden.
     *
     * @param isHidden <code>true</code> to set as hidden and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle hidden(boolean isHidden) {
        this.hidden = isHidden;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets whether the cell should be locked.
     *
     * @param isLocked <code>true</code> to set as locked and <code>false</code> otherwise.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle locked(boolean isLocked) {
        this.locked = isLocked;
        this.isDirty = true;
        return this;
    }

    /**
     * Sets the number of spaces to indent the text in the cell.
     *
     * @param indention the number of spaces to set.
     * @return this cell style object to allow joining of methods calls into chain.
     */
    public ExcelCellStyle indention(short indention) {
        this.indention = indention;
        this.isDirty = true;
        return this;
    }

    /**
     * Applies this style to the current cell.
     */
    public void apply() {
        if (cell != null && isDirty) {
            applyTo(cell);
        }
    }

    /**
     * Applies this style to given cell.
     *
     * @param cell object representing target cell.
     */
    public void applyTo(Cell cell) {
        int documentId = cell.getDocument().getId();
        int sheetIndex = cell.getSheetIndex();

        CellRange mr = cell.getMergedRegion();
        if (mr == null) {
            applyToPoiCell(documentId, cell.getPoiCell());
        } else {
            for (int i = mr.getFirstRow(); i <= mr.getLastRow(); i++) {
                for (int j = mr.getFirstCol(); j <= mr.getLastCol(); j++) {
                    applyToPoiCell(documentId, POIElementsCache.getPoiCell(documentId, null, sheetIndex, i, j));
                }
            }
        }

        this.cell = cell;
    }

    /**
     * Applies this style to given POI cell.
     *
     * @param documentId id of document that owns the POI cell.
     * @param poiCell    instance of target POI cell
     */
    protected void applyToPoiCell(int documentId, org.apache.poi.ss.usermodel.Cell poiCell) {
        poiCell.setCellStyle(getOrCreatePoiCellStyle(documentId, poiCell.getSheet().getWorkbook()));
    }

    /**
     * Analyzes existing styles in Excel document and return POI cell style that corresponds to this style. Otherwise
     * creates new POI cell style with registration in styles table.
     *
     * @param documentId id of target Excel document.
     * @param workbook   instance of POI workbook representing target Excel document.
     * @return instance of POI cell style that corresponds to this style.
     */
    protected CellStyle getOrCreatePoiCellStyle(int documentId, Workbook workbook) {
        CellStyle cellStyle = null;

        if (!isDirty && poiCellStyleIndex != null && parentDocumentId == documentId) {
            cellStyle = workbook.getCellStyleAt(poiCellStyleIndex);
            if (cellStyle != null) {
                return cellStyle;
            }
        }

        Font font = null;

        if (bgColor.isDefined() && bgColor.getIndex() != ExcelColors.AUTOMATIC.getPoiIndex() && bgFill == FillPatternType.NO_FILL) {
            bgFill = FillPatternType.SOLID_FOREGROUND;
        }

        for (int i = 0; i < workbook.getNumCellStyles(); i++) {
            CellStyle cs = workbook.getCellStyleAt(i);
            if (isSameStyleAs(cs)) {
                Font f = workbook.getFontAt(cs.getFontIndex());
                if (isSameFontAs(f)) {
                    cellStyle = cs;
                    font = f;
                    break;
                }
            }
        }

        if (cellStyle == null) {
            cellStyle = workbook.createCellStyle();

            for (int i = 0; i < workbook.getNumberOfFonts(); i++) {
                Font f = workbook.getFontAt(i);
                if (isSameFontAs(workbook.getFontAt(i))) {
                    font = f;
                    break;
                }
            }

            if (font == null) {
                font = workbook.createFont();
                font.setFontName(fontName);
                font.setFontHeightInPoints(fontSize);
                font.setBold(fontBold);
                font.setItalic(fontItalic);
                font.setStrikeout(fontStrikeout);
                font.setUnderline(fontUnderline.getPoiValue());
                font.setTypeOffset(fontOffset.getPoiValue());
                if (fontColor.isIndexed()) {
                    font.setColor(fontColor.getIndex());
                }
            }
            cellStyle.setFont(font);

            if (dataFormat.isIndexed()) {
                cellStyle.setDataFormat(dataFormat.getIndex());
            } else {
                cellStyle.setDataFormat(workbook.createDataFormat().getFormat(dataFormat.getFormat()));
            }

            if (bgColor.isIndexed()) {
                cellStyle.setFillForegroundColor(bgColor.getIndex());
            } else if (cellStyle instanceof XSSFCellStyle) {
                ((XSSFCellStyle) cellStyle).setFillForegroundColor(bgColor.toXSSFColor(workbook));
            }

            cellStyle.setFillPattern(bgFill);
            cellStyle.setAlignment(hAlign);
            cellStyle.setVerticalAlignment(vAlign);
            cellStyle.setWrapText(wrapText);
            cellStyle.setRotation(rotation);

            if (cellStyle instanceof XSSFCellStyle) {
                CTBorder ct = TypeUtils.callMethod(cellStyle, "getCTBorder");
                if (topBorder == BorderStyle.NONE) {
                    if (ct.isSetTop()) ct.unsetTop();
                } else {
                    CTBorderPr pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
                    pr.setStyle(STBorderStyle.Enum.forInt(topBorder.getCode() + 1));
                    pr.setColor(topBorderColor.toXSSFColor(workbook).getCTColor());
                }
                if (rightBorder == BorderStyle.NONE) {
                    if (ct.isSetRight()) ct.unsetRight();
                } else {
                    CTBorderPr pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
                    pr.setStyle(STBorderStyle.Enum.forInt(rightBorder.getCode() + 1));
                    pr.setColor(rightBorderColor.toXSSFColor(workbook).getCTColor());
                }
                if (bottomBorder == BorderStyle.NONE) {
                    if (ct.isSetBottom()) ct.unsetBottom();
                } else {
                    CTBorderPr pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
                    pr.setStyle(STBorderStyle.Enum.forInt(bottomBorder.getCode() + 1));
                    pr.setColor(bottomBorderColor.toXSSFColor(workbook).getCTColor());
                }
                if (leftBorder == BorderStyle.NONE) {
                    if (ct.isSetLeft()) ct.unsetLeft();
                } else {
                    CTBorderPr pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
                    pr.setStyle(STBorderStyle.Enum.forInt(leftBorder.getCode() + 1));
                    pr.setColor(leftBorderColor.toXSSFColor(workbook).getCTColor());
                }
                TypeUtils.callMethod(cellStyle, "addBorder", ct);

            } else {
                cellStyle.setBorderTop(topBorder);
                cellStyle.setBorderRight(rightBorder);
                cellStyle.setBorderBottom(bottomBorder);
                cellStyle.setBorderLeft(leftBorder);
                if (topBorderColor.isIndexed()) {
                    cellStyle.setTopBorderColor(topBorderColor.getIndex());
                }
                if (rightBorderColor.isIndexed()) {
                    cellStyle.setRightBorderColor(rightBorderColor.getIndex());
                }
                if (bottomBorderColor.isIndexed()) {
                    cellStyle.setBottomBorderColor(bottomBorderColor.getIndex());
                }
                if (leftBorderColor.isIndexed()) {
                    cellStyle.setLeftBorderColor(leftBorderColor.getIndex());
                }
            }

            cellStyle.setHidden(hidden);
            cellStyle.setLocked(locked);
            cellStyle.setIndention(indention);
        }

        parentDocumentId = documentId;
        poiCellStyleIndex = cellStyle.getIndex();
        isDirty = false;

        return cellStyle;
    }

    /**
     * Checks whether given POI cell style corresponds to this style.
     *
     * @param cellStyle instance of POI cell style to check.
     * @return <code>true</code> if POI cell style corresponds to this style or <code>false</code> otherwise.
     */
    protected boolean isSameStyleAs(CellStyle cellStyle) {
        boolean result = dataFormat.getFormat().equals(cellStyle.getDataFormatString())
                && bgColor.isSameColorAs(cellStyle.getFillForegroundColorColor())
                && bgFill == cellStyle.getFillPattern()
                && hAlign == cellStyle.getAlignment()
                && vAlign == cellStyle.getVerticalAlignment()
                && wrapText == cellStyle.getWrapText()
                && rotation == cellStyle.getRotation()
                && topBorder == cellStyle.getBorderTop()
                && rightBorder == cellStyle.getBorderRight()
                && bottomBorder == cellStyle.getBorderBottom()
                && leftBorder == cellStyle.getBorderLeft();

        if (cellStyle instanceof XSSFCellStyle) {
            XSSFCellStyle xssfCellStyle = (XSSFCellStyle) cellStyle;
            result = result
                    && topBorderColor.isSameColorAs(xssfCellStyle.getTopBorderXSSFColor())
                    && rightBorderColor.isSameColorAs(xssfCellStyle.getRightBorderXSSFColor())
                    && bottomBorderColor.isSameColorAs(xssfCellStyle.getBottomBorderXSSFColor())
                    && leftBorderColor.isSameColorAs(xssfCellStyle.getLeftBorderXSSFColor());
        } else {
            result = result
                    && topBorderColor.isSameColorAs(cellStyle.getTopBorderColor())
                    && rightBorderColor.isSameColorAs(cellStyle.getRightBorderColor())
                    && bottomBorderColor.isSameColorAs(cellStyle.getBottomBorderColor())
                    && leftBorderColor.isSameColorAs(cellStyle.getLeftBorderColor());
        }

        return result
                && hidden == cellStyle.getHidden()
                && locked == cellStyle.getLocked()
                && indention == cellStyle.getIndention();
    }

    /**
     * Checks whether given POI font corresponds to the font of this style.
     *
     * @param font instance of POI font to check.
     * @return <code>true</code> if POI font corresponds to the font of this style or <code>false</code> otherwise.
     */
    protected boolean isSameFontAs(Font font) {
        return fontName.equals(font.getFontName())
                && fontSize == font.getFontHeightInPoints()
                && fontBold == font.getBold()
                && fontItalic == font.getItalic()
                && fontStrikeout == font.getStrikeout()
                && fontUnderline == FontUnderlineStyle.valueOf(font.getUnderline())
                && fontOffset == FontOffsetType.valueOf(font.getTypeOffset())
                && fontColor.isSameColorAs(font.getColor());
    }
}
