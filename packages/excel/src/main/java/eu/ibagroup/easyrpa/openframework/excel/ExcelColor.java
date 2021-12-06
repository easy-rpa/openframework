package eu.ibagroup.easyrpa.openframework.excel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;

public class ExcelColor {

    private short index = -1;

    private byte red = -1;

    private byte green = -1;

    private byte blue = -1;

    public ExcelColor(short index) {
        this.index = index;
    }

    public ExcelColor(byte red, byte green, byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public ExcelColor(String rgbHexCode) {
        Color color = Color.decode(rgbHexCode);
        red = (byte) color.getRed();
        green = (byte) color.getGreen();
        blue = (byte) color.getBlue();
    }

    protected ExcelColor(org.apache.poi.ss.usermodel.Color color) {
        if (color instanceof XSSFColor) {
            XSSFColor xssfColor = (XSSFColor) color;
            if (xssfColor.isIndexed()) {
                index = xssfColor.getIndex();
            } else if (xssfColor.isRGB()) {
                byte[] rgb = xssfColor.getRGB();
                red = rgb[0];
                green = rgb[1];
                blue = rgb[2];
            }
        } else if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor) color;
            index = hssfColor.getIndex();
        }
    }

    public short getIndex() {
        return index;
    }

    public byte getRed() {
        return red;
    }

    public byte getGreen() {
        return green;
    }

    public byte getBlue() {
        return blue;
    }

    public boolean isIndexed() {
        return index >= 0;
    }

    public boolean isDefined() {
        return index >= 0 || (red >= 0 && green >= 0 && blue >= 0);
    }

    protected XSSFColor toXSSFColor(Workbook workbook) {
        IndexedColorMap colorMap = workbook instanceof XSSFWorkbook
                ? ((XSSFWorkbook) workbook).getStylesSource().getIndexedColors()
                : new DefaultIndexedColorMap();
        if (isIndexed()) {
            return new XSSFColor(IndexedColors.fromInt(index), colorMap);
        }
        return new XSSFColor(new byte[]{red, green, blue}, colorMap);
    }

    protected boolean isSameColorAs(short colorIndex) {
        return this.index == colorIndex;
    }

    protected boolean isSameColorAs(org.apache.poi.ss.usermodel.Color color) {
        if (color instanceof XSSFColor) {
            XSSFColor xssfColor = (XSSFColor) color;
            if (index >= 0 && xssfColor.isIndexed() && xssfColor.getIndex() == index) {
                return true;
            }
            if (xssfColor.isRGB()) {
                byte[] rgb = xssfColor.getRGB();
                return red == rgb[0] && green == rgb[1] && blue == rgb[2];
            }
        } else if (color instanceof HSSFColor) {
            HSSFColor hssfColor = (HSSFColor) color;
            return index >= 0 && hssfColor.getIndex() == index;
        }
        return false;
    }
}
