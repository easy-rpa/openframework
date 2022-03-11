package eu.easyrpa.openframework.excel.internal.poi;

import eu.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.helpers.ColumnHelper;
import org.apache.poi.xssf.usermodel.helpers.XSSFColumnShifter;
import org.apache.poi.xssf.usermodel.helpers.XSSFRowShifter;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

import static org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;

public class XSSFSheetExt extends XSSFSheet {

    private SheetRowsProvider rowsProvider;

    protected XSSFSheetExt() {
        super();
    }

    protected XSSFSheetExt(PackagePart part) {
        super(part);
    }

    protected SheetRowsProvider getRowsProvider() {
        return rowsProvider;
    }

    protected void read(InputStream is) throws IOException {
        StringBuilder worksheetXML = new StringBuilder(8192);
        List<String> rowXMLs = new ArrayList<>();

        extractRowsFromInput(is, worksheetXML, rowXMLs);

        rowsProvider = new SheetRowsProvider(this, rowXMLs);

        try {
            worksheet = WorksheetDocument.Factory.parse(worksheetXML.toString(), DEFAULT_XML_OPTIONS).getWorksheet();
            worksheet.setSheetData(CTSheetData.Factory.newInstance(DEFAULT_XML_OPTIONS));
            worksheet.getSheetData().addNewRow();
        } catch (XmlException e) {
            throw new POIXMLException(e);
        }

        TypeUtils.setFieldValue(this, "tables", new TreeMap<>());
        TypeUtils.setFieldValue(this, "sharedFormulas", new HashMap<>());
        TypeUtils.setFieldValue(this, "arrayFormulas", new ArrayList<>());
        TypeUtils.setFieldValue(this, "columnHelper", new ColumnHelper(worksheet));

        SortedMap<String, XSSFTable> tables = TypeUtils.getFieldValue(this, "tables", false);
        // Look for bits we're interested in
        for (RelationPart rp : getRelationParts()) {
            POIXMLDocumentPart p = rp.getDocumentPart();
            if (p instanceof CommentsTable) {
                TypeUtils.setFieldValue(this, "sheetComments", p);
            }
            if (p instanceof XSSFTable) {
                tables.put(rp.getRelationship().getId(), (XSSFTable) p);
            }
            if (p instanceof XSSFPivotTable) {
                getWorkbook().getPivotTables().add((XSSFPivotTable) p);
            }
        }

        // Process external hyperlinks for the sheet, if there are any
        TypeUtils.callMethod(this, "initHyperlinks");
    }

    @Override
    protected void onDocumentCreate() {
        worksheet = TypeUtils.callMethod(this, "newSheet");
        worksheet.setSheetData(CTSheetData.Factory.newInstance(DEFAULT_XML_OPTIONS));
        worksheet.getSheetData().addNewRow();
        TypeUtils.setFieldValue(this, "tables", new TreeMap<>());
        TypeUtils.setFieldValue(this, "sharedFormulas", new HashMap<>());
        TypeUtils.setFieldValue(this, "arrayFormulas", new ArrayList<>());
        TypeUtils.setFieldValue(this, "columnHelper", new ColumnHelper(worksheet));
        TypeUtils.setFieldValue(this, "hyperlinks", new ArrayList<>());
    }

    @Override
    public XSSFRow createRow(int rowNum) {
        return rowsProvider.createRow(rowNum);
    }

    @Override
    public int getFirstRowNum() {
        return rowsProvider.getFirstRowIndex();
    }

    @Override
    public int getLastRowNum() {
        return rowsProvider.getLastRowIndex();
    }

    @Override
    public int getPhysicalNumberOfRows() {
        return rowsProvider.getRowsCount();
    }

    @Override
    public XSSFRow getRow(int rowNum) {
        return rowsProvider.getRow(rowNum);
    }

