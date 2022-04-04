package eu.easyrpa.openframework.excel.internal.poi;

import eu.easyrpa.openframework.core.utils.TypeUtils;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRelation;

public class POISaveMemoryExtension {

    private static final int BYTE_ARRAY_MAX_SIZE_FOR_POI = Integer.MAX_VALUE;

    private static boolean initialized;

    private static int rowsCacheMaxSize = 1000;

    public static void init() {
        if (!initialized) {
            try {
                IOUtils.setByteArrayMaxOverride(BYTE_ARRAY_MAX_SIZE_FOR_POI);
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

    public static boolean isInitialized() {
        return initialized;
    }

    public static int getRowsCacheMaxSize() {
        return rowsCacheMaxSize;
    }

    public static void setRowsCacheMaxSize(int rowsCacheMaxSize) {
        POISaveMemoryExtension.rowsCacheMaxSize = rowsCacheMaxSize;
    }
}
