package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.helpers.ColumnHelper;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.apache.poi.ooxml.POIXMLTypeLoader.DEFAULT_XML_OPTIONS;

public class XSSFSheetExt extends XSSFSheet {

    protected XSSFSheetExt() {
        super();
    }

    /**
     * Creates an XSSFSheet representing the given package part and relationship.
     * Should only be called by XSSFWorkbook when reading in an existing file.
     *
     * @param part - The package part that holds xml data representing this sheet.
     * @since POI 3.14-Beta1
     */
    protected XSSFSheetExt(PackagePart part) {
        super(part);
    }

    protected void read(InputStream is) throws IOException {

        System.out.println("Read worksheet");



        try {
            worksheet = WorksheetDocument.Factory.parse(is, DEFAULT_XML_OPTIONS).getWorksheet();
        } catch (XmlException e) {
            throw new POIXMLException(e);
        }


        _initRows(worksheet);
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

        System.out.println("End read");
    }

//    /**
//     * Initialize worksheet data when creating a new sheet.
//     */
//    @Override
//    protected void onDocumentCreate(){
//        worksheet = newSheet();
//        initRows(worksheet);
//        columnHelper = new ColumnHelper(worksheet);
//        hyperlinks = new ArrayList<>();
//    }

    private void _initRows(CTWorksheet worksheetParam) {
//        _rows.clear();
        System.out.println("Init rows");
        TypeUtils.setFieldValue(this, "tables", new TreeMap<>());
        TypeUtils.setFieldValue(this, "sharedFormulas", new HashMap<>());
        TypeUtils.setFieldValue(this, "arrayFormulas", new ArrayList<>());
        for (CTRow row : worksheetParam.getSheetData().getRowArray()) {
            if(row.getR() % 10000 == 0 ){
                System.out.println(row.toString());
            }
//            XSSFRow r = new XSSFRow(row, this);
//            // Performance optimization: explicit boxing is slightly faster than auto-unboxing, though may use more memory
//            //noinspection UnnecessaryBoxing
//            final Integer rownumI = Integer.valueOf(r.getRowNum()); // NOSONAR
//            _rows.put(rownumI, r);
        }
    }
}
