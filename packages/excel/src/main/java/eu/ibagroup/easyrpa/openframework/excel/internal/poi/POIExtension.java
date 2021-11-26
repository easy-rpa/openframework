package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.xssf.usermodel.XSSFRelation;

public class POIExtension {

    private static boolean initialized;

    public static void init() {
        if (!initialized) {
            try {
                TypeUtils.setFieldValue(XSSFRelation.WORKSHEET,
                        "noArgConstructor", (POIXMLRelation.NoArgConstructor) XSSFSheetExt::new);
                TypeUtils.setFieldValue(XSSFRelation.WORKSHEET,
                        "packagePartConstructor", (POIXMLRelation.PackagePartConstructor) XSSFSheetExt::new);
                initialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
