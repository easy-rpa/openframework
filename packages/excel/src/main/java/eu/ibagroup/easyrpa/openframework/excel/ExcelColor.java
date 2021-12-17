package eu.ibagroup.easyrpa.openframework.excel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;

/**
 * Represents specific color used for styling cells of Excel document.
 * <p>
 * Can be presented as index of built-in Excel color (indexed color) or as ARGB value (custom color).
 *
 * @see IndexedColors
 */
public class ExcelColor {

    /**
     * Index of built-in Excel color.
     */
    private short index = -1;

    /**
     * Alpha component of this color
     */
    private byte alpha = -1;

    /**
     * Red component of this color
     */
    private byte red = -1;

    /**
     * Green component of this color
     */
    private byte green = -1;

    /**
     * Blue component of this color
     */
    private byte blue = -1;

    /**
     * Creates a new instance of indexed color.
     *
     * @param index index of corresponding built-in Excel color.
     */
    public ExcelColor(short index) {
        this.index = index;
    }

    /**
     * Creates a new instance of custom color with specific ARGB value.
     *
     * @param alpha the Alpha component of the color.
     * @param red   the Red component of the color.
     * @param green the Green component of the color.
     * @param blue  the Blue component of the color.
     */
    public ExcelColor(byte alpha, byte red, byte green, byte blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Creates a new instance of custom color with specific ARGB value.
     *
     * @param argbHexCode the hex ARGB string of the color.
     */
    public ExcelColor(String argbHexCode) {
        Color color = Color.decode(argbHexCode);
        alpha = (byte) color.getAlpha();
        red = (byte) color.getRed();
        green = (byte) color.getGreen();
        blue = (byte) color.getBlue();
    }

    /**
     * Creates a new instance of color based on given POI color.
     *
     * @param color the instance of source POI color.
     */
    protected ExcelColor(org.apache.poi.ss.usermodel.Color color) {
        if (color instanceof XSSFColor) {
            XSSFColor xssfColor = (XSSFColor) color;
            if (xssfColor.isIndexed()) {
                index = xssfColor.getIndex();
            } else if (xssfColor.isRGB()) {
                byte[] argb = xssfColor.getARGB();
                alpha = argb[0];
                red = argb[1];
                green = argb[2];
                blue = argb[3];
            }
        } else if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor) color;
            index = hssfColor.getIndex();
        }
    }

    /**
     * Gets index of built-in Excel color that corresponds to this color.
     *
     * @return the index of corresponding built-in Excel color or <code>-1</code> if this is a custom color.
     * otherwise.
     */
    public short getIndex() {
        return index;
    }

    /**
     * Gets Alpha component of this color.
     *
     * @return the value of Alpha component of this color or <code>-1</code> if this is an indexed color.
     */
    public byte getAlpha() {
        return alpha;
    }

    /**
     * Gets Red component of this color.
     *
     * @return the value of Red component of this color or <code>-1</code> if this is an indexed color.
     */
    public byte getRed() {
        return red;
    }

    /**
     * Gets Green component of this color.
     *
     * @return the value of Green component of this color or <code>-1</code> if this is an indexed color.
     */
    public byte getGreen() {
        return green;
    }

    /**
     * Gets Blue component of this color.
     *
     * @return the value of Blue component of this color or <code>-1</code> if this is an indexed color.
     */
    public byte getBlue() {
        return blue;
    }

    /**
     * Checks whether this color is indexed color and thous has index of corresponding built-in Excel color.
     *
     * @return <code>true</code> if this color is indexed color or <code>false</code> otherwise.
     */
    public boolean isIndexed() {
        return index >= 0;
    }

    /**
     * Checks whether this color is properly defined.
     *
     * @return <code>true</code> if this color has corresponding index (is indexed color) or ARGB value (is custom
     * color). Otherwise returns <code>false</code>.
     */
    public boolean isDefined() {
        return index >= 0 || (alpha >= 0 && red >= 0 && green >= 0 && blue >= 0);
    }

    /**
     * Converts this color into corresponding POI color object.
     *
     * @param workbook instance of related POI workbook.
     * @return the POI color object that corresponds to this color.
     */
    protected XSSFColor toXSSFColor(Workbook workbook) {
        IndexedColorMap colorMap = workbook instanceof XSSFWorkbook
                ? ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors()
                : new DefaultIndexedColorMap();
        if (isIndexed()) {
            return new XSSFColor(IndexedColors.fromInt(index), colorMap);
        }
        return new XSSFColor(new byte[]{alpha, red, green, blue}, colorMap);
    }

    /**
     * Checks whether this color is indexed color and has the same index as given.
     *
     * @param colorIndex index of built-in Excel color to check.
     * @return <code>true</code> if this color is indexed color and has the same index as given or <code>false</code>
     * otherwise.
     */
    protected boolean isSameColorAs(short colorIndex) {
        return this.index == colorIndex;
    }

    /**
     * Checks whether given POI color corresponds to this color.
     *
     * @param color instance of POI color to check.
     * @return <code>true</code> if POI color corresponds to this color or <code>false</code> otherwise.
     */
    protected boolean isSameColorAs(org.apache.poi.ss.usermodel.Color color) {
        if (color == null && !isDefined()) {
            return true;

        } else if (color instanceof XSSFColor) {
            XSSFColor xssfColor = (XSSFColor) color;
            if (index >= 0 && xssfColor.isIndexed() && xssfColor.getIndex() == index) {
                return true;
            }
            if (xssfColor.isRGB()) {
                byte[] argb = xssfColor.getARGB();
                return alpha == argb[0] && red == argb[1] && green == argb[2] && blue == argb[3];
            }

        } else if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor) color;
            return index >= 0 && hssfColor.getIndex() == index;
        }
        return false;
    }
}