    @Override
    public void removeRow(Row row) {
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        int rowNum = row.getRowNum();
        rowsProvider.removeRow(rowNum);
        // also remove any comment located in that row
        final CommentsTable sheetComments = TypeUtils.getFieldValue(this, "sheetComments");
        if (sheetComments != null) {
            for (Iterator<CellAddress> it = sheetComments.getCellAddresses(); it.hasNext(); ) {
                CellAddress address = it.next();
                if (address.getRow() == rowNum) {
                    sheetComments.removeComment(address);
                }
            }
        }
    }

    @Override
    public Iterator<Row> rowIterator() {
        return rowsProvider.rowIterator();
    }

    @Override
    public void groupRow(int fromRow, int toRow) {
        for (int i = fromRow; i <= toRow; i++) {
            XSSFRow xrow = getRow(i);
            if (xrow == null) {
                xrow = createRow(i);
            }
            CTRow ctrow = xrow.getCTRow();
            short outlineLevel = ctrow.getOutlineLevel();
            ctrow.setOutlineLevel((short) (outlineLevel + 1));
        }
        _setSheetFormatPrOutlineLevelRow();
    }

    @Override
    public void ungroupRow(int fromRow, int toRow) {
        for (int i = fromRow; i <= toRow; i++) {
            XSSFRow xrow = getRow(i);
            if (xrow != null) {
                CTRow ctRow = xrow.getCTRow();
                int outlineLevel = ctRow.getOutlineLevel();
                ctRow.setOutlineLevel((short) (outlineLevel - 1));
                //remove a row only if the row has no cell and if the outline level is 0
                if (outlineLevel == 1 && xrow.getFirstCellNum() == -1) {
                    removeRow(xrow);
                }
            }
        }
        _setSheetFormatPrOutlineLevelRow();
    }

