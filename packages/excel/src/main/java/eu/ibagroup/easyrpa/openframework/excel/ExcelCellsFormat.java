package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents format of some cells range.</p>
 *
 * <p>The format includes information about style of each cell in range, merged regions and regions with data
 * validation constraints. This information can be applied multiple times to different cells ranges of any sheet
 * within Excel Document.</p>
 * <br>
 * <p>If range size of this format is less than size of cells range to which this format is going to be applied then
 * it will be repeatedly applied to each cells sub-range with size equals to size of this format range.</p>
 * <p>
 * E.g. lets take a format that keeps styles of cells range with 3 rows and 3 columns and style of cell in the center
 * of range has a border:
 * <table cellSpacing="0" cellPadding="10">
 *      <tr><td>St11</td><td>St21</td><td>St31</td></tr>
 *      <tr><td>St12</td><td style="border:1px solid">St22</td><td>St32</td></tr>
 *      <tr><td>St13</td><td>St23</td><td>St33</td></tr>
 * </table>
 * If this format apply to cells range with 5 columns and 6 rows the result will be the following:
 * <table cellSpacing="0" cellPadding="10">
 *      <tr><td>St11</td><td>St21</td><td>St31</td><td>St11</td><td>St21</td></tr>
 *      <tr><td>St12</td><td style="border:1px solid">St22</td><td>St32</td><td>St12</td><td style="border:1px solid">St22</td></tr>
 *      <tr><td>St13</td><td>St23</td><td>St33</td><td>St13</td><td>St23</td></tr>
 *      <tr><td>St11</td><td>St21</td><td>St31</td><td>St11</td><td>St21</td></tr>
 *      <tr><td>St12</td><td style="border:1px solid">St22</td><td>St32</td><td>St12</td><td style="border:1px solid">St22</td></tr>
 *      <tr><td>St13</td><td>St23</td><td>St33</td><td>St13</td><td>St23</td></tr>
 * </table>
 * </p>
 */
public class ExcelCellsFormat {

    /**
     * Contains styles of all cells in the range.
     */
    private ExcelCellStyle[][] cellStyles;

    /**
     * Contains existing merged regions hit into the range.
     */
    private CellRange[] mergedRegions;

    /**
     * Amount of rows in the range.
     */
    private int rowsCount;

    /**
     * Amount of columns in the range.
     */
    private int columnsCount;

    /**
     * Contains existing data validation constraints hit into the range.
     */
    private DataValidation[] dataValidations;

    /**
     * Creates a new format object that includes formatting information of all cells of given sheet.
     *
     * @param sheet object representing source sheet.
     */
    public ExcelCellsFormat(Sheet sheet) {
        this(sheet, 0, 0, sheet.getLastRowIndex(), sheet.getLastColumnIndex());
    }

    /**
     * Creates a new format object that includes formatting information of all cells of given range on sheet.
     *
     * @param sheet     object representing source sheet.
     * @param cellRange the necessary range of source sheet cells.
     */
    public ExcelCellsFormat(Sheet sheet, CellRange cellRange) {
        this(sheet, cellRange.getFirstRow(), cellRange.getFirstCol(), cellRange.getLastRow(), cellRange.getLastCol());
    }

    /**
     * Creates a new format object that includes formatting information of all cells of given column.
     *
     * @param column object representing source column.
     */
    public ExcelCellsFormat(Column column) {
        this(column.getSheet(), column.getFirstRowIndex(), column.getIndex(), column.getLastRowIndex(), column.getIndex());
    }

    /**
     * Creates a new format object that includes formatting information of all cells of given range in specific column.
     *
     * @param column   object representing source column.
     * @param firstRow 0-based index of the first row in necessary range of column cells.
     * @param lastRow  0-based index of the last row in necessary range of column cells.
     */
    public ExcelCellsFormat(Column column, int firstRow, int lastRow) {
        this(column.getSheet(), firstRow, column.getIndex(), lastRow, column.getIndex());
    }

