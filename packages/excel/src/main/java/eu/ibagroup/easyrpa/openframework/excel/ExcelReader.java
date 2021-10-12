package eu.ibagroup.easyrpa.openframework.excel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelReader {
    private final String fileName;

    private String activeSheet;
    private Map<Integer, String> header;
    private long skipFirstNRows = 0;
    private boolean useFirstLineAsHeader = false;
    private Class<?> mapToClass;
    private final List<Map<String, String>> output = new ArrayList<>();
    private int activeSheetByIndex = -1;

    public ExcelReader(String fileName) {
        this.fileName = fileName;
    }

    public ExcelReader mapToClass(Class<?> clazz) {
        this.mapToClass = clazz;
        return this;
    }

    public ExcelReader skipFirstNRows(long number) {
        this.skipFirstNRows = number;
        return this;
    }

    public ExcelReader useFirstLineAsHeader() {
        this.useFirstLineAsHeader = true;
        header = null;
        return this;
    }

    public ExcelReader setHeader(ArrayList<String> header) {
        this.useFirstLineAsHeader = false;
        this.header = IntStream.range(0, header.size()).boxed().collect(Collectors.toMap(i -> i, header::get));
        return this;
    }

    public ExcelReader setActiveSheet(String activeSheet) {
        this.activeSheet = activeSheet;
        return this;
    }

    public ExcelReader setActiveSheetByIndex(int activeSheetByIndex) {
        this.activeSheetByIndex = activeSheetByIndex;
        return this;
    }

    private InputStream getSheet(XSSFReader xSsfReader) throws IOException, InvalidFormatException {
        InputStream inputStream;
        XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xSsfReader.getSheetsData();
        int idx = 0;
        while (iterator.hasNext()) {
            inputStream = iterator.next();
            if (null == activeSheet && activeSheetByIndex == -1)
                return inputStream;
            if (null == activeSheet && activeSheetByIndex == idx)
                return inputStream;
            if (null != activeSheet && iterator.getSheetName().contains(activeSheet))
                return inputStream;
            idx++;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getData() {
        List<T> result = new ArrayList<>();
        if (mapToClass != null) {
            Class<T> clazz = (Class<T>) mapToClass;
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            for (Map<String, String> m : output) {
                T object = mapper.convertValue(m, clazz);
                result.add(object);
            }
        } else {
            result = (List<T>) output;
        }
        return result;
    }

    public <T> List<T> parse() throws IOException, SAXException, OpenXML4JException, ParserConfigurationException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException();
        }
        try (OPCPackage opcPackage = OPCPackage.open(f.getPath(), PackageAccess.READ)) {
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opcPackage);
            XSSFReader xSsfReader = new XSSFReader(opcPackage);
            StylesTable styles = xSsfReader.getStylesTable();

            try (InputStream is = getSheet(xSsfReader)) {
                InputSource source = new InputSource(is);
                XMLReader sheetParser = XMLHelper.newXMLReader();
                ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, new CustomContentHandler(), false);
                sheetParser.setContentHandler(handler);
                sheetParser.parse(source);
            }
            return getData();
        }
    }

    protected class CustomContentHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private boolean skipRow;
        private boolean headerRow;
        private Map<Integer, String> row;
        private int currentRow = -1;
        private int minColumns = -1;

        private void addMissingRow(int num) {
            for (int i = 0; i < num; i++) {
                Map<String, String> dto = new LinkedHashMap<>();
                for (int j = 0; j < minColumns; j++) {
                    dto.put("" + j, "");
                }
                output.add(dto);
            }
        }

        @Override
        public void startRow(int rowNum) {
            addMissingRow(rowNum - currentRow - 1);
            currentRow = rowNum;
            if (rowNum < skipFirstNRows) {
                skipRow = true;
                return;
            } else if (skipFirstNRows == rowNum && useFirstLineAsHeader) {
                headerRow = true;
            }
            row = new TreeMap<>();
        }

        @Override
        public void endRow(int rowNum) {
            if (skipRow) {
                skipRow = false;
            } else if (headerRow) {
                headerRow = false;
                calcHeader();
            } else {
                applyData();
            }
        }

        private void applyData() {
            Map<String, String> dto = new LinkedHashMap<>();
            row.forEach((k, value) -> {
                String key = "" + k;
                if (null != header && header.containsKey(k)) {
                    key = header.get(k);
                }
                dto.put(key, value);
            });
            output.add(dto);
        }

        private void calcHeader() {
            header = row;
            minColumns = row.size();
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            if (!skipRow) {
                int colIdx = (new CellReference(cellReference)).getCol();
                row.put(colIdx, formattedValue);
            }
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
        }
    }
//    public static void main(String[] args) throws FileNotFoundException {
//
//
//        String file = "c:\\work\\1\\Example 3 week 24-26 2020 Softlaunch.xlsx";
////        String file = "c:\\work\\1\\[SE200] BFO lagersaldojustering 20200705 Verification 1020002057.xlsm";
//
//        List r = new ExcelReader(file)
////                .skipFirstNRows(12)
////                .setActiveSheet("Bokföringsorder")
////                .setHeader(new ArrayList<>(Arrays.asList("one", "two")))
//                .setActiveSheet("WORK.MEANSUMMARYSTATS")
//                .useFirstLineAsHeader()
////                .mapToClass(BaseRecord.class)
//                .parse();
//
//        System.out.println("" + r.size());
//   }
}
