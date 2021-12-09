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

public class ExcelCellStyle {

    private int parentDocumentId;
    private Short poiCellStyleIndex;

    private String fontName = XSSFFont.DEFAULT_FONT_NAME;
    private short fontSize = XSSFFont.DEFAULT_FONT_SIZE;
    private boolean fontBold = false;
    private boolean fontItalic = false;
    private boolean fontStrikeout = false;
    private FontOffsetType fontOffset = FontOffsetType.NORMAL;
    private FontUnderlineStyle fontUnderline = FontUnderlineStyle.NONE;
    private ExcelColor fontColor = ExcelColors.AUTOMATIC.get();

    private DataFormat dataFormat = DataFormats.GENERAL.get();
    private ExcelColor bgColor = ExcelColors.AUTOMATIC.get();
    private FillPatternType bgFill = FillPatternType.NO_FILL;
    private HorizontalAlignment hAlign = HorizontalAlignment.GENERAL;
    private VerticalAlignment vAlign = VerticalAlignment.BOTTOM;
    private boolean wrapText = false;
    private short rotation = 0;
    private BorderStyle topBorder = BorderStyle.NONE;
    private BorderStyle rightBorder = BorderStyle.NONE;
    private BorderStyle bottomBorder = BorderStyle.NONE;
    private BorderStyle leftBorder = BorderStyle.NONE;
    private ExcelColor topBorderColor = ExcelColors.BLACK.get();
    private ExcelColor rightBorderColor = ExcelColors.BLACK.get();
    private ExcelColor bottomBorderColor = ExcelColors.BLACK.get();
    private ExcelColor leftBorderColor = ExcelColors.BLACK.get();
    private boolean hidden = false;
    private boolean locked = false;
    private short indention = 0;

    private boolean isDirty = false;

    private Cell cell;

    public ExcelCellStyle() {
    }

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

    public ExcelCellStyle font(String fontName) {
        this.fontName = fontName;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle fontSize(int fontSize) {
        this.fontSize = (short) fontSize;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bold(boolean isBold) {
        this.fontBold = isBold;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle italic(boolean isItalic) {
        this.fontItalic = isItalic;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle strikeout(boolean isStrikeout) {
        this.fontStrikeout = isStrikeout;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle underline(FontUnderlineStyle underlineStyle) {
        this.fontUnderline = underlineStyle;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle fontOffset(FontOffsetType offsetType) {
        this.fontOffset = offsetType;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle color(ExcelColor color) {
        this.fontColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle format(DataFormat format) {
        this.dataFormat = format;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle format(String format) {
        int fInx = BuiltinFormats.getBuiltinFormat(format);
        this.dataFormat = fInx >= 0 ? new DataFormat((short) fInx, format) : new DataFormat(format);
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle background(ExcelColor color) {
        this.bgColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle fill(FillPatternType fillPattern) {
        this.bgFill = fillPattern;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle hAlign(HorizontalAlignment hAlign) {
        this.hAlign = hAlign;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle vAlign(VerticalAlignment vAlign) {
        this.vAlign = vAlign;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle wrapText(boolean wrapText) {
        this.wrapText = wrapText;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle rotation(short rotation) {
        this.rotation = rotation;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle borders(BorderStyle style) {
        this.topBorder = this.rightBorder = this.bottomBorder = this.leftBorder = style;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle borders(BorderStyle topBottom, BorderStyle rightLeft) {
        this.topBorder = topBottom;
        this.rightBorder = rightLeft;
        this.bottomBorder = topBottom;
        this.leftBorder = rightLeft;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle borders(BorderStyle top, BorderStyle right, BorderStyle bottom, BorderStyle left) {
        this.topBorder = top;
        this.rightBorder = right;
        this.bottomBorder = bottom;
        this.leftBorder = left;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bordersColor(ExcelColor color) {
        this.topBorderColor = this.rightBorderColor = this.bottomBorderColor = this.leftBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bordersColor(ExcelColor topBottom, ExcelColor rightLeft) {
        this.topBorderColor = topBottom;
        this.rightBorderColor = rightLeft;
        this.bottomBorderColor = topBottom;
        this.leftBorderColor = rightLeft;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bordersColor(ExcelColor top, ExcelColor right, ExcelColor bottom, ExcelColor left) {
        this.topBorderColor = top;
        this.rightBorderColor = right;
        this.bottomBorderColor = bottom;
        this.leftBorderColor = left;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle topBorder(BorderStyle style) {
        this.topBorder = style;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle topBorder(ExcelColor color) {
        this.topBorderColor = color;
        return this;
    }

    public ExcelCellStyle topBorder(BorderStyle style, ExcelColor color) {
        this.topBorder = style;
        this.topBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle rightBorder(BorderStyle style) {
        this.rightBorder = style;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle rightBorder(ExcelColor color) {
        this.rightBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle rightBorder(BorderStyle style, ExcelColor color) {
        this.rightBorder = style;
        this.rightBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bottomBorder(BorderStyle style) {
        this.bottomBorder = style;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bottomBorder(ExcelColor color) {
        this.bottomBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle bottomBorder(BorderStyle style, ExcelColor color) {
        this.bottomBorder = style;
        this.bottomBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle leftBorder(BorderStyle style) {
        this.leftBorder = style;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle leftBorder(ExcelColor color) {
        this.leftBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle leftBorder(BorderStyle style, ExcelColor color) {
        this.leftBorder = style;
        this.leftBorderColor = color;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle hidden(boolean isHidden) {
        this.hidden = isHidden;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle locked(boolean isLocked) {
        this.locked = isLocked;
        this.isDirty = true;
        return this;
    }

    public ExcelCellStyle indention(short indention) {
        this.indention = indention;
        this.isDirty = true;
        return this;
    }

    public void apply() {
        if (cell != null && isDirty) {
            applyTo(cell);
        }
    }

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

    protected void applyToPoiCell(int documentId, org.apache.poi.ss.usermodel.Cell poiCell) {
        poiCell.setCellStyle(getOrCreatePoiCellStyle(documentId, poiCell.getSheet().getWorkbook()));
    }

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