    /**
     * Creates a new format object that includes formatting information of all cells of given row.
     *
     * @param row object representing source row.
     */
    public ExcelCellsFormat(Row row) {
        this(row.getSheet(), row.getIndex(), row.getFirstCellIndex(), row.getIndex(), row.getLastCellIndex());
    }

    /**
     * Creates a new format object that includes formatting information of all cells of given range in specific row.
     *
     * @param row      object representing source row.
     * @param firstCol 0-based index of the first column in necessary range of row cells.
     * @param lastCol  0-based index of the last column in necessary range of row cells.
     */
    public ExcelCellsFormat(Row row, int firstCol, int lastCol) {
        this(row.getSheet(), row.getIndex(), firstCol, row.getIndex(), lastCol);
    }

    /**
     * Creates a new format object that includes formatting information of given cell.
     *
     * @param cell object representing source cell.
     */
    public ExcelCellsFormat(Cell cell) {
        this(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex());
    }

    /**
     * Creates a new format object that includes formatting information of all cells of given range on sheet.
     *
     * @param sheet    object representing source sheet.
     * @param firstRow 0-based index of the top row of the range.
     * @param firstCol 0-based index of the left column of the range.
     * @param lastRow  0-based index of the bottom row of the range.
     * @param lastCol  0-based index of the right column of the range.
     */
    public ExcelCellsFormat(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        rowsCount = lastRow - firstRow + 1;
        columnsCount = lastCol - firstCol + 1;
        readMergedRegions(sheet, firstRow, firstCol, lastRow, lastCol);
        readCellStyles(sheet, firstRow, firstCol, lastRow, lastCol);
        readDataValidations(sheet, firstRow, firstCol, lastRow, lastCol);
    }

    /**
     * Applies current formatting information to all cells of given sheet.
     *
     * @param sheet object representing target sheet.
     */
    public void applyTo(Sheet sheet) {
        applyTo(sheet, 0, 0, sheet.getLastRowIndex(), sheet.getLastColumnIndex());
    }

    /**
     * Applies current formatting information to all cells of given range on specific sheet.
     *
     * @param sheet     object representing target sheet.
     * @param cellRange the necessary range of target sheet cells.
     */
    public void applyTo(Sheet sheet, CellRange cellRange) {
        applyTo(sheet, cellRange.getFirstRow(), cellRange.getFirstCol(), cellRange.getLastRow(), cellRange.getLastCol());
    }

    /**
     * Applies current formatting information to all cells of given column.
     *
     * @param column object representing target column.
     */
    public void applyTo(Column column) {
        applyTo(column.getSheet(), column.getFirstRowIndex(), column.getIndex(), column.getLastRowIndex(), column.getIndex());
    }

    /**
     * Applies current formatting information to all cells of given range in specific column.
     *
     * @param column   object representing target column.
     * @param firstRow 0-based index of the first row in the range of target column cells.
     * @param lastRow  0-based index of the last row in the range of target column cells.
     */
    public void applyTo(Column column, int firstRow, int lastRow) {
        applyTo(column.getSheet(), firstRow, column.getIndex(), lastRow, column.getIndex());
    }

    /**
     * Applies current formatting information to all cells of given row.
     *
     * @param row object representing target row.
     */
    public void applyTo(Row row) {
        applyTo(row.getSheet(), row.getIndex(), 0, row.getIndex(), row.getLastCellIndex());
    }

    /**
     * Applies current formatting information to all cells of given range in specific row.
     *
     * @param row      object representing target row.
     * @param firstCol 0-based index of the first column in the range of target row cells.
     * @param lastCol  0-based index of the last column in the range of target row cells.
     */
    public void applyTo(Row row, int firstCol, int lastCol) {
        applyTo(row.getSheet(), row.getIndex(), firstCol, row.getIndex(), lastCol);
    }

    /**
     * Applies current formatting information to given cell.
     *
     * @param cell object representing target cell.
     */
    public void applyTo(Cell cell) {
        applyTo(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex());
    }