    @Override
    public void shiftRows(int startRow, int endRow, final int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {
        XSSFVMLDrawing vml = getVMLDrawing(false);

        int sheetIndex = getWorkbook().getSheetIndex(this);
        String sheetName = getWorkbook().getSheetName(sheetIndex);
        FormulaShifter formulaShifter = FormulaShifter.createForRowShift(
                sheetIndex, sheetName, startRow, endRow, n, SpreadsheetVersion.EXCEL2007);

        _removeOverwritten(vml, startRow, endRow, n);
        _shiftCommentsAndRows(vml, startRow, endRow, n);

        new XSSFRowShifter(this).shiftMergedRegions(startRow, endRow, n);

        XSSFRowColExtShifter.updateNamedRanges(this, formulaShifter);
        XSSFRowColExtShifter.updateFormulas(this, formulaShifter);
        XSSFRowColExtShifter.updateConditionalFormatting(this, formulaShifter);
        XSSFRowColExtShifter.updateHyperlinks(this, formulaShifter);
    }

    @Override
    public void shiftColumns(int startColumn, int endColumn, final int n) {
        XSSFVMLDrawing vml = getVMLDrawing(false);

        _shiftCommentsForColumns(vml, startColumn, endColumn, n);

        FormulaShifter formulaShifter = FormulaShifter.createForColumnShift(
                this.getWorkbook().getSheetIndex(this), this.getSheetName(),
                startColumn, endColumn, n,
                SpreadsheetVersion.EXCEL2007);

        XSSFColumnShifter columnShifter = new XSSFColumnShifter(this);
        columnShifter.shiftColumns(startColumn, endColumn, n);
        columnShifter.shiftMergedRegions(startColumn, endColumn, n);

        XSSFRowColExtShifter.updateFormulas(this, formulaShifter);
        XSSFRowColExtShifter.updateConditionalFormatting(this, formulaShifter);
        XSSFRowColExtShifter.updateHyperlinks(this, formulaShifter);
        XSSFRowColExtShifter.updateNamedRanges(this, formulaShifter);
    }

    public CellRangeAddress getSheetDimension() {
        return rowsProvider.getSheetDimension();
    }

    protected void write(OutputStream out) throws IOException {
        boolean setToNull = false;
        if (worksheet.sizeOfColsArray() == 1) {
            CTCols col = worksheet.getColsArray(0);
            if (col.sizeOfColArray() == 0) {
                setToNull = true;
                // this is necessary so that we do not write an empty <cols/> item into the sheet-xml in the xlsx-file
                // Excel complains about a corrupted file if this shows up there!
                worksheet.setColsArray(null);
            } else {
                TypeUtils.callMethod(this, "setColWidthAttribute", col);
            }
        }

        // Now re-generate our CTHyperlinks, if needed
        List<XSSFHyperlink> hyperlinks = TypeUtils.getFieldValue(this, "hyperlinks");
        if (hyperlinks.size() > 0) {
            if (worksheet.getHyperlinks() == null) {
                worksheet.addNewHyperlinks();
            }
            CTHyperlink[] ctHls = new CTHyperlink[hyperlinks.size()];
            for (int i = 0; i < ctHls.length; i++) {
                // If our sheet has hyperlinks, have them add
                //  any relationships that they might need
                XSSFHyperlink hyperlink = hyperlinks.get(i);
                TypeUtils.callMethod(hyperlink, "generateRelationIfNeeded", getPackagePart());
                // Now grab their underling object
                ctHls[i] = hyperlink.getCTHyperlink();
            }
            worksheet.getHyperlinks().setHyperlinkArray(ctHls);
        } else {
            if (worksheet.getHyperlinks() != null) {
                final int count = worksheet.getHyperlinks().sizeOfHyperlinkArray();
                for (int i = count - 1; i >= 0; i--) {
                    worksheet.getHyperlinks().removeHyperlink(i);
                }
                // For some reason, we have to remove the hyperlinks one by one from the CTHyperlinks array
                // before unsetting the hyperlink array.
                // Resetting the hyperlink array seems to break some XML nodes.
                //worksheet.getHyperlinks().setHyperlinkArray(new CTHyperlink[0]);
                worksheet.unsetHyperlinks();
            } /*else {
                // nothing to do
            }*/
        }

        // finally, if we had at least one cell we can populate the optional dimension-field
        CellRangeAddress sheetDimension = rowsProvider.getSheetDimension();
        if (sheetDimension != null) {
            String ref = sheetDimension.formatAsString();
            if (worksheet.isSetDimension()) {
                worksheet.getDimension().setRef(ref);
            } else {
                worksheet.addNewDimension().setRef(ref);
            }
        }

        XmlOptions xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTWorksheet.type.getName().getNamespaceURI(), "worksheet"));

        worksheet.save(new WorksheetOutputStream(out, rowsProvider), xmlOptions);

