package eu.easyrpa.openframework.word.internal.docx4j;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.util.HashMap;
import java.util.Map;

// TODO REMOVE THIS CLASS

public class Docx4jElementsCache {

    private static Docx4jElementsCache INSTANCE;
    private Map<Integer, WordprocessingMLPackage> mlPackageMap = new HashMap<>();

    private static Docx4jElementsCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Docx4jElementsCache();
        }
        return INSTANCE;
    }

    private Docx4jElementsCache() {
    }

    public static void register(int wordDocumentId, WordprocessingMLPackage mlPackage) {
        Docx4jElementsCache cache = getInstance();
        cache.mlPackageMap.put(wordDocumentId, mlPackage);
    }

    public static void unregister(int wordDocumentId) {
        Docx4jElementsCache cache = getInstance();
        cache.mlPackageMap.remove(wordDocumentId);
    }

    /**
     * @return unique Id for Word Document.
     */

    public static int generateWordDocumentId() {
        return Integer.parseInt((int) (Math.random() * 100) + "" + (System.currentTimeMillis() % 1000000));
    }
}