    /**
     * Applies current formatting information to all cells of given range on specific sheet.
     *
     * @param sheet    object representing target sheet.
     * @param firstRow 0-based index of the top row of the range.
     * @param firstCol 0-based index of the left column of the range.
     * @param lastRow  0-based index of the bottom row of the range.
     * @param lastCol  0-based index of the right column of the range.
     */
    public void applyTo(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        applyMergedRegions(sheet, firstRow, firstCol, lastRow, lastCol);
        applyCellStyles(sheet, firstRow, firstCol, lastRow, lastCol);
        applyDataValidations(sheet, firstRow, firstCol, lastRow, lastCol);
    }

    /**
     * Applies contained cell styles to given cells range of sheet.
     */
    private void applyCellStyles(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        if (cellStyles.length == 0) {
            return;
        }
        for (int rInd = firstRow, i = 0; rInd <= lastRow; rInd++, i++) {
            if (i == cellStyles.length) i = 0;
            ExcelCellStyle[] rowStyles = cellStyles[i];

            Row row = sheet.getRow(rInd);
            if (row != null) {
                for (int cInd = firstCol, j = 0; cInd <= lastCol; cInd++, j++) {
                    if (j == rowStyles.length) j = 0;
                    ExcelCellStyle cellStyle = rowStyles[j];
                    Cell cell = row.getCell(cInd);
                    if (cellStyle != null && cell != null) {
                        cellStyle.applyTo(cell);
                    }
                }
            }
        }
    }

    /**
     * Applies contained merged regions to given cells range of sheet.
     */
    private void applyMergedRegions(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        List<CellRange> regionsToAdd = new ArrayList<>();
        for (CellRange region : mergedRegions) {
            int i = -1;
            while (firstRow + ++i * rowsCount + region.getLastRow() <= lastRow) {
                int j = -1;
                while (firstCol + ++j * columnsCount + region.getLastCol() <= lastCol) {
                    regionsToAdd.add(new CellRange(firstRow + i * rowsCount + region.getFirstRow(),
                            firstCol + j * columnsCount + region.getFirstCol(),
                            firstRow + i * rowsCount + region.getLastRow(),
                            firstCol + j * columnsCount + region.getLastCol()));
                }
            }
        }

        int documentId = sheet.getDocument().getId();
        int sheetIndex = sheet.getIndex();
        org.apache.poi.ss.usermodel.Sheet poiSheet = sheet.getPoiSheet();
        for (CellRange region : regionsToAdd) {
            CellRangeAddress poiRegion = new CellRangeAddress(region.getFirstRow(), region.getLastRow(), region.getFirstCol(), region.getLastCol());
            int regionIndex = poiSheet.addMergedRegion(poiRegion);
            if (regionIndex >= 0) {
                POIElementsCache.addMergedRegion(documentId, sheetIndex, regionIndex, poiRegion);
            }
        }
    }

    /**
     * Applies contained data validation constraints to given cells range of sheet.
     */
    private void applyDataValidations(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        org.apache.poi.ss.usermodel.Sheet poiSheet = sheet.getPoiSheet();
        for (DataValidation dv : dataValidations) {
            CellRangeAddressList dvRegionsToApply = new CellRangeAddressList();

            CellRangeAddressList regions = dv.getRegions();
            for (int rI = 0; rI < regions.countRanges(); rI++) {
                CellRangeAddress dvRegion = regions.getCellRangeAddress(rI);
                int i = -1;
                while (firstRow + ++i * rowsCount + dvRegion.getLastRow() <= lastRow) {
                    int j = -1;
                    while (firstCol + ++j * columnsCount + dvRegion.getLastColumn() <= lastCol) {
                        dvRegionsToApply.addCellRangeAddress(new CellRangeAddress(firstRow + i * rowsCount + dvRegion.getFirstRow(),
                                firstRow + i * rowsCount + dvRegion.getLastRow(),
                                firstCol + j * columnsCount + dvRegion.getFirstColumn(),
                                firstCol + j * columnsCount + dvRegion.getLastColumn()));
                    }
                }
            }

            if (dvRegionsToApply.countRanges() > 0) {
                DataValidation dvToAdd = poiSheet.getDataValidationHelper().createValidation(dv.getValidationConstraint(), dvRegionsToApply);
                poiSheet.addValidationData(dvToAdd);
            }
        }
    }

