package eu.ibagroup.easyrpa.openframework.googlesheets;

import java.util.HashMap;
import java.util.Map;

public class GSessionManager {
    private static Map<String, GSession> gSessions = new HashMap<>();

    public static boolean isSessionOpened(SpreadsheetDocument document) {
        return gSessions.containsKey(document.getId());
    }

    public static GSession getSession(SpreadsheetDocument document) {
        return gSessions.get(document.getId());
    }

    public static void openSession(SpreadsheetDocument document) {
        if (!gSessions.containsKey(document.getId())) {
            gSessions.put(document.getId(), new GSession());
        }
    }

    public static void closeSession(SpreadsheetDocument document) {
        gSessions.get(document.getId()).commit(document);
        gSessions.remove(document.getId());
    }


}
