package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;

public class SheetRowsProvider implements SheetRowsWriter {

    private static final Pattern ROW_NUM_REGEXP = Pattern.compile("\\sr=\"(\\d+)\"\\s");
    private static final Pattern ROW_OUTLINE_LEVEL_REGEXP = Pattern.compile("\\soutlineLevel=\"(\\d+)\"\\s");
    private static final Pattern CELL_REF_REGEXP = Pattern.compile("\\sr=\"([a-zA-Z]+\\d+)\"\\s");
    private static final String CELL_XML_START = "<c ";
    private static final String[] ROW_NS_APPENDER = new String[]{
            "<row",
            "<row xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" " +
                    "xmlns:x14ac=\"http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac\""
    };
    private static final XmlOptions ROW_SERIALIZATION_OPTIONS = new XmlOptions(DEFAULT_XML_OPTIONS);

    static {
        ROW_SERIALIZATION_OPTIONS.setSaveNoXmlDecl();
        Map<String, String> nsMap = new HashMap<>();
        nsMap.put("", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
        nsMap.put("x14ac", "http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac");
        ROW_SERIALIZATION_OPTIONS.setSaveImplicitNamespaces(nsMap);
    }

    private final XSSFSheet sheet;
    private final SortedMap<Integer, String> rowXMLs = new TreeMap<>();
    private final Map<Integer, XSSFRowExt> rowsCache = new HashMap<>(POISaveMemoryExtension.getRowsCacheMaxSize());
    private final LinkedList<Integer> rowsCacheQueue = new LinkedList<>();

    private CellRangeAddress sheetDimension;

    public SheetRowsProvider(XSSFSheet sheet, List<String> rowXMLs) {
        this.sheet = sheet;
        for (String rowXML : rowXMLs) {
            Matcher rowNumMatcher = ROW_NUM_REGEXP.matcher(rowXML);
            if (rowNumMatcher.find()) {
                this.rowXMLs.put(Integer.valueOf(rowNumMatcher.group(1)), rowXML);
            } else {
                throw new IllegalArgumentException("Attribute 'r' must be defined for row");
            }
        }
    }

    public XSSFRowExt getRow(int rowNum) {
        //noinspection UnnecessaryBoxing
        final Integer num = Integer.valueOf(rowNum);
        XSSFRowExt row = rowsCache.get(num);
        if (row == null) {
            String rowXml = rowXMLs.get(num);
            if (rowXml != null) {
                row = deserializeRow(rowXml);
                freeUpRowsCacheIfNeeded();
                rowsCache.put(num, row);
                rowsCacheQueue.add(num);
            }
        } else {
            rowsCacheQueue.remove(num);
            rowsCacheQueue.add(num);
        }
        return row;
    }

    public XSSFRowExt createRow(int rowNum) {
        removeRow(rowNum);
        XSSFRowExt r = new XSSFRowExt(CTRow.Factory.newInstance(), sheet);
        r.setRowNum(rowNum);
        rowXMLs.put(rowNum, serializeRow(r));
        freeUpRowsCacheIfNeeded();
        rowsCache.put(rowNum, r);
        rowsCacheQueue.add(rowNum);
        updateSheetDimension(true, false);
        return r;
    }

    public void removeRow(int rowNmm) {
        if (rowsCacheQueue.contains(rowNmm)) {
            rowsCacheQueue.remove(rowNmm);
            XSSFRowExt row = rowsCache.remove(rowNmm);
            row.setStale();
        }
        rowXMLs.remove(rowNmm);
        updateSheetDimension(true, false);
    }

    public Set<Integer> getRowNumbers() {
        return rowXMLs.keySet();
    }

    public int getRowsCount() {
        return rowXMLs.size();
    }

    public int getFirstRowNum() {
        return rowXMLs.isEmpty() ? -1 : rowXMLs.firstKey();
    }

    public int getLastRowNum() {
        return rowXMLs.isEmpty() ? -1 : rowXMLs.lastKey();
    }

    public CellRangeAddress getSheetDimension() {
        if (sheetDimension == null) {
            updateSheetDimension(true, true);
        }
        return sheetDimension;
    }

    public Iterator<Row> rowIterator() {
        return new RowIterator();
    }

    @Override
    public void writeRows(OutputStream out) throws IOException {
        while (rowsCacheQueue.size() > 0) {
            Integer num = rowsCacheQueue.pollFirst();
            XSSFRowExt row = rowsCache.remove(num);
            row.setStale();
            String rowXml = serializeRow(row);
            rowXMLs.put(num, rowXml);
        }

        for (String rowXml : rowXMLs.values()) {
            out.write(rowXml.getBytes());
        }
    }

    public void actualiseRowsOrder() {
        SortedMap<Integer, String> reorderedRowXMLs = new TreeMap<>();
        Map<Integer, XSSFRowExt> reorderedRowsCache = new HashMap<>(POISaveMemoryExtension.getRowsCacheMaxSize());
        List<Integer> reorderedRowsCacheQueue = new ArrayList<>(POISaveMemoryExtension.getRowsCacheMaxSize());

        for (Integer rowNum : rowXMLs.keySet()) {
            String rowXml = rowXMLs.get(rowNum);
            XSSFRowExt row = rowsCache.get(rowNum);
            if (row == null) {
                Matcher rowNumMatcher = ROW_NUM_REGEXP.matcher(rowXml);
                if (rowNumMatcher.find()) {
                    reorderedRowXMLs.put(Integer.valueOf(rowNumMatcher.group(1)), rowXml);
                }
            } else {
                Integer actualRowNum = Math.toIntExact(row.getCTRow().getR());
                reorderedRowXMLs.put(actualRowNum, rowXml);
                reorderedRowsCache.put(actualRowNum, row);
                reorderedRowsCacheQueue.add(actualRowNum);
            }
        }
        rowXMLs.clear();
        rowXMLs.putAll(reorderedRowXMLs);
        rowsCache.clear();
        rowsCache.putAll(reorderedRowsCache);
        rowsCacheQueue.clear();
        rowsCacheQueue.addAll(reorderedRowsCacheQueue);

        updateSheetDimension(true, false);
    }

    public short getMaxOutlineLevelRows() {
        int outlineLevel = 0;
        for (Integer rowNum : rowXMLs.keySet()) {
            String rowXml = rowXMLs.get(rowNum);
            XSSFRowExt row = rowsCache.get(rowNum);
            if (row == null) {
                Matcher rowOutLevelMatcher = ROW_OUTLINE_LEVEL_REGEXP.matcher(rowXml);
                if (rowOutLevelMatcher.find()) {
                    outlineLevel = Math.max(outlineLevel, Integer.parseInt(rowOutLevelMatcher.group(1)));
                }
            } else {
                outlineLevel = Math.max(outlineLevel, row.getCTRow().getOutlineLevel());
            }
        }
        return (short) outlineLevel;
    }

    protected void updateSheetDimension(boolean updateRows, boolean updateColumns) {
        if (sheetDimension == null) {
            sheetDimension = new CellRangeAddress(-1, -1, -1, -1);
        }
        if (updateRows) {
            sheetDimension.setFirstRow(getFirstRowNum());
            sheetDimension.setLastRow(getLastRowNum());
        }
        if (updateColumns) {
            int minColNum = Integer.MAX_VALUE;
            int maxColNum = -1;
            for (Integer rowNum : rowXMLs.keySet()) {
                XSSFRow row = rowsCache.get(rowNum);
                if (row == null) {
                    String rowXml = rowXMLs.get(rowNum);
                    Matcher cellRefMatcher = CELL_REF_REGEXP.matcher(rowXml);
                    if (cellRefMatcher.find()) {
                        CellAddress cellRef = new CellAddress(cellRefMatcher.group(1));
                        minColNum = Math.min(minColNum, cellRef.getColumn());
                    }
                    int lastCellStart = rowXml.lastIndexOf(CELL_XML_START);
                    if (lastCellStart > 0 && cellRefMatcher.find(lastCellStart)) {
                        CellAddress cellRef = new CellAddress(cellRefMatcher.group(1));
                        maxColNum = Math.max(maxColNum, cellRef.getColumn());
                    }
                } else {
                    minColNum = Math.min(minColNum, row.getFirstCellNum());
                    maxColNum = Math.max(maxColNum, row.getLastCellNum());
                }
            }
            if (minColNum != Integer.MAX_VALUE) {
                sheetDimension.setFirstColumn(minColNum);
                sheetDimension.setLastColumn(maxColNum);
            }
        }
    }

    private void freeUpRowsCacheIfNeeded() {
        if (rowsCacheQueue.size() == POISaveMemoryExtension.getRowsCacheMaxSize()) {
            Integer num = rowsCacheQueue.pollFirst();
            XSSFRowExt row = rowsCache.remove(num);
            row.setStale();
            String rowXml = serializeRow(row);
            rowXMLs.put(num, rowXml);
        }
    }

    private String serializeRow(XSSFRowExt row) {
        try {
            // first perform the normal write actions for the row
            TypeUtils.callMethod(row, "onDocumentWrite");

            try (ByteArrayOutputStream rowOut = new ByteArrayOutputStream()) {
                row.getCTRow().save(rowOut, ROW_SERIALIZATION_OPTIONS);
                return rowOut.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("Row serialization has failed. Row num: '%s'", row.getRowNum()), e);
        }
    }

    private XSSFRowExt deserializeRow(String rowXml) {
        try {
            rowXml = rowXml.replace(ROW_NS_APPENDER[0], ROW_NS_APPENDER[1]);
            CTRow ctRow = CTSheetData.Factory.parse(rowXml, DEFAULT_XML_OPTIONS).getRowArray(0);
            return new XSSFRowExt(ctRow, sheet);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Row deserialization has failed. Row XML: %s", rowXml), e);
        }
    }

    private class RowIterator implements Iterator<Row> {

        private Iterator<Integer> rowNumbersIterator;

        public RowIterator() {
            this.rowNumbersIterator = rowXMLs.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return rowNumbersIterator.hasNext();
        }

        @Override
        public Row next() {
            return getRow(rowNumbersIterator.next());
        }
    }
}