    /**
     * Extracts cell styles from given cells range of sheet.
     */
    private void readCellStyles(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        cellStyles = new ExcelCellStyle[rowsCount][columnsCount];
        for (int rInd = firstRow, i = 0; rInd <= lastRow; rInd++, i++) {
            Row row = sheet.getRow(rInd);
            if (row != null) {
                for (int cInd = firstCol, j = 0; cInd <= lastCol; cInd++, j++) {
                    Cell cell = row.getCell(cInd);
                    if (cell != null) {
                        cellStyles[i][j] = row.getCell(cInd).getStyle();
                    }
                }
            }
        }
    }

    /**
     * Extracts merged regions that hit into given cells range of sheet.
     */
    private void readMergedRegions(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        List<CellRange> mergedRegions = new ArrayList<>();

        List<CellRangeAddress> regions = sheet.getPoiSheet().getMergedRegions();
        for (CellRangeAddress region : regions) {
            if (firstRow <= region.getFirstRow() && region.getFirstRow() <= lastRow
                    && firstCol <= region.getFirstColumn() && region.getFirstColumn() <= lastCol
                    && firstRow <= region.getLastRow() && region.getLastRow() <= lastRow
                    && firstCol <= region.getLastColumn() && region.getLastColumn() <= lastCol) {

                mergedRegions.add(new CellRange(region.getFirstRow() - firstRow,
                        region.getFirstColumn() - firstCol,
                        region.getLastRow() - firstRow,
                        region.getLastColumn() - firstCol));
            }
        }
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        this.mergedRegions = mergedRegions.toArray(new CellRange[mergedRegions.size()]);
    }

    /**
     * Extracts data validation constraints that hit into given cells range of sheet.
     */
    private void readDataValidations(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        org.apache.poi.ss.usermodel.Sheet poiSheet = sheet.getPoiSheet();

        List<DataValidation> dataValidations = new ArrayList<>();

        for (DataValidation dv : poiSheet.getDataValidations()) {
            CellRangeAddressList dvRegionsInRange = new CellRangeAddressList();
            DataValidationConstraint dvConstraint = dv.getValidationConstraint();

            CellRangeAddressList regions = dv.getRegions();
            for (int i = 0; i < regions.countRanges(); i++) {
                CellRangeAddress dvRegion = regions.getCellRangeAddress(i);
                if (firstRow <= dvRegion.getFirstRow() && dvRegion.getFirstRow() <= lastRow
                        && firstCol <= dvRegion.getFirstColumn() && dvRegion.getFirstColumn() <= lastCol
                        && firstRow <= dvRegion.getLastRow() && dvRegion.getLastRow() <= lastRow
                        && firstCol <= dvRegion.getLastColumn() && dvRegion.getLastColumn() <= lastCol) {

                    dvRegionsInRange.addCellRangeAddress(new CellRangeAddress(dvRegion.getFirstRow() - firstRow,
                            dvRegion.getLastRow() - firstRow,
                            dvRegion.getFirstColumn() - firstCol,
                            dvRegion.getLastColumn() - firstCol));
                }
            }

            if (dvRegionsInRange.countRanges() > 0) {
                dataValidations.add(poiSheet.getDataValidationHelper().createValidation(dvConstraint, dvRegionsInRange));
            }
        }

        //noinspection ToArrayCallWithZeroLengthArrayArgument
        this.dataValidations = dataValidations.toArray(new DataValidation[dataValidations.size()]);
    }

}
