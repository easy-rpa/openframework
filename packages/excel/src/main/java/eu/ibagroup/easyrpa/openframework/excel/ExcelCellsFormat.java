package eu.ibagroup.easyrpa.openframework.excel;

import eu.ibagroup.easyrpa.openframework.excel.internal.poi.POIElementsCache;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.ArrayList;
import java.util.List;

public class ExcelCellsFormat {

    private ExcelCellStyle[][] cellStyles;
    private CellRange[] mergedRegions;
    private int rowsCount;
    private int columnsCount;

    private DataValidation[] dataValidations;

    public ExcelCellsFormat(Sheet sheet) {
        this(sheet, 0, 0, sheet.getLastRowIndex(), sheet.getLastColumnIndex());
    }

    public ExcelCellsFormat(Sheet sheet, CellRange cellRange) {
        this(sheet, cellRange.getFirstRow(), cellRange.getFirstCol(), cellRange.getLastRow(), cellRange.getLastCol());
    }

    public ExcelCellsFormat(Column column) {
        this(column.getSheet(), column.getFirstRowIndex(), column.getIndex(), column.getLastRowIndex(), column.getIndex());
    }

    public ExcelCellsFormat(Column column, int firstRow, int lastRow) {
        this(column.getSheet(), firstRow, column.getIndex(), lastRow, column.getIndex());
    }

    public ExcelCellsFormat(Row row) {
        this(row.getSheet(), row.getIndex(), row.getFirstCellIndex(), row.getIndex(), row.getLastCellIndex());
    }

    public ExcelCellsFormat(Row row, int firstCol, int lastCol) {
        this(row.getSheet(), row.getIndex(), firstCol, row.getIndex(), lastCol);
    }

    public ExcelCellsFormat(Cell cell) {
        this(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex());
    }

    public ExcelCellsFormat(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        rowsCount = lastRow - firstRow + 1;
        columnsCount = lastCol - firstCol + 1;
        readMergedRegions(sheet, firstRow, firstCol, lastRow, lastCol);
        readCellStyles(sheet, firstRow, firstCol, lastRow, lastCol);
        readDataValidations(sheet, firstRow, firstCol, lastRow, lastCol);
    }

    public void applyTo(Sheet sheet) {
        applyTo(sheet, 0, 0, sheet.getLastRowIndex(), sheet.getLastColumnIndex());
    }

    public void applyTo(Sheet sheet, CellRange cellRange) {
        applyTo(sheet, cellRange.getFirstRow(), cellRange.getFirstCol(), cellRange.getLastRow(), cellRange.getLastCol());
    }

    public void applyTo(Column column) {
        applyTo(column.getSheet(), column.getFirstRowIndex(), column.getIndex(), column.getLastRowIndex(), column.getIndex());
    }

    public void applyTo(Row row, int firstCol, int lastCol) {
        applyTo(row.getSheet(), row.getIndex(), firstCol, row.getIndex(), lastCol);
    }

    public void applyTo(Row row) {
        applyTo(row.getSheet(), row.getIndex(), 0, row.getIndex(), row.getLastCellIndex());
    }

    public void applyTo(Column column, int firstRow, int lastRow) {
        applyTo(column.getSheet(), firstRow, column.getIndex(), lastRow, column.getIndex());
    }

    public void applyTo(Cell cell) {
        applyTo(cell.getSheet(), cell.getRowIndex(), cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex());
    }

    public void applyTo(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) {
        applyMergedRegions(sheet, firstRow, firstCol, lastRow, lastCol);
        applyCellStyles(sheet, firstRow, firstCol, lastRow, lastCol);
        applyDataValidations(sheet, firstRow, firstCol, lastRow, lastCol);
    }

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