        // Bug 52233: Ensure that we have a col-array even if write() removed it
        if (setToNull) {
            worksheet.addNewCols();
        }
    }

    private void _setSheetFormatPrOutlineLevelRow() {
        short maxLevelRow = rowsProvider.getMaxOutlineLevelRows();
        CTSheetFormatPr ctSheetFormatPr = TypeUtils.callMethod(this, "getSheetTypeSheetFormatPr");
        ctSheetFormatPr.setOutlineLevelRow(maxLevelRow);
    }

    private void _shiftCommentsAndRows(XSSFVMLDrawing vml, int startRow, int endRow, final int n) {
        // then do the actual moving and also adjust comments/rowHeight
        // we need to sort it in a way so the shifting does not mess up the structures,
        // i.e. when shifting down, start from down and go up, when shifting up, vice-versa
        SortedMap<XSSFComment, Integer> commentsToShift = new TreeMap<>((o1, o2) -> {
            int row1 = o1.getRow();
            int row2 = o2.getRow();

            if (row1 == row2) {
                // ordering is not important when row is equal, but don't return zero to still
                // get multiple comments per row into the map
                return o1.hashCode() - o2.hashCode();
            }

            // when shifting down, sort higher row-values first
            if (n > 0) {
                return row1 < row2 ? 1 : -1;
            } else {
                // sort lower-row values first when shifting up
                return row1 > row2 ? 1 : -1;
            }
        });

        final CommentsTable sheetComments = TypeUtils.getFieldValue(this, "sheetComments");
        for (Integer rowNum : rowsProvider.getRowNumbers()) {
            if (sheetComments != null) {
                // calculate the new rowNum
                int newRowNum = TypeUtils.callMethod(this, "shiftedRowNum", startRow, endRow, n, rowNum);

                // is there a change necessary for the current row?
                if (newRowNum != rowNum) {
                    CTCommentList lst = sheetComments.getCTComments().getCommentList();
                    for (CTComment comment : lst.getCommentArray()) {
                        String oldRef = comment.getRef();
                        CellReference ref = new CellReference(oldRef);

                        // is this comment part of the current row?
                        if (ref.getRow() == rowNum) {
                            XSSFComment xssfComment = new XSSFComment(sheetComments, comment,
                                    vml == null ? null : vml.findCommentShape(rowNum, ref.getCol()));

                            // we should not perform the shifting right here as we would then find
                            // already shifted comments and would shift them again...
                            commentsToShift.put(xssfComment, newRowNum);
                        }
                    }
                }
            }

            if (rowNum < startRow || rowNum > endRow) {
                continue;
            }
            rowsProvider.getRow(rowNum).shift(n);
        }

        // adjust all the affected comment-structures now
        // the Map is sorted and thus provides them in the order that we need here,
        // i.e. from down to up if shifting down, vice-versa otherwise
        for (Map.Entry<XSSFComment, Integer> entry : commentsToShift.entrySet()) {
            entry.getKey().setRow(entry.getValue());
        }

        rowsProvider.actualiseRowsOrder();
    }

    private void _shiftCommentsForColumns(XSSFVMLDrawing vml, int startColumnIndex, int endColumnIndex, final int n) {
        // then do the actual moving and also adjust comments/rowHeight
        // we need to sort it in a way so the shifting does not mess up the structures,
        // i.e. when shifting down, start from down and go up, when shifting up, vice-versa
        SortedMap<XSSFComment, Integer> commentsToShift = new TreeMap<>((o1, o2) -> {
            int column1 = o1.getColumn();
            int column2 = o2.getColumn();

            if (column1 == column2) {
                // ordering is not important when row is equal, but don't return zero to still
                // get multiple comments per row into the map
                return o1.hashCode() - o2.hashCode();
            }

            // when shifting down, sort higher row-values first
            if (n > 0) {
                return column1 < column2 ? 1 : -1;
            } else {
                // sort lower-row values first when shifting up
                return column1 > column2 ? 1 : -1;
            }
        });

        final CommentsTable sheetComments = TypeUtils.getFieldValue(this, "sheetComments");
        if (sheetComments != null) {
            CTCommentList lst = sheetComments.getCTComments().getCommentList();
            for (CTComment comment : lst.getCommentArray()) {
                String oldRef = comment.getRef();
                CellReference ref = new CellReference(oldRef);

                int columnIndex = ref.getCol();
                int newColumnIndex = TypeUtils.callMethod(this, "shiftedRowNum", startColumnIndex, endColumnIndex, n, columnIndex);
                if (newColumnIndex != columnIndex) {
                    XSSFComment xssfComment = new XSSFComment(sheetComments, comment,
                            vml == null ? null : vml.findCommentShape(ref.getRow(), columnIndex));
                    commentsToShift.put(xssfComment, newColumnIndex);
                }
            }
        }
        // adjust all the affected comment-structures now
        // the Map is sorted and thus provides them in the order that we need here,
        // i.e. from down to up if shifting down, vice-versa otherwise
        for (Map.Entry<XSSFComment, Integer> entry : commentsToShift.entrySet()) {
            entry.getKey().setColumn(entry.getValue());
        }

        rowsProvider.resetSheetDimension();
    }

    // remove all rows which will be overwritten
    private void _removeOverwritten(XSSFVMLDrawing vml, int startRow, int endRow, final int n) {

        List<Integer> removedRows = new ArrayList<>();
        for (Integer rowNum : rowsProvider.getRowNumbers()) {
            // check if we should remove this row as it will be overwritten by the data later
            boolean shouldRemoveRow = _shouldRemoveRow(startRow, endRow, n, rowNum);
            if (shouldRemoveRow) {
                rowsProvider.removeRow(rowNum);
                removedRows.add(rowNum);
            }
        }

        // also remove any comments associated with this row
        final CommentsTable sheetComments = TypeUtils.getFieldValue(this, "sheetComments");
        if (sheetComments != null) {
            CTCommentList lst = sheetComments.getCTComments().getCommentList();
            for (CTComment comment : lst.getCommentArray()) {
                String strRef = comment.getRef();
                CellAddress ref = new CellAddress(strRef);

                // is this comment part of the current row?
                if (removedRows.contains(ref.getRow())) {
                    sheetComments.removeComment(ref);
                    TypeUtils.callMethod(vml, "removeCommentShape", ref.getRow(), ref.getColumn());
                }
            }
        }

        // also remove any hyperlinks associated with this row
        List<XSSFHyperlink> hyperlinks = TypeUtils.getFieldValue(this, "hyperlinks");
        if (hyperlinks != null) {
            for (XSSFHyperlink link : new ArrayList<>(hyperlinks)) {
                CellReference ref = new CellReference(link.getCellRef());
                if (removedRows.contains(ref.getRow())) {
                    hyperlinks.remove(link);
                }
            }
        }
    }

    private boolean _shouldRemoveRow(int startRow, int endRow, int n, int rowNum) {
        // is this row in the target-window where the moved rows will land?
        if (rowNum >= (startRow + n) && rowNum <= (endRow + n)) {
            // only remove it if the current row is not part of the data that is copied
            if (n > 0 && rowNum > endRow) {
                return true;
            } else {
                return n < 0 && rowNum < startRow;
            }
        }
        return false;
    }

    private void extractRowsFromInput(InputStream is, StringBuilder worksheetXML, List<String> rowXMLs) throws IOException {
        int readAmount;
        int capacity = 4096;
        char[] buf = new char[capacity];
        boolean sheetDataReading = false;
        StringBuilder chunk = new StringBuilder(capacity * 2);
        StringBuilder rowXML = new StringBuilder();

        try (InputStreamReader reader = new InputStreamReader(is)) {
            while ((readAmount = reader.read(buf, 0, capacity)) > 0) {
                chunk.append(buf, 0, readAmount);
                if (!sheetDataReading) {
                    int sheetDataStart = chunk.indexOf("<sheetData>");
                    if (sheetDataStart >= 0) {
                        worksheetXML.append(chunk, 0, sheetDataStart);
                        chunk.delete(0, sheetDataStart + "<sheetData>".length());
                        sheetDataReading = true;
                    }
                }

                if (sheetDataReading) {
                    int sheetDataEnd = chunk.lastIndexOf("</sheetData>");
                    if (sheetDataEnd >= 0) {
                        worksheetXML.append(chunk, sheetDataEnd + "</sheetData>".length(), chunk.length());
                        chunk.delete(sheetDataEnd, chunk.length());
                        sheetDataReading = false;
                    }
                    int rowStart;
                    while ((rowStart = chunk.indexOf("<row")) >= 0) {
                        int rowEnd = chunk.indexOf("</row>", rowStart);
                        if (rowEnd > 0) {
                            rowXML.append(chunk, rowStart, rowEnd + "</row>".length());
                            chunk.delete(0, rowEnd + "</row>".length());
                            rowXMLs.add(rowXML.toString());
                            rowXML.setLength(0);
                        } else {
                            break;
                        }
                    }
                } else {
                    worksheetXML.append(chunk);
                    chunk.setLength(0);
                }
            }
        }
    }
}
